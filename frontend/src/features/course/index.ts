// Types (shared)
export type { Group, StudentDto, GroupDetails } from "./types/group.types";
export type { Grade, GradeListItem, GradeList } from "./types/grade.types";
export type {
  Attendance,
  AttendanceStatus,
  LecturePreview,
  Lecture,
} from "./types/attendance.types";
export type { Summary } from "./types/summary.types";
export {
  formatGrade,
  formatDate,
  getAttendanceStatus,
  getStudentName,
  getStudent,
  sortedStudentIds,
  COURSE_TABS,
} from "./utils/course.utils";
export type { TabType } from "./utils/course.utils";

// Shared (CourseManagePage + CoursePerformancePage)
export { CourseTabs } from "./components/CourseTabs";

// CourseManagePage (lecturer)
export { useGroupDetails } from "./hooks/useGroupDetails";
export { useGrades } from "./hooks/useGrades";
export { useAttendance } from "./hooks/useAttendance";
export { useLectures } from "./hooks/useLectures";
export { useSummary } from "./hooks/useSummary";
export { GradesTab } from "./components/GradesTab";
export { AttendanceTab } from "./components/AttendanceTab";
export { SummaryTab } from "./components/SummaryTab";
export {
  SingleGradeModal,
  EditGradeModal,
  ListGradeModal,
} from "./components/GradeModals";
export { AttendanceModal } from "./components/AttendanceModal";
export { LecturesModal } from "./components/LecturesModal";

// CoursePerformancePage (student)
export { useCourseDetails } from "./hooks/useCourseDetails";
export { useStudentGrades } from "./hooks/useStudentGrades";
export { useStudentAttendance } from "./hooks/useStudentAttendance";
export { useStudentSummary } from "./hooks/useStudentSummary";
export { StudentGradesTab } from "./components/StudentGradesTab";
export { StudentAttendanceTab } from "./components/StudentAttendanceTab";
export { StudentSummaryTab } from "./components/StudentSummaryTab";
