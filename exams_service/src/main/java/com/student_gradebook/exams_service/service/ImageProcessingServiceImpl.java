package com.student_gradebook.exams_service.service;

import com.student_gradebook.exams_service.controller.exceptions.FileProcessingException;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {
    private final int MIN_COUNTOUR_AREA = 100;
    private final double TRIANGLE_TOLERANCE = 0.25;
    private final int[] SQUARE_TOLERANCE = {70, 110};

    @Override
    public byte[] process(byte[] input) {
        Mat img = Imgcodecs.imdecode(new MatOfByte(input), Imgcodecs.IMREAD_COLOR);
        if (img.empty()) {
            throw new FileProcessingException("Could not decode image!");
        }

        img = enhanceRedInk(img);
        img = correctOrientation(img);

        MatOfByte out = new MatOfByte();
        Imgcodecs.imencode(".png", img, out);
        return out.toArray();
    }

    private Mat enhanceRedInk(Mat img) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV); // Convert image from BGR to HSV for easier color filtering
        // HSV is a color model that represents colors by Hue (color type), Saturation (color intensity), and Value (brightness).

        // Define red color ranges in HSV
        Scalar lowerRed1 = new Scalar(0, 35, 30);
        Scalar upperRed1 = new Scalar(12, 255, 255);
        Scalar lowerRed2 = new Scalar(168, 35, 30);
        Scalar upperRed2 = new Scalar(180, 255, 255);

        Mat mask1 = new Mat();
        Mat mask2 = new Mat();
        Core.inRange(hsv, lowerRed1, upperRed1, mask1); // Mask pixels in the first red range
        Core.inRange(hsv, lowerRed2, upperRed2, mask2); // Mask pixels in the second red range

        Mat redMask = new Mat();
        Core.bitwise_or(mask1, mask2, redMask); // Combine both masks to cover all red pixels

        Mat result = img.clone(); // Create output image
        Mat redPixels = new Mat();
        img.copyTo(redPixels, redMask); // Copy only the red pixels from original image

        Core.multiply(redPixels, new Scalar(0.2, 0.2, 0.2), redPixels); // Darken the red pixels (multiply RGB channels)
        redPixels.copyTo(result, redMask); // Place the darkened red pixels back into the output image

        return result; // Return image with red areas enhanced/darkened
    }

    private Mat correctOrientation(Mat img) {
        int h = img.rows();
        int w = img.cols();
        Point center = new Point(w / 2.0, h / 2.0);

        Point[] centers = findShapeCenters(img);
        Point square = centers[0];
        Point triangle = centers[1];

        if (square == null || triangle == null) {
            return img; // Can't determine rotation without both shapes
        }

        double dx = square.x - triangle.x;
        double dy = square.y - triangle.y;

        // Decide rotation based on relative positions of square and triangle
        double angle;
        if (Math.abs(dx) > Math.abs(dy)) {
            angle = dx < 0 ? 180 : 0;
        } else {
            angle = dy < 0 ? -90 : 90;
        }

        Mat rotMat = Imgproc.getRotationMatrix2D(center, angle, 1.0);
        Mat rotated = new Mat();

        // apply a linear transformation (rotate, translate, scale) to an image.
        Imgproc.warpAffine(img, rotated, rotMat, new Size(w, h));
        return rotated;
    }

    private Point[] findShapeCenters(Mat img) {
        Mat thresh = preprocess(img); // Convert image to binary mask for shape detection

        // Find all external contours in the binary image
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Point squareCenter = null;
        Point triangleCenter = null;

        for (MatOfPoint cnt : contours) {
            if (Imgproc.contourArea(cnt) < MIN_COUNTOUR_AREA) continue; // Ignore very small shapes

            // Approximate contour to a polygon with fewer vertices
            double peri = Imgproc.arcLength(new MatOfPoint2f(cnt.toArray()), true);
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(cnt.toArray()), approx, 0.04 * peri, true);

            // Compute contour centroid using image moments
            Moments m = Imgproc.moments(cnt);
            if (m.m00 == 0) continue; // Avoid division by zero

            Point center = new Point(m.m10 / m.m00, m.m01 / m.m00);

            // Check if the polygon is an equilateral triangle
            if (approx.total() == 3 && isEquilateralTriangle(approx.toArray())) {
                triangleCenter = center;
            }


            // Check if the polygon is a square
            if (approx.total() == 4 && isSquare(approx.toArray())) {
                squareCenter = center;
            }
        }

        return new Point[]{squareCenter, triangleCenter};
    }

    private Mat preprocess(Mat img) {
        Mat gray = new Mat();
        Mat blur = new Mat();
        Mat thresh = new Mat();
        Mat morph = new Mat();

        // Convert the image to grayscale – simplifies processing by removing color
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply Gaussian blur to reduce noise and smooth edges
        Imgproc.GaussianBlur(gray, blur, new Size(5, 5), 0);

        // Adaptive threshold to handle uneven lighting
        Imgproc.adaptiveThreshold(
                blur,
                thresh,
                255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY_INV,
                11,
                2
        );

        // Close small gaps in contours
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(thresh, morph, Imgproc.MORPH_CLOSE, kernel);

        // Return processed binary image ready for contour detection
        return morph;
    }

    private boolean isEquilateralTriangle(Point[] pts) {
        double[] d = new double[3];
        for (int i = 0; i < 3; i++) {
            Point p1 = pts[i];
            Point p2 = pts[(i + 1) % 3];
            d[i] = Math.hypot(p1.x - p2.x, p1.y - p2.y);
        }
        return max(d) / min(d) <= 1.0 + TRIANGLE_TOLERANCE; // Sides must be roughly equal
    }

    private boolean isSquare(Point[] pts) {
        double[] d = new double[4];
        for (int i = 0; i < 4; i++) {
            d[i] = norm(pts[i], pts[(i + 1) % 4]);
        }

        if (max(d) / min(d) > 1.30) return false;

        for (int i = 0; i < 4; i++) {
            double a = calculateAngle(pts[(i + 3) % 4], pts[i], pts[(i + 1) % 4]);
            if (a < SQUARE_TOLERANCE[0] || a > SQUARE_TOLERANCE[1]) return false;
        }

        return true;
    }

    private double calculateAngle(Point p1, Point p2, Point p3) {
        // Create vectors from the central point (p2) to the other two points
        double[] v1 = {p1.x - p2.x, p1.y - p2.y};
        double[] v2 = {p3.x - p2.x, p3.y - p2.y};

        // Compute the dot product of the two vectors
        double dot = v1[0] * v2[0] + v1[1] * v2[1];

        // Compute the product of the vectors' magnitudes
        double mag = Math.hypot(v1[0], v1[1]) * Math.hypot(v2[0], v2[1]);

        // Calculate cosine of the angle
        double cos = dot / mag;

        // Clamp the value to [-1, 1] to avoid NaN due to floating point errors
        cos = Math.max(-1, Math.min(1, cos));

        // Return angle in degrees
        return Math.toDegrees(Math.acos(cos));
    }


    private double norm(Point a, Point b) {
        // Return straight line distance between two points
        return Math.hypot(a.x - b.x, a.y - b.y);
    }

    private double max(double[] v) {
        return Arrays.stream(v).max().orElse(0);
    }

    private double min(double[] v) {
        return Arrays.stream(v).min().orElse(1);
    }
}
