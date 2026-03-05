import React from "react";
import type { Group } from "@/features/course";
import type { ScopeRow } from "../types/exam.types";
import { ScopeRowsEditor } from "./ScopeRowsEditor";
import { getUniqueGroups } from "../utils/exam.utils";

interface ExamFormFieldsProps {
  title: string;
  onTitleChange: (value: string) => void;
  numberOfTasks: number | "";
  onNumberOfTasksChange: (value: number | "") => void;
  courseUnitId: string;
  onCourseUnitIdChange: (value: string) => void;
  scopeRows: ScopeRow[];
  onScopeRowsChange: (rows: ScopeRow[]) => void;
  groups: Group[];
}

export const ExamFormFields: React.FC<ExamFormFieldsProps> = ({
  title,
  onTitleChange,
  numberOfTasks,
  onNumberOfTasksChange,
  courseUnitId,
  onCourseUnitIdChange,
  scopeRows,
  onScopeRowsChange,
  groups,
}) => {
  const uniqueGroups = getUniqueGroups(groups);

  return (
    <>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Tytuł
          </label>
          <input
            type="text"
            value={title}
            onChange={(e) => onTitleChange(e.target.value)}
            placeholder="np. Kolokwium 1"
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-sm"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Liczba zadań
          </label>
          <input
            type="number"
            min={1}
            value={numberOfTasks}
            onChange={(e) =>
              onNumberOfTasksChange(
                e.target.value === "" ? "" : Number(e.target.value),
              )
            }
            placeholder="np. 5"
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-sm"
          />
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Przedmiot
        </label>
        <select
          value={courseUnitId}
          onChange={(e) => onCourseUnitIdChange(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-sm"
        >
          <option value="">Wybierz przedmiot...</option>
          {uniqueGroups.map((g) => (
            <option key={g.courseUnitId} value={g.courseUnitId}>
              {g.courseName}
            </option>
          ))}
        </select>
      </div>

      <ScopeRowsEditor scopeRows={scopeRows} onChange={onScopeRowsChange} />
    </>
  );
};
