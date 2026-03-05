import React from "react";
import { formatDate, getAttendanceStatus } from "../utils/course.utils";
import { getStudentName, getStudent, sortedStudentIds } from "../utils/course.utils";
import { ATTENDANCE_OPTIONS } from "./AttendanceModal";
import type { StudentDto } from "../types/group.types";
import type {
  Attendance,
  AttendanceStatus,
  LecturePreview,
  Lecture,
} from "../types/attendance.types";

interface LecturesModalProps {
  participants: StudentDto[];
  lectures: LecturePreview[];
  expandedLectures: Set<string>;
  onToggleLecture: (lectureId: string) => void;
  lectureDetails: Record<string, Lecture>;
  lectureDetailsLoading: Set<string>;
  lectureEdits: Record<string, Record<string, AttendanceStatus>>;
  onSetAttendanceEdit: (
    lectureId: string,
    studentId: string,
    status: AttendanceStatus,
  ) => void;
  onClearEdits: (lectureId: string) => void;
  onSaveEdits: (lectureId: string) => void;
  savingLectureId: string | null;
  editingAttendanceId: string | null;
  onSetEditingAttendanceId: (id: string | null) => void;
  deletingLectureId: string | null;
  onDeleteLecture: (lectureId: string) => void;
  onClose: () => void;
}

