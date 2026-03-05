import React from "react";
import type { Exam } from "../types/exam.types";
import { formatDate } from "../utils/exam.utils";

interface ExamInfoCardProps {
  exam: Exam;
}

export const ExamInfoCard: React.FC<ExamInfoCardProps> = ({ exam }) => (
  <div className="space-y-4">
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div className="bg-gray-50 rounded-lg p-4">
        <p className="text-xs text-gray-500 mb-1">Liczba zadań</p>
        <p className="text-lg font-semibold text-gray-900">
          {exam.numberOfTasks}
        </p>
      </div>
      <div className="bg-gray-50 rounded-lg p-4">
        <p className="text-xs text-gray-500 mb-1">Utworzono</p>
        <p className="text-sm font-medium text-gray-900">
          {formatDate(exam.createdAt)}
        </p>
      </div>
      <div className="bg-gray-50 rounded-lg p-4">
        <p className="text-xs text-gray-500 mb-1">Ostatnia modyfikacja</p>
        <p className="text-sm font-medium text-gray-900">
          {formatDate(exam.lastUpdated)}
        </p>
      </div>
    </div>

    <div className="bg-gray-50 rounded-lg p-4">
      <p className="text-xs text-gray-500 mb-2">Progi ocen</p>
      <div className="flex flex-wrap gap-2">
        {Object.entries(exam.scope)
          .sort(([a], [b]) => Number(a) - Number(b))
          .map(([percent, grade]) => (
            <div
              key={percent}
              className="flex items-center gap-1 px-3 py-1.5 bg-white border border-gray-200 rounded-lg text-sm"
            >
              <span className="text-gray-500">od</span>
              <span className="font-semibold text-gray-900">{percent}%</span>
              <span className="text-gray-400 mx-1">&rarr;</span>
              <span className="font-semibold text-indigo-600">
                {grade.toFixed(1)}
              </span>
            </div>
          ))}
      </div>
    </div>
  </div>
);
