import React from "react";
import { getStudentName, getStudent, sortedStudentIds, getAttendanceStatus } from "../utils/course.utils";
import type { StudentDto } from "../types/group.types";
import type { AttendanceStatus } from "../types/attendance.types";

export const ATTENDANCE_OPTIONS: { value: AttendanceStatus; label: string }[] = [
  { value: "PRESENT", label: "Obecny" },
  { value: "ABSENT", label: "Nieobecny" },
  { value: "EXCUSED", label: "Usprawiedliwiony" },
];

interface AttendanceModalProps {
  participants: StudentDto[];
  statuses: Record<string, AttendanceStatus>;
  onStatusChange: (studentId: string, status: AttendanceStatus) => void;
  submitting: boolean;
  onSubmit: () => void;
  onClose: () => void;
}

export const AttendanceModal: React.FC<AttendanceModalProps> = ({
  participants,
  statuses,
  onStatusChange,
  submitting,
  onSubmit,
  onClose,
}) => (
  <div className="fixed inset-0 z-50 flex items-center justify-center">
    <div className="absolute inset-0 bg-black/40" onClick={onClose} />
    <div className="relative bg-white rounded-lg shadow-xl w-full max-w-2xl mx-4 p-6 max-h-[90vh] overflow-y-auto">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">
        Utwórz listę obecności
      </h3>

      <div className="border border-gray-200 rounded-lg overflow-hidden">
        <div className="grid grid-cols-[1fr_auto] px-4 py-2 bg-gray-100 text-xs font-semibold text-gray-600 uppercase">
          <span>Student</span>
          <span>Status</span>
        </div>
        <div className="divide-y divide-gray-100">
          {sortedStudentIds(participants, participants.map((p) => p.id)).map(
            (studentId) => {
              const student = getStudent(participants, studentId);
              const currentStatus = statuses[studentId] ?? "PRESENT";
              return (
                <div
                  key={studentId}
                  className="flex items-center justify-between px-4 py-2"
                >
                  <div>
                    <span className="text-sm font-medium text-gray-900">
                      {getStudentName(participants, studentId)}
                    </span>
                    {student && (
                      <span className="ml-2 text-xs text-gray-500">
                        ({student.studentNumber})
                      </span>
                    )}
                  </div>
                  <div className="flex items-center gap-1.5">
                    {ATTENDANCE_OPTIONS.map((opt) => {
                      const optStatus = getAttendanceStatus(opt.value);
                      const isActive = currentStatus === opt.value;
                      return (
                        <button
                          key={opt.value}
                          type="button"
                          onClick={() =>
                            onStatusChange(studentId, opt.value)
                          }
                          className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-semibold transition-all cursor-pointer ${
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
                  </div>
                </div>
              );
            },
          )}
        </div>
      </div>

      <div className="flex justify-end gap-3 mt-6">
        <button
          onClick={onClose}
          className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
        >
          Anuluj
        </button>
        <button
          onClick={onSubmit}
          disabled={submitting}
          className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 rounded-lg transition-colors"
        >
          {submitting ? "Zapisywanie..." : "Zapisz"}
        </button>
      </div>
    </div>
  </div>
);
