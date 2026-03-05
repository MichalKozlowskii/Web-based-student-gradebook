import { useState, useCallback } from "react";
import type { Summary } from "../types/summary.types";

interface UseStudentSummaryReturn {
  summary: Summary | null;
  summaryLoading: boolean;
  summaryError: string | null;
  fetchSummary: () => Promise<void>;
  summaryAttempted: boolean;
}

export const useStudentSummary = (
  courseUnitId: string | undefined,
): UseStudentSummaryReturn => {
  const [summary, setSummary] = useState<Summary | null>(null);
  const [summaryLoading, setSummaryLoading] = useState(false);
  const [summaryError, setSummaryError] = useState<string | null>(null);
  const [summaryAttempted, setSummaryAttempted] = useState(false);

  const fetchSummary = useCallback(async () => {
    if (!courseUnitId) return;
    setSummaryAttempted(true);
    setSummaryLoading(true);
    setSummaryError(null);
    try {
      const response = await fetch(
        `/gradebook/summary/api/get/${courseUnitId}`,
        {
          method: "GET",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        },
      );
      if (response.ok) {
        setSummary(await response.json());
      } else {
        setSummaryError("Nie udało się załadować podsumowania");
      }
    } catch (error) {
      console.error("Error fetching summary:", error);
      setSummaryError("Nie udało się załadować podsumowania");
    } finally {
      setSummaryLoading(false);
    }
  }, [courseUnitId]);

  return {
    summary,
    summaryLoading,
    summaryError,
    fetchSummary,
    summaryAttempted,
  };
};
