import React from "react";
import { formatDate, getAttendanceStatus } from "../utils/course.utils";
import type { Attendance } from "../types/attendance.types";

interface StudentAttendanceTabProps {
  attendance: Attendance[];
  attendanceLoading: boolean;
  attendanceError: string | null;
}

export const StudentAttendanceTab: React.FC<StudentAttendanceTabProps> = ({
  attendance,
  attendanceLoading,
  attendanceError,
}) => {
  if (attendanceLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
        <span className="ml-3 text-gray-600">Ładowanie obecności...</span>
      </div>
    );
  }

  if (attendanceError) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
        {attendanceError}
      </div>
    );
  }

  if (attendance.length === 0) {
    return (
      <div className="text-center py-12">
        <div className="mx-auto w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mb-6">
          <svg
            className="w-10 h-10 text-green-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
        </div>
        <h3 className="text-xl font-semibold text-gray-900 mb-2">
          Brak danych o obecności
        </h3>
        <p className="text-gray-600">
          Nie ma jeszcze zapisów o obecności na zajęciach
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {attendance.map((record) => {
        const status = getAttendanceStatus(record.status);
        return (
          <div
            key={record.id}
            className="flex items-center justify-between p-4 bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <div className="flex-1">
              <p className="text-sm text-gray-500">
                {formatDate(record.createdAt)}
              </p>
            </div>
            <div className="ml-4">
              <span
                className={`inline-flex items-center gap-2 px-4 py-2 rounded-full font-semibold ${status.color}`}
              >
                <span>{status.icon}</span>
                <span>{status.label}</span>
              </span>
            </div>
          </div>
        );
      })}
    </div>
  );
};