export const LecturesModal: React.FC<LecturesModalProps> = ({
  participants,
  lectures,
  expandedLectures,
  onToggleLecture,
  lectureDetails,
  lectureDetailsLoading,
  lectureEdits,
  onSetAttendanceEdit,
  onClearEdits,
  onSaveEdits,
  savingLectureId,
  editingAttendanceId,
  onSetEditingAttendanceId,
  deletingLectureId,
  onDeleteLecture,
  onClose,
}) => {
  const sorted = [...lectures].sort((a, b) =>
    (b.createdAt ?? "").localeCompare(a.createdAt ?? ""),
  );

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/40" onClick={onClose} />
      <div className="relative bg-white rounded-lg shadow-xl w-full max-w-3xl mx-4 p-6 max-h-[90vh] overflow-y-auto">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          Listy obecności
        </h3>

        {sorted.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            Brak list obecności
          </div>
        ) : (
          <div className="space-y-4">
            {sorted.map((lecture, index) => {
              const date = lecture.createdAt
                ? formatDate(lecture.createdAt)
                : "Brak daty";

              const isExpanded = expandedLectures.has(lecture.id);
              const detail = lectureDetails[lecture.id];
              const isDetailLoading = lectureDetailsLoading.has(lecture.id);
              const records = detail?.attendanceList ?? [];
              const edits = lectureEdits[lecture.id] ?? {};
              const hasEdits = Object.keys(edits).length > 0;
              const isSaving = savingLectureId === lecture.id;

              const getEffectiveStatus = (r: Attendance) =>
                edits[r.studentId] ?? r.status;

              const presentCount = records.filter(
                (r) => getEffectiveStatus(r) === "PRESENT",
              ).length;
              const absentCount = records.filter(
                (r) => getEffectiveStatus(r) === "ABSENT",
              ).length;
              const excusedCount = records.filter(
                (r) => getEffectiveStatus(r) === "EXCUSED",
              ).length;

              return (
                <div
                  key={lecture.id}
                  className="border border-gray-200 rounded-lg overflow-hidden"
                >
                  <div
                    role="button"
                    tabIndex={0}
                    onClick={() => onToggleLecture(lecture.id)}
                    onKeyDown={(e) => { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); onToggleLecture(lecture.id); } }}
                    className="w-full px-4 py-3 bg-gray-50 flex items-center justify-between hover:bg-gray-100 transition-colors cursor-pointer"
                  >
                    <div className="flex items-center gap-3">
                      <svg
                        className={`w-4 h-4 text-gray-500 transition-transform ${isExpanded ? "rotate-90" : ""}`}
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M9 5l7 7-7 7"
                        />
                      </svg>
                      <span className="font-semibold text-gray-900">
                        Lista #{sorted.length - index}
                      </span>
                      <span className="text-sm text-gray-500">{date}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      {detail && (
                        <>
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
                        </>
                      )}
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          onDeleteLecture(lecture.id);
                        }}
                        disabled={deletingLectureId === lecture.id}
                        className="ml-1 p-1 text-gray-400 hover:text-red-600 disabled:text-gray-300 rounded transition-colors cursor-pointer"
                        title="Usuń listę obecności"
                      >
                        {deletingLectureId === lecture.id ? (
                          <div className="w-4 h-4 animate-spin rounded-full border-2 border-red-300 border-t-red-600" />
                        ) : (
                          <svg
                            className="w-4 h-4"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                            />
                          </svg>
                        )}
                      </button>
                    </div>
                  </div>
                  {isExpanded &&
                    (isDetailLoading ? (
                      <div className="flex items-center justify-center py-4">
                        <div className="w-5 h-5 animate-spin rounded-full border-2 border-indigo-300 border-t-indigo-600" />
                        <span className="ml-2 text-sm text-gray-500">
                          Ładowanie...
                        </span>
                      </div>
                    ) : records.length > 0 ? (
                      <div>
                        <div className="divide-y divide-gray-100">
                          {sortedStudentIds(
                            participants,
                            records.map((r) => r.studentId),
                          ).map((studentId) => {
                            const record = records.find(
                              (r) => r.studentId === studentId,
                            );
                            if (!record) return null;
                            const effectiveStatus =
                              getEffectiveStatus(record);
                            const status =
                              getAttendanceStatus(effectiveStatus);
                            const student = getStudent(
                              participants,
                              studentId,
                            );
                            const isEditing =
                              editingAttendanceId === record.id;
                            const isEdited =
                              edits[studentId] !== undefined &&
                              edits[studentId] !== record.status;
                            return (
                              <div
                                key={record.id}
                                className={`flex items-center justify-between px-4 py-2 ${isEdited ? "bg-amber-50" : ""}`}
                              >
                                <div>
                                  <span className="text-sm font-medium text-gray-900">
                                    {getStudentName(
                                      participants,
                                      studentId,
                                    )}
                                  </span>
                                  {student && (
                                    <span className="ml-2 text-xs text-gray-500">
                                      ({student.studentNumber})
                                    </span>
                                  )}
                                </div>
                                {isEditing ? (
                                  <div className="flex items-center gap-1.5">
                                    {ATTENDANCE_OPTIONS.map((opt) => {
                                      const optStatus =
                                        getAttendanceStatus(opt.value);
                                      const isActive =
                                        effectiveStatus === opt.value;
                                      return (
                                        <button
                                          key={opt.value}
                                          onClick={() =>
                                            onSetAttendanceEdit(
                                              lecture.id,
                                              studentId,
                                              opt.value,
                                            )
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
                                      onClick={() =>
                                        onSetEditingAttendanceId(null)
                                      }
                                      className="ml-1 p-1 text-gray-400 hover:text-gray-600 rounded transition-colors"
                                    >
                                      <svg
                                        className="w-3.5 h-3.5"
                                        fill="none"
                                        stroke="currentColor"
                                        viewBox="0 0 24 24"
                                      >
                                        <path
                                          strokeLinecap="round"
                                          strokeLinejoin="round"
                                          strokeWidth={2}
                                          d="M6 18L18 6M6 6l12 12"
                                        />
                                      </svg>
                                    </button>
                                  </div>
                                ) : (
                                  <button
                                    onClick={() =>
                                      onSetEditingAttendanceId(record.id)
                                    }
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
                        {hasEdits && (
                          <div className="flex justify-end px-4 py-3 bg-gray-50 border-t border-gray-200">
                            <button
                              onClick={() => onClearEdits(lecture.id)}
                              className="px-3 py-1.5 text-sm font-medium text-gray-600 hover:bg-gray-200 rounded-lg transition-colors mr-2"
                            >
                              Anuluj
                            </button>
                            <button
                              onClick={() => onSaveEdits(lecture.id)}
                              disabled={isSaving}
                              className="px-4 py-1.5 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 rounded-lg transition-colors"
                            >
                              {isSaving ? "Zapisywanie..." : "Zatwierdź"}
                            </button>
                          </div>
                        )}
                      </div>
                    ) : (
                      <div className="text-center py-4 text-sm text-gray-400">
                        Brak danych o obecności
                      </div>
                    ))}
                </div>
              );
            })}
          </div>
        )}

        <div className="flex justify-end mt-6">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
          >
            Zamknij
          </button>
        </div>
      </div>
    </div>
  );
};
