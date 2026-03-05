import React from "react";
import { formatGrade, formatDate } from "../utils/course.utils";
import type { Grade } from "../types/grade.types";

interface StudentGradesTabProps {
  grades: Grade[];
  gradesLoading: boolean;
  gradesError: string | null;
}

export const StudentGradesTab: React.FC<StudentGradesTabProps> = ({
  grades,
  gradesLoading,
  gradesError,
}) => {
  if (gradesLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
        <span className="ml-3 text-gray-600">Ładowanie ocen...</span>
      </div>
    );
  }

  if (gradesError) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
        {gradesError}
      </div>
    );
  }

  if (grades.length === 0) {
    return (
      <div className="text-center py-12">
        <div className="mx-auto w-20 h-20 bg-indigo-100 rounded-full flex items-center justify-center mb-6">
          <svg
            className="w-10 h-10 text-indigo-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
            />
          </svg>
        </div>
        <h3 className="text-xl font-semibold text-gray-900 mb-2">Brak ocen</h3>
        <p className="text-gray-600">
          Nie masz jeszcze żadnych ocen z tego przedmiotu
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {grades.map((grade) => (
        <div
          key={grade.id}
          className="flex items-center justify-between p-4 bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors"
        >
          <div className="flex-1">
            <h4 className="font-semibold text-gray-900 mb-1">{grade.title}</h4>
            <p className="text-sm text-gray-500">
              Zaktualizowano {formatDate(grade.lastUpdated)}
            </p>
          </div>
          <div className="ml-4">
            <span
              className={`inline-flex items-center justify-center w-12 h-12 rounded-full font-bold text-lg ${
                formatGrade(grade.grade) === "+"
                  ? "bg-green-100 text-green-700"
                  : formatGrade(grade.grade) === "-"
                    ? "bg-red-100 text-red-700"
                    : "bg-indigo-100 text-indigo-700"
              }`}
            >
              {formatGrade(grade.grade)}
            </span>
          </div>
        </div>
      ))}
    </div>
  );
};
