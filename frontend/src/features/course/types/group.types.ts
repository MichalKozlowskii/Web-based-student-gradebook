export interface Group {
  id: string;
  courseName: string;
  courseUnitId: string;
  groupNumber: number;
}

export interface StudentDto {
  id: string;
  firstName: string;
  lastName: string;
  studentNumber: string;
}

export interface GroupDetails {
  id: string;
  courseUnitId: string;
  groupNumber: number;
  courseName: string;
  termId: string;
  participants: StudentDto[];
  lecturerId: string;
}
