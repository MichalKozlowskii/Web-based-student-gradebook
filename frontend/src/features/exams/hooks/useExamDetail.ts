import { useState, useEffect, useCallback } from "react";
import type { Group } from "@/features/course";
import type { Exam } from "../types/exam.types";

interface UseExamDetailReturn {
  exam: Exam | null;
  groups: Group[];
  loading: boolean;
  refetch: () => Promise<void>;
  generating: boolean;
  handleGenerateTable: () => Promise<void>;
}

export const useExamDetail = (examId: string | undefined): UseExamDetailReturn => {
  const [exam, setExam] = useState<Exam | null>(null);
  const [groups, setGroups] = useState<Group[]>([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);

  const fetchData = useCallback(async () => {
    if (!examId) return;
    setLoading(true);
    try {
      const [examRes, groupsRes] = await Promise.all([
        fetch(`/gradebook/exams/api/fetch/${examId}`, {
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        }),
        fetch("/gradebook/groups/api/fetch", {
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        }),
      ]);

      if (examRes.ok) {
        setExam(await examRes.json());
      }
      if (groupsRes.ok) {
        setGroups(await groupsRes.json());
      }
    } catch (error) {
      console.error("Error fetching exam:", error);
    } finally {
      setLoading(false);
    }
  }, [examId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleGenerateTable = useCallback(async () => {
    if (!examId) return;
    setGenerating(true);
    try {
      const response = await fetch(`/gradebook/exams/api/generateTable/${examId}`, {
        credentials: "include",
      });

      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `${exam?.title ?? "tabelka"}.jpg`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
      } else {
        console.error("Failed to generate table");
      }
    } catch (error) {
      console.error("Error generating table:", error);
    } finally {
      setGenerating(false);
    }
  }, [examId, exam?.title]);

  return { exam, groups, loading, refetch: fetchData, generating, handleGenerateTable };
};
