export interface Exam {
  id: string;
  title: string;
  numberOfTasks: number;
  scope: Record<string, number>;
  courseUnitId: string;
  lecturerId: string;
  createdAt: string;
  lastUpdated: string;
}

export interface ScopeRow {
  percent: string;
  grade: number;
}

export interface ScanResult {
  numberOfTasks: number;
  result: Record<string, number>;
  totalScore: number;
  resultPercent: number;
  grade: string;
  studentNumber: string;
}

export const GRADE_OPTIONS = [2.5, 3.0, 3.5, 4.0, 4.5, 5.0];

export const ALL_GRADES = [
  "0.01",
  "0.1",
  "2.0",
  "2.5",
  "3.0",
  "3.5",
  "4.0",
  "4.5",
  "5.0",
];
