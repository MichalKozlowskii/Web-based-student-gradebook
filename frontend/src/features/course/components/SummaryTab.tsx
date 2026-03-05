import React from "react";
import { getStudentName, getStudent, sortedStudentIds } from "../utils/course.utils";
import type { StudentDto } from "../types/group.types";
import type { Summary } from "../types/summary.types";

interface SummaryTabProps {
  participants: StudentDto[];
  summaries: Record<string, Summary>;
  summaryLoading: boolean;
  summaryError: string | null;
}

export const SummaryTab: React.FC<SummaryTabProps> = ({
  participants,
  summaries,
  summaryLoading,
  summaryError,
}) => {
  if (summaryLoading) {
    return (
      <div className="p-6">
        <div className="flex items-center justify-center py-12">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
          <span className="ml-3 text-gray-600">Ładowanie podsumowań...</span>
        </div>
      </div>
    );
  }

  if (summaryError) {
    return (
      <div className="p-6">
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
          {summaryError}
        </div>
      </div>
    );
  }

  const studentIds = sortedStudentIds(
    participants,
    Object.keys(summaries).length > 0
      ? Object.keys(summaries)
      : participants.map((p) => p.id),
  );

  return (
    <div className="p-6">
      <div className="space-y-4">
        {studentIds.length === 0 && (
          <div className="text-center py-12 text-gray-500">
            <p>Brak studentów w tej grupie.</p>
          </div>
        )}

        {studentIds.map((studentId) => {
          const summary = summaries[studentId];
          const student = getStudent(participants, studentId);

          return (
            <div
              key={studentId}
              className="border border-gray-200 rounded-lg overflow-hidden"
            >
              <div className="px-4 py-3 bg-gray-50">
                <span className="font-semibold text-gray-900">
                  {getStudentName(participants, studentId)}
                </span>
                {student && (
                  <span className="ml-2 text-xs text-gray-500">
                    ({student.studentNumber})
                  </span>
                )}
              </div>

              {summary ? (
                <div className="p-4">
                  <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
                    <div className="bg-indigo-50 rounded-lg p-3 text-center">
                      <p className="text-xs text-gray-500 mb-1">Średnia</p>
                      <p className="text-lg font-bold text-indigo-600">
                        {summary.gradesMean.toFixed(2)}
                      </p>
                    </div>
                    <div className="bg-green-50 rounded-lg p-3 text-center">
                      <p className="text-xs text-gray-500 mb-1">Plusy</p>
                      <p className="text-lg font-bold text-green-600">
                        {summary.plusCount}
                      </p>
                    </div>
                    <div className="bg-red-50 rounded-lg p-3 text-center">
                      <p className="text-xs text-gray-500 mb-1">Minusy</p>
                      <p className="text-lg font-bold text-red-600">
                        {summary.minusCount}
                      </p>
                    </div>
                    <div className="bg-green-50 rounded-lg p-3 text-center">
                      <p className="text-xs text-gray-500 mb-1">Obecny</p>
                      <p className="text-lg font-bold text-green-600">
                        {summary.present}
                      </p>
                    </div>
                    <div className="bg-red-50 rounded-lg p-3 text-center">
                      <p className="text-xs text-gray-500 mb-1">Nieobecny</p>
                      <p className="text-lg font-bold text-red-600">
                        {summary.absent}
                      </p>
                    </div>
                    <div className="bg-yellow-50 rounded-lg p-3 text-center">
                      <p className="text-xs text-gray-500 mb-1">Uspr.</p>
                      <p className="text-lg font-bold text-yellow-600">
                        {summary.excused}
                      </p>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="px-4 py-3 text-sm text-gray-400">
                  Brak danych
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
