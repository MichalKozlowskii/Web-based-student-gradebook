import type { StudentDto } from "../types/group.types";

export const getStudentName = (
  participants: StudentDto[],
  studentId: string,
): string => {
  const student = participants.find((p) => p.id === studentId);
  return student ? `${student.firstName} ${student.lastName}` : studentId;
};

export const getStudent = (
  participants: StudentDto[],
  studentId: string,
): StudentDto | undefined => {
  return participants.find((p) => p.id === studentId);
};

export const sortedStudentIds = (
  participants: StudentDto[],
  ids: string[],
): string[] => {
  return [...ids].sort((a, b) => {
    const sa = participants.find((p) => p.id === a);
    const sb = participants.find((p) => p.id === b);
    if (!sa || !sb) return 0;
    return sa.lastName.localeCompare(sb.lastName, "pl");
  });
};

export const formatGrade = (grade: string): string => {
  if (grade === "0.1") return "+";
  if (grade === "0.01") return "-";
  return grade;
};

export const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString("pl-PL", {
    day: "numeric",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

export const getAttendanceStatus = (status: string) => {
  switch (status) {
    case "PRESENT":
      return {
        label: "Obecny",
        color: "bg-green-100 text-green-700",
        icon: "✓",
      };
    case "ABSENT":
      return {
        label: "Nieobecny",
        color: "bg-red-100 text-red-700",
        icon: "✗",
      };
    case "EXCUSED":
      return {
        label: "Usprawiedliwiony",
        color: "bg-yellow-100 text-yellow-700",
        icon: "⚠",
      };
    default:
      return { label: status, color: "bg-gray-100 text-gray-700", icon: "?" };
  }
};

export type TabType = "grades" | "attendance" | "summary";

export const COURSE_TABS: { id: TabType; label: string; icon: string }[] = [
  { id: "grades", label: "Oceny", icon: "📝" },
  { id: "attendance", label: "Obecności", icon: "✓" },
  { id: "summary", label: "Podsumowanie", icon: "📊" },
];
