import { useState, useCallback } from "react";
import type { GroupDetails } from "../types/group.types";
import type { Summary } from "../types/summary.types";

interface UseSummaryReturn {
  summaries: Record<string, Summary>;
  summaryLoading: boolean;
  summaryError: string | null;
  fetchSummary: () => Promise<void>;
  summaryAttempted: boolean;
  invalidate: () => void;
}

export const useSummary = (group: GroupDetails | null): UseSummaryReturn => {
  const [summaries, setSummaries] = useState<Record<string, Summary>>({});
  const [summaryLoading, setSummaryLoading] = useState(false);
  const [summaryError, setSummaryError] = useState<string | null>(null);
  const [summaryAttempted, setSummaryAttempted] = useState(false);

  const fetchSummary = useCallback(async () => {
    if (!group) return;
    setSummaryAttempted(true);
    setSummaryLoading(true);
    setSummaryError(null);
    try {
      const response = await fetch(
        `/gradebook/summary/api/get/${group.courseUnitId}/${group.groupNumber}`,
        {
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        },
      );
      if (response.ok) {
        setSummaries(await response.json());
      } else {
        setSummaryError("Nie udało się załadować podsumowań");
      }
    } catch (error) {
      console.error("Error fetching summaries:", error);
      setSummaryError("Nie udało się załadować podsumowań");
    } finally {
      setSummaryLoading(false);
    }
  }, [group]);

  const invalidate = useCallback(() => {
    setSummaryAttempted(false);
  }, []);

  return {
    summaries,
    summaryLoading,
    summaryError,
    fetchSummary,
    summaryAttempted,
    invalidate,
  };
};
