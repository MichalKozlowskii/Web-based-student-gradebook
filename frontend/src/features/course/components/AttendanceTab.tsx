import React from "react";
import { formatDate, getAttendanceStatus } from "../utils/course.utils";
import { getStudentName, getStudent, sortedStudentIds } from "../utils/course.utils";
import type { StudentDto } from "../types/group.types";
import type { Attendance, AttendanceStatus } from "../types/attendance.types";

const ATTENDANCE_OPTIONS: { value: AttendanceStatus; label: string }[] = [
  { value: "PRESENT", label: "Obecny" },
  { value: "ABSENT", label: "Nieobecny" },
  { value: "EXCUSED", label: "Usprawiedliwiony" },
];

interface AttendanceTabProps {
  participants: StudentDto[];
  attendance: Record<string, Attendance[]>;
  attendanceLoading: boolean;
  attendanceError: string | null;
  expandedAttendanceStudents: Set<string>;
  onToggleStudent: (studentId: string) => void;
  editingAttendanceId: string | null;
  onSetEditingAttendanceId: (id: string | null) => void;
  updatingAttendanceId: string | null;
  onUpdateAttendance: (
    record: Attendance,
    studentId: string,
    newStatus: AttendanceStatus,
  ) => void;
  onOpenAttendanceModal: () => void;
  onOpenLecturesModal: () => void;
  lecturesLoading: boolean;
}

export const AttendanceTab: React.FC<AttendanceTabProps> = ({
  participants,
  attendance,
  attendanceLoading,
  attendanceError,
  expandedAttendanceStudents,
  onToggleStudent,
  editingAttendanceId,
  onSetEditingAttendanceId,
  updatingAttendanceId,
  onUpdateAttendance,
  onOpenAttendanceModal,
  onOpenLecturesModal,
  lecturesLoading,
}) => {
  if (attendanceLoading) {
    return (
      <>
        <div className="px-6 pt-4 flex gap-3">
          <button disabled className="flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg opacity-50">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            Utwórz listę obecności
          </button>
        </div>
        <div className="p-6">
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
            <span className="ml-3 text-gray-600">Ładowanie obecności...</span>
          </div>
        </div>
      </>
    );
  }

  if (attendanceError) {
    return (
      <div className="p-6">
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
          {attendanceError}
        </div>
      </div>
    );
  }

  const studentIds = sortedStudentIds(
    participants,
    Object.keys(attendance).length > 0
      ? Object.keys(attendance)
      : participants.map((p) => p.id),
  );

  return (
    <>
      <div className="px-6 pt-4 flex gap-3">
        <button
          onClick={onOpenAttendanceModal}
          className="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-lg transition duration-200"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Utwórz listę obecności
        </button>
        <button
          onClick={onOpenLecturesModal}
          disabled={lecturesLoading}
          className="flex items-center gap-2 px-4 py-2 border border-indigo-600 text-indigo-600 hover:bg-indigo-50 text-sm font-medium rounded-lg transition duration-200 disabled:opacity-50"
        >
          {lecturesLoading ? (
            <div className="w-4 h-4 animate-spin rounded-full border-2 border-indigo-300 border-t-indigo-600" />
          ) : (
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          )}
          Zobacz listy obecności
        </button>
      </div>
      <div className="p-6">
        <div className="space-y-4">
          {studentIds.length === 0 && (
            <div className="text-center py-12 text-gray-500">
              <p>Brak studentów w tej grupie.</p>
            </div>
          )}

          {studentIds.map((studentId) => {
            const records = attendance[studentId] ?? [];
            const student = getStudent(participants, studentId);
            const isExpanded = expandedAttendanceStudents.has(studentId);

            const presentCount = records.filter((r) => r.status === "PRESENT").length;
            const absentCount = records.filter((r) => r.status === "ABSENT").length;
            const excusedCount = records.filter((r) => r.status === "EXCUSED").length;

            return (
              <div
                key={studentId}
                className="border border-gray-200 rounded-lg overflow-hidden"
              >
                <button
                  type="button"
                  onClick={() => onToggleStudent(studentId)}
                  className="w-full px-4 py-3 bg-gray-50 flex items-center justify-between hover:bg-gray-100 transition-colors cursor-pointer"
                >
                  <div className="flex items-center gap-3">
                    <svg
                      className={`w-4 h-4 text-gray-500 transition-transform ${isExpanded ? "rotate-90" : ""}`}
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                    </svg>
                    <div>
                      <span className="font-semibold text-gray-900">
                        {getStudentName(participants, studentId)}
                      </span>
                      {student && (
                        <span className="ml-2 text-xs text-gray-500">
                          ({student.studentNumber})
                        </span>
                      )}
                    </div>
                  </div>
                  {records.length > 0 && (
                    <div className="flex items-center gap-2">
                      <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-700">
                        {presentCount}
                      </span>
                      <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-700">
                        {absentCount}
                      </span>
                      {excusedCount > 0 && (
                        <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-700">
                          {excusedCount}
                        </span>
                      )}
                    </div>
                  )}
                </button>

                {isExpanded &&
                  (records.length > 0 ? (
                    <div className="divide-y divide-gray-100">
                      {records.map((record) => {
                        const status = getAttendanceStatus(record.status);
                        const isEditing = editingAttendanceId === record.id;
                        const isUpdating = updatingAttendanceId === record.id;
                        return (
                          <div
                            key={record.id}
                            className="flex items-center justify-between px-4 py-3"
                          >
                            <p className="text-sm text-gray-600">
                              {formatDate(record.createdAt)}
                            </p>
                            {isUpdating ? (
                              <div className="w-5 h-5 animate-spin rounded-full border-2 border-indigo-300 border-t-indigo-600" />
                            ) : isEditing ? (
                              <div className="flex items-center gap-1.5">
                                {ATTENDANCE_OPTIONS.map((opt) => {
                                  const optStatus = getAttendanceStatus(opt.value);
                                  const isActive = record.status === opt.value;
                                  return (
                                    <button
                                      key={opt.value}
                                      onClick={() =>
                                        onUpdateAttendance(record, studentId, opt.value)
                                      }
                                      className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-semibold transition-all ${
                                        isActive
                                          ? `${optStatus.color} ring-2 ring-offset-1 ring-current`
                                          : `${optStatus.color} opacity-50 hover:opacity-100`
                                      }`}
                                    >
                                      <span>{optStatus.icon}</span>
                                      <span>{opt.label}</span>
                                    </button>
                                  );
                                })}
                                <button
                                  onClick={() => onSetEditingAttendanceId(null)}
                                  className="ml-1 p-1 text-gray-400 hover:text-gray-600 rounded transition-colors"
                                >
                                  <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                  </svg>
                                </button>
                              </div>
                            ) : (
                              <button
                                onClick={() => onSetEditingAttendanceId(record.id)}
                                className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold cursor-pointer hover:ring-2 hover:ring-offset-1 hover:ring-current transition-all ${status.color}`}
                              >
                                <span>{status.icon}</span>
                                <span>{status.label}</span>
                              </button>
                            )}
                          </div>
                        );
                      })}
                    </div>
                  ) : (
                    <div className="px-4 py-3 text-sm text-gray-400">
                      Brak danych o obecności
                    </div>
                  ))}
              </div>
            );
          })}
        </div>
      </div>
    </>
  );
};
