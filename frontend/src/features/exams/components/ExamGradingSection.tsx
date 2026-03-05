import React from "react";
import type { UseExamGradingReturn } from "../hooks/useExamGrading";

type ExamGradingSectionProps = UseExamGradingReturn;

export const ExamGradingSection: React.FC<ExamGradingSectionProps> = ({
  fileInputRef,
  analyzing,
  scanResult,
  grading,
  gradeMessage,
  allGrades,
  handleFileUpload,
  updateScanResultField,
  updateScanTaskResult,
  handleAcceptGrade,
  handleCancelScan,
}) => (
  <div className="bg-white rounded-lg shadow p-6 mt-6">
    <h3 className="text-lg font-semibold text-gray-800 mb-4">
      Sprawdź pracę studenta
    </h3>

    {/* File upload */}
    <input
      ref={fileInputRef}
      type="file"
      accept="image/*"
      onChange={handleFileUpload}
      className="hidden"
    />

    {!scanResult && !analyzing && (
      <button
        onClick={() => fileInputRef.current?.click()}
        className="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-lg transition duration-200"
      >
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
            d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
          />
        </svg>
        Prześlij zdjęcie sprawdzianu
      </button>
    )}

    {analyzing && (
      <div className="flex items-center gap-3 py-4">
        <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-indigo-600"></div>
        <span className="text-sm text-gray-600">
          Analizowanie sprawdzianu...
        </span>
      </div>
    )}

    {/* Scan result - editable */}
    {scanResult && (
      <div className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-xs text-gray-500 mb-1">
              Numer indeksu
            </label>
            <input
              type="text"
              value={scanResult.studentNumber}
              onChange={(e) =>
                updateScanResultField("studentNumber", e.target.value)
              }
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs text-gray-500 mb-1">
              Wynik procentowy
            </label>
            <input
              type="number"
              value={scanResult.resultPercent}
              readOnly
              className="w-full px-3 py-2 border border-gray-200 rounded-lg bg-gray-100 text-gray-700 text-sm cursor-not-allowed"
            />
          </div>
          <div>
            <label className="block text-xs text-gray-500 mb-1">Ocena</label>
            <select
              value={scanResult.grade}
              onChange={(e) =>
                updateScanResultField("grade", e.target.value)
              }
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-sm"
            >
              {allGrades.map((g) => (
                <option key={g} value={g}>
                  {g}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div>
          <label className="block text-xs text-gray-500 mb-1">
            Suma punktów
          </label>
          <input
            type="number"
            value={scanResult.totalScore}
            readOnly
            className="w-32 px-3 py-2 border border-gray-200 rounded-lg bg-gray-100 text-gray-700 text-sm cursor-not-allowed"
          />
        </div>

        {/* Per-task results */}
        <div>
          <label className="block text-xs text-gray-500 mb-2">
            Punkty za zadania
          </label>
          <div className="flex flex-wrap gap-3">
            {Object.entries(scanResult.result)
              .sort(([a], [b]) => Number(a) - Number(b))
              .map(([taskNum, score]) => (
                <div key={taskNum} className="flex items-center gap-1.5">
                  <span className="text-xs text-gray-500 font-medium">
                    Zad. {taskNum}
                  </span>
                  <input
                    type="number"
                    step="0.5"
                    min={0}
                    max={1}
                    value={score}
                    onChange={(e) =>
                      updateScanTaskResult(
                        Number(taskNum),
                        Number(e.target.value),
                      )
                    }
                    className="w-16 px-2 py-1.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-sm text-center"
                  />
                </div>
              ))}
          </div>
        </div>

        <div className="flex gap-3 pt-2">
          <button
            onClick={handleCancelScan}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition duration-200"
          >
            Anuluj
          </button>
          <button
            onClick={handleAcceptGrade}
            disabled={grading}
            className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-green-600 hover:bg-green-700 disabled:bg-green-400 rounded-lg transition duration-200"
          >
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
                d="M5 13l4 4L19 7"
              />
            </svg>
            {grading ? "Wystawianie..." : "Akceptuj i wystaw ocenę"}
          </button>
        </div>
      </div>
    )}

    {/* Grade result message */}
    {gradeMessage && (
      <div
        className={`mt-4 flex gap-2 p-3 rounded-lg text-sm ${
          gradeMessage.type === "success"
            ? "bg-green-50 border border-green-200 text-green-800"
            : "bg-red-50 border border-red-200 text-red-800"
        }`}
      >
        <svg
          className="w-4 h-4 flex-shrink-0 mt-0.5"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d={
              gradeMessage.type === "success"
                ? "M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                : "M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            }
          />
        </svg>
        <span>{gradeMessage.text}</span>
      </div>
    )}
  </div>
);
