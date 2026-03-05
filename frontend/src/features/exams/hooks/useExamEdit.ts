import { useState, useEffect, useCallback } from "react";
import type { Exam, ScopeRow } from "../types/exam.types";

interface UseExamEditOptions {
  onSave?: () => void;
}

interface UseExamEditReturn {
  editing: boolean;
  setEditing: (v: boolean) => void;
  title: string;
  setTitle: (v: string) => void;
  numberOfTasks: number | "";
  setNumberOfTasks: (v: number | "") => void;
  courseUnitId: string;
  setCourseUnitId: (v: string) => void;
  scopeRows: ScopeRow[];
  setScopeRows: (v: ScopeRow[]) => void;
  isFormValid: boolean;
  submitting: boolean;
  handleUpdate: (e: React.FormEvent) => Promise<void>;
  handleCancelEdit: () => void;
}

export const useExamEdit = (
  exam: Exam | null,
  options?: UseExamEditOptions,
): UseExamEditReturn => {
  const [editing, setEditing] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [title, setTitle] = useState("");
  const [numberOfTasks, setNumberOfTasks] = useState<number | "">("");
  const [courseUnitId, setCourseUnitId] = useState("");
  const [scopeRows, setScopeRows] = useState<ScopeRow[]>([]);

  const populateForm = useCallback((data: Exam) => {
    setTitle(data.title);
    setNumberOfTasks(data.numberOfTasks);
    setCourseUnitId(data.courseUnitId);
    setScopeRows(
      Object.entries(data.scope)
        .sort(([a], [b]) => Number(a) - Number(b))
        .map(([percent, grade]) => ({ percent, grade })),
    );
  }, []);

  useEffect(() => {
    if (exam) populateForm(exam);
  }, [exam, populateForm]);

  const handleCancelEdit = useCallback(() => {
    if (exam) populateForm(exam);
    setEditing(false);
  }, [exam, populateForm]);

  const handleUpdate = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!exam) return;

      const scope: Record<string, number> = {};
      for (const row of scopeRows) {
        if (row.percent !== "") {
          scope[row.percent] = row.grade;
        }
      }

      const body = {
        title,
        numberOfTasks: Number(numberOfTasks),
        scope,
        courseUnitId,
      };

      setSubmitting(true);
      try {
        const response = await fetch(`/gradebook/exams/api/update/${exam.id}`, {
          method: "PUT",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(body),
        });

        if (response.ok) {
          setEditing(false);
          options?.onSave?.();
        } else {
          console.error("Failed to update exam template");
        }
      } catch (error) {
        console.error("Error updating exam template:", error);
      } finally {
        setSubmitting(false);
      }
    },
    [exam, title, numberOfTasks, courseUnitId, scopeRows, options],
  );

  const isFormValid =
    title.trim() !== "" &&
    numberOfTasks !== "" &&
    Number(numberOfTasks) > 0 &&
    courseUnitId !== "" &&
    scopeRows.length > 0 &&
    scopeRows.every(
      (r) =>
        r.percent !== "" && Number(r.percent) >= 0 && Number(r.percent) <= 100,
    );

  return {
    editing,
    setEditing,
    title,
    setTitle,
    numberOfTasks,
    setNumberOfTasks,
    courseUnitId,
    setCourseUnitId,
    scopeRows,
    setScopeRows,
    isFormValid,
    submitting,
    handleUpdate,
    handleCancelEdit,
  };
};
