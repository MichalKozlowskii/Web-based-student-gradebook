export interface Grade {
  id: string;
  title: string;
  studentId: string;
  courseUnitId: string;
  termId: string;
  grade: string;
  createdAt: string;
  lastUpdated: string;
}

export interface GradeListItem {
  studentId: string;
  grade: string;
}

export interface GradeList {
  title: string;
  courseUnitId: string;
  groupNumber: number;
  list: GradeListItem[];
}
