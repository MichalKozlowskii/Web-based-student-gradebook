import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Navbar } from "@/components/Navbar";
import {
  CourseTabs,
  StudentGradesTab,
  StudentAttendanceTab,
  StudentSummaryTab,
  useCourseDetails,
  useStudentGrades,
  useStudentAttendance,
  useStudentSummary,
  type TabType,
} from "@/features/course";

export const CoursePerformancePage: React.FC = () => {
  const { courseUnitId } = useParams<{ courseUnitId: string }>();
  const [activeTab, setActiveTab] = useState<TabType>("grades");

  const { course, loading } = useCourseDetails(courseUnitId);
  const grades = useStudentGrades(courseUnitId);
  const attendance = useStudentAttendance(courseUnitId);
  const summary = useStudentSummary(courseUnitId);

  // Lazy-load tab data
  const { fetchGrades, gradesAttempted } = grades;
  const { fetchAttendance, attendanceAttempted } = attendance;
  const { fetchSummary, summaryAttempted } = summary;

  useEffect(() => {
    if (!courseUnitId) return;

    if (activeTab === "grades" && !gradesAttempted) {
      fetchGrades();
    } else if (activeTab === "attendance" && !attendanceAttempted) {
      fetchAttendance();
    } else if (activeTab === "summary" && !summaryAttempted) {
      fetchSummary();
    }
  }, [
    activeTab,
    courseUnitId,
    gradesAttempted,
    attendanceAttempted,
    summaryAttempted,
    fetchGrades,
    fetchAttendance,
    fetchSummary,
  ]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar showBackButton={true} />
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
            <span className="ml-3 text-gray-600">Ładowanie...</span>
          </div>
        </main>
      </div>
    );
  }

  if (!course) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar showBackButton={true} />
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-red-700">
            Nie znaleziono przedmiotu
          </div>
        </main>
      </div>
    );
  }

  const renderContent = () => {
    switch (activeTab) {
      case "grades":
        return (
          <StudentGradesTab
            grades={grades.grades}
            gradesLoading={grades.gradesLoading}
            gradesError={grades.gradesError}
          />
        );
      case "attendance":
        return (
          <StudentAttendanceTab
            attendance={attendance.attendance}
            attendanceLoading={attendance.attendanceLoading}
            attendanceError={attendance.attendanceError}
          />
        );
      case "summary":
        return (
          <StudentSummaryTab
            summary={summary.summary}
            summaryLoading={summary.summaryLoading}
            summaryError={summary.summaryError}
          />
        );
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar showBackButton={true} />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Course Info Card */}
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 bg-indigo-600 rounded-lg flex items-center justify-center">
              <svg
                className="w-7 h-7 text-white"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                />
              </svg>
            </div>
            <div>
              <h2 className="text-xl font-semibold text-gray-900">
                {course.courseName}
              </h2>
              <p className="text-sm text-gray-600">
                Grupa {course.groupNumber}
              </p>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <CourseTabs activeTab={activeTab} onTabChange={setActiveTab}>
          <div className="p-6">{renderContent()}</div>
        </CourseTabs>
      </main>
    </div>
  );
};
