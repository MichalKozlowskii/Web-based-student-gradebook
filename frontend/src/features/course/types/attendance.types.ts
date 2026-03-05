export type AttendanceStatus = "PRESENT" | "ABSENT" | "EXCUSED";

export interface Attendance {
  id: string;
  studentId: string;
  status: AttendanceStatus;
  lectureId: string;
  createdAt: string;
  lastUpdated: string;
}

export interface LecturePreview {
  id: string;
  courseUnitId: string;
  groupNumber: number;
  createdAt: string;
  lastUpdated: string;
}

export interface Lecture {
  id: string;
  courseUnitId: string;
  groupNumber: number;
  attendanceList: Attendance[];
  termId: string;
  createdAt: string;
  lastUpdated: string;
}
