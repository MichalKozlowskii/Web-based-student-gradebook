import { useState, useRef, useCallback } from "react";
import type { ScanResult } from "../types/exam.types";
import { ALL_GRADES } from "../types/exam.types";

interface GradeMessage {
  type: "success" | "error";
  text: string;
}

export interface UseExamGradingReturn {
  fileInputRef: React.RefObject<HTMLInputElement | null>;
  analyzing: boolean;
  scanResult: ScanResult | null;
  grading: boolean;
  gradeMessage: GradeMessage | null;
  allGrades: string[];
  handleFileUpload: (e: React.ChangeEvent<HTMLInputElement>) => Promise<void>;
  updateScanResultField: (field: keyof ScanResult, value: string | number) => void;
  updateScanTaskResult: (taskIndex: number, value: number) => void;
  handleAcceptGrade: () => Promise<void>;
  handleCancelScan: () => void;
}

const gradeFromScope = (scope: Record<string, number>, percent: number): string => {
  const thresholds = Object.entries(scope)
    .map(([p, g]) => ({ percent: Number(p), grade: g }))
    .sort((a, b) => b.percent - a.percent);

  for (const t of thresholds) {
    if (percent >= t.percent) return t.grade.toFixed(1);
  }
  return "2.0";
};

export const useExamGrading = (
  examId: string | undefined,
  examScope: Record<string, number>,
): UseExamGradingReturn => {
  const [scanResult, setScanResult] = useState<ScanResult | null>(null);
  const [analyzing, setAnalyzing] = useState(false);
  const [grading, setGrading] = useState(false);
  const [gradeMessage, setGradeMessage] = useState<GradeMessage | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileUpload = useCallback(
    async (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (!file || !examId) return;

      setAnalyzing(true);
      setGradeMessage(null);
      setScanResult(null);

      const formData = new FormData();
      formData.append("file", file);

      try {
        const response = await fetch(`/gradebook/exams/api/analyze/${examId}`, {
          method: "POST",
          credentials: "include",
          body: formData,
        });

        if (response.ok) {
          const data: ScanResult = await response.json();
          setScanResult(data);
        } else {
          setGradeMessage({
            type: "error",
            text: "Nie udało się przeanalizować zdjęcia. Spróbuj ponownie.",
          });
        }
      } catch (error) {
        console.error("Error analyzing exam:", error);
        setGradeMessage({
          type: "error",
          text: "Wystąpił błąd podczas analizy. Spróbuj ponownie.",
        });
      } finally {
        setAnalyzing(false);
        if (fileInputRef.current) fileInputRef.current.value = "";
      }
    },
    [examId],
  );

  const updateScanResultField = useCallback(
    (field: keyof ScanResult, value: string | number) => {
      setScanResult((prev) => (prev ? { ...prev, [field]: value } : null));
    },
    [],
  );

  const updateScanTaskResult = useCallback(
    (taskIndex: number, value: number) => {
      setScanResult((prev) => {
        if (!prev) return null;
        const updatedResult = { ...prev.result };
        updatedResult[taskIndex] = value;

        const totalScore = Object.values(updatedResult).reduce((sum, v) => sum + v, 0);
        const resultPercent =
          prev.numberOfTasks > 0
            ? Math.round((totalScore / prev.numberOfTasks) * 100)
            : 0;
        const grade = gradeFromScope(examScope, resultPercent);

        return { ...prev, result: updatedResult, totalScore, resultPercent, grade };
      });
    },
    [examScope],
  );

  const handleAcceptGrade = useCallback(async () => {
    const current = scanResult;
    if (!current || !examId) return;

    setGrading(true);
    setGradeMessage(null);

    try {
      const response = await fetch(`/gradebook/exams/api/grade/${examId}`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(current),
      });

      if (response.ok) {
        setGradeMessage({
          type: "success",
          text: `Ocena ${current.grade} została wystawiona dla studenta ${current.studentNumber}.`,
        });
        setScanResult(null);
      } else {
        const text = await response.text();
        setGradeMessage({
          type: "error",
          text: text || "Nie udało się wystawić oceny. Spróbuj ponownie.",
        });
      }
    } catch (error) {
      console.error("Error grading exam:", error);
      setGradeMessage({
        type: "error",
        text: "Wystąpił błąd podczas wystawiania oceny.",
      });
    } finally {
      setGrading(false);
    }
  }, [scanResult, examId]);

  const handleCancelScan = useCallback(() => {
    setScanResult(null);
    setGradeMessage(null);
  }, []);

  return {
    fileInputRef,
    analyzing,
    scanResult,
    grading,
    gradeMessage,
    allGrades: ALL_GRADES,
    handleFileUpload,
    updateScanResultField,
    updateScanTaskResult,
    handleAcceptGrade,
    handleCancelScan,
  };
};
