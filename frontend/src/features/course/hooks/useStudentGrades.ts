import { useState, useCallback } from "react";
import type { Grade } from "../types/grade.types";

interface UseStudentGradesReturn {
  grades: Grade[];
  gradesLoading: boolean;
  gradesError: string | null;
  fetchGrades: () => Promise<void>;
  gradesAttempted: boolean;
}

export const useStudentGrades = (
  courseUnitId: string | undefined,
): UseStudentGradesReturn => {
  const [grades, setGrades] = useState<Grade[]>([]);
  const [gradesLoading, setGradesLoading] = useState(false);
  const [gradesError, setGradesError] = useState<string | null>(null);
  const [gradesAttempted, setGradesAttempted] = useState(false);

  const fetchGrades = useCallback(async () => {
    if (!courseUnitId) return;
    setGradesAttempted(true);
    setGradesLoading(true);
    setGradesError(null);
    try {
      const response = await fetch(
        `/gradebook/grades/api/fetch/course/${courseUnitId}`,
        {
          method: "GET",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        },
      );
      if (response.ok) {
        setGrades(await response.json());
      } else {
        setGradesError("Nie udało się załadować ocen");
      }
    } catch (error) {
      console.error("Error fetching grades:", error);
      setGradesError("Nie udało się załadować ocen");
    } finally {
      setGradesLoading(false);
    }
  }, [courseUnitId]);

  return { grades, gradesLoading, gradesError, fetchGrades, gradesAttempted };
};
