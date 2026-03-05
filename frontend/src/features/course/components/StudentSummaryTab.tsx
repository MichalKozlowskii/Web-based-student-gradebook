import React from "react";
import type { Summary } from "../types/summary.types";

interface StudentSummaryTabProps {
  summary: Summary | null;
  summaryLoading: boolean;
  summaryError: string | null;
}

const hasSummaryData = (summary: Summary | null): boolean => {
  if (!summary) return false;
  return (
    summary.gradesMean > 0 ||
    summary.plusCount > 0 ||
    summary.minusCount > 0 ||
    summary.present > 0 ||
    summary.absent > 0 ||
    summary.excused > 0
  );
};

export const StudentSummaryTab: React.FC<StudentSummaryTabProps> = ({
  summary,
  summaryLoading,
  summaryError,
}) => {
  if (summaryLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
        <span className="ml-3 text-gray-600">Ładowanie podsumowania...</span>
      </div>
    );
  }

  if (summaryError) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
        {summaryError}
      </div>
    );
  }

  if (!hasSummaryData(summary)) {
    return (
      <div className="text-center py-12">
        <div className="mx-auto w-20 h-20 bg-purple-100 rounded-full flex items-center justify-center mb-6">
          <svg
            className="w-10 h-10 text-purple-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
            />
          </svg>
        </div>
        <h3 className="text-xl font-semibold text-gray-900 mb-2">
          Brak danych
        </h3>
        <p className="text-gray-600">
          Nie ma jeszcze danych do podsumowania
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Grades Section */}
      <div>
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Oceny</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-gradient-to-br from-indigo-50 to-blue-50 border-2 border-indigo-200 rounded-lg p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Średnia</p>
                <p className="text-3xl font-bold text-indigo-600">
                  {summary?.gradesMean.toFixed(2) || "0.00"}
                </p>
              </div>
              <div className="w-12 h-12 bg-indigo-600 rounded-full flex items-center justify-center">
                <svg
                  className="w-6 h-6 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
                  />
                </svg>
              </div>
            </div>
          </div>

          <div className="bg-gradient-to-br from-green-50 to-emerald-50 border-2 border-green-200 rounded-lg p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Plusy</p>
                <p className="text-3xl font-bold text-green-600">
                  {summary?.plusCount || 0}
                </p>
              </div>
              <div className="w-12 h-12 bg-green-600 rounded-full flex items-center justify-center text-white text-2xl font-bold">
                +
              </div>
            </div>
          </div>

          <div className="bg-gradient-to-br from-red-50 to-rose-50 border-2 border-red-200 rounded-lg p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Minusy</p>
                <p className="text-3xl font-bold text-red-600">
                  {summary?.minusCount || 0}
                </p>
              </div>
              <div className="w-12 h-12 bg-red-600 rounded-full flex items-center justify-center text-white text-2xl font-bold">
                -
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Attendance Section */}
      <div>
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          Obecności
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-gradient-to-br from-green-50 to-emerald-50 border-2 border-green-200 rounded-lg p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Obecny</p>
                <p className="text-3xl font-bold text-green-600">
                  {summary?.present || 0}
                </p>
              </div>
              <div className="w-12 h-12 bg-green-600 rounded-full flex items-center justify-center text-white text-2xl font-bold">
                ✓
              </div>
            </div>
          </div>

          <div className="bg-gradient-to-br from-red-50 to-rose-50 border-2 border-red-200 rounded-lg p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">Nieobecny</p>
                <p className="text-3xl font-bold text-red-600">
                  {summary?.absent || 0}
                </p>
              </div>
              <div className="w-12 h-12 bg-red-600 rounded-full flex items-center justify-center text-white text-2xl font-bold">
                ✗
              </div>
            </div>
          </div>

          <div className="bg-gradient-to-br from-yellow-50 to-amber-50 border-2 border-yellow-200 rounded-lg p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 mb-1">
                  Usprawiedliwiony
                </p>
                <p className="text-3xl font-bold text-yellow-600">
                  {summary?.excused || 0}
                </p>
              </div>
              <div className="w-12 h-12 bg-yellow-600 rounded-full flex items-center justify-center text-white text-2xl font-bold">
                ⚠
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
