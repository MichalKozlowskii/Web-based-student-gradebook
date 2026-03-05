import React from "react";
import type { ScopeRow } from "../types/exam.types";
import { GRADE_OPTIONS } from "../types/exam.types";

interface ScopeRowsEditorProps {
  scopeRows: ScopeRow[];
  onChange: (rows: ScopeRow[]) => void;
}

export const ScopeRowsEditor: React.FC<ScopeRowsEditorProps> = ({
  scopeRows,
  onChange,
}) => {
  const addScopeRow = () => {
    const usedGrades = scopeRows.map((r) => r.grade);
    const nextGrade =
      GRADE_OPTIONS.find((g) => !usedGrades.includes(g)) ?? 2.5;
    onChange([...scopeRows, { percent: "", grade: nextGrade }]);
  };

  const removeScopeRow = (index: number) => {
    onChange(scopeRows.filter((_, i) => i !== index));
  };

  const updateScopeRow = (
    index: number,
    field: keyof ScopeRow,
    value: string | number,
  ) => {
    const updated = [...scopeRows];
    if (field === "percent") {
      updated[index].percent = value as string;
    } else {
      updated[index].grade = value as number;
    }
    onChange(updated);
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-2">
        <label className="block text-sm font-medium text-gray-700">
          Progi ocen
        </label>
        <button
          type="button"
          onClick={addScopeRow}
          disabled={scopeRows.length >= GRADE_OPTIONS.length}
          className="flex items-center gap-1 text-xs text-indigo-600 hover:text-indigo-800 disabled:text-gray-400 disabled:cursor-not-allowed font-medium"
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
              d="M12 4v16m8-8H4"
            />
          </svg>
          Dodaj próg
        </button>
      </div>

      <div className="space-y-2">
        {scopeRows.map((row, index) => (
          <div key={index} className="flex items-center gap-3">
            <div className="flex items-center gap-2 flex-1">
              <span className="text-sm text-gray-500 whitespace-nowrap">
                Od
              </span>
              <input
                type="number"
                min={0}
                max={100}
                value={row.percent}
                onChange={(e) =>
                  updateScopeRow(index, "percent", e.target.value)
                }
                placeholder="0-100"
                className="w-24 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-sm"
              />
              <span className="text-sm text-gray-500">%</span>
            </div>
            <div className="flex items-center gap-2">
              <span className="text-sm text-gray-500">Ocena</span>
              <select
                value={row.grade}
                onChange={(e) =>
                  updateScopeRow(index, "grade", Number(e.target.value))
                }
                className="w-20 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-sm"
              >
                {GRADE_OPTIONS.filter(
                  (g) =>
                    g === row.grade ||
                    !scopeRows.some((r, i) => i !== index && r.grade === g),
                ).map((g) => (
                  <option key={g} value={g}>
                    {g.toFixed(1)}
                  </option>
                ))}
              </select>
            </div>
            {scopeRows.length > 1 && (
              <button
                type="button"
                onClick={() => removeScopeRow(index)}
                className="p-1.5 text-red-400 hover:text-red-600 hover:bg-red-50 rounded transition-colors"
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
                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                  />
                </svg>
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};
