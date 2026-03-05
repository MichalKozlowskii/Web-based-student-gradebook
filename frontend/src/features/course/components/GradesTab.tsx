import React from "react";
import { formatGrade, formatDate } from "../utils/course.utils";
import { getStudentName, getStudent, sortedStudentIds } from "../utils/course.utils";
import type { StudentDto } from "../types/group.types";
import type { Grade } from "../types/grade.types";

interface GradesTabProps {
  participants: StudentDto[];
  grades: Record<string, Grade[]>;
  gradesLoading: boolean;
  gradesError: string | null;
  expandedGradeStudents: Set<string>;
  onToggleStudent: (studentId: string) => void;
  onAddGrade: (studentId: string) => void;
  onEditGrade: (grade: Grade) => void;
  onDeleteGrade: (gradeId: string) => void;
  deletingGradeId: string | null;
  onOpenListGradeModal: () => void;
}

export const GradesTab: React.FC<GradesTabProps> = ({
  participants,
  grades,
  gradesLoading,
  gradesError,
  expandedGradeStudents,
  onToggleStudent,
  onAddGrade,
  onEditGrade,
  onDeleteGrade,
  deletingGradeId,
  onOpenListGradeModal,
}) => {
  if (gradesLoading) {
    return (
      <>
        <div className="px-6 pt-4">
          <button
            disabled
            className="flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg opacity-50"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
            Dodaj oceny z listy
          </button>
        </div>
        <div className="p-6">
          <div className="flex items-center justify-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
            <span className="ml-3 text-gray-600">Ładowanie ocen...</span>
          </div>
        </div>
      </>
    );
  }

  if (gradesError) {
    return (
      <div className="p-6">
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
          {gradesError}
        </div>
      </div>
    );
  }

  const studentIds = sortedStudentIds(
    participants,
    Object.keys(grades).length > 0
      ? Object.keys(grades)
      : participants.map((p) => p.id),
  );

  return (
    <>
      <div className="px-6 pt-4">
        <button
          onClick={onOpenListGradeModal}
          className="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-lg transition duration-200"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          Dodaj oceny z listy
        </button>
      </div>
      <div className="p-6">
        <div className="space-y-4">
          {studentIds.length === 0 && (
            <div className="text-center py-12 text-gray-500">
              <p>Brak studentów w tej grupie.</p>
            </div>
          )}

          {studentIds.map((studentId) => {
            const studentGrades = grades[studentId] ?? [];
            const student = getStudent(participants, studentId);
            const isExpanded = expandedGradeStudents.has(studentId);

            return (
              <div
                key={studentId}
                className="border border-gray-200 rounded-lg overflow-hidden"
              >
                <div className="flex items-center justify-between px-4 py-3 bg-gray-50">
                  <button
                    type="button"
                    onClick={() => onToggleStudent(studentId)}
                    className="flex items-center gap-3 cursor-pointer"
                  >
                    <svg
                      className={`w-4 h-4 text-gray-500 transition-transform ${isExpanded ? "rotate-90" : ""}`}
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                    </svg>
                    <div>
                      <span className="font-semibold text-gray-900">
                        {getStudentName(participants, studentId)}
                      </span>
                      {student && (
                        <span className="ml-2 text-xs text-gray-500">
                          ({student.studentNumber})
                        </span>
                      )}
                    </div>
                  </button>
                  <div className="flex items-center gap-2">
                    {studentGrades.length > 0 && (
                      <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-indigo-100 text-indigo-700">
                        {studentGrades.length}{" "}
                        {studentGrades.length === 1
                          ? "ocena"
                          : studentGrades.length < 5
                            ? "oceny"
                            : "ocen"}
                      </span>
                    )}
                    <button
                      onClick={() => onAddGrade(studentId)}
                      className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-indigo-600 hover:text-indigo-800 hover:bg-indigo-50 rounded-lg transition-colors"
                    >
                      <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                      </svg>
                      Dodaj ocenę
                    </button>
                  </div>
                </div>

                {isExpanded &&
                  (studentGrades.length > 0 ? (
                    <div className="divide-y divide-gray-100">
                      {studentGrades.map((grade) => (
                        <div
                          key={grade.id}
                          className="flex items-center justify-between px-4 py-3"
                        >
                          <div className="flex-1">
                            <p className="text-sm font-medium text-gray-900">
                              {grade.title}
                            </p>
                            <p className="text-xs text-gray-500">
                              {formatDate(grade.lastUpdated)}
                            </p>
                          </div>
                          <div className="flex items-center gap-2">
                            <button
                              onClick={() => onEditGrade(grade)}
                              className="p-1.5 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors"
                              title="Edytuj"
                            >
                              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                              </svg>
                            </button>
                            <button
                              onClick={() => onDeleteGrade(grade.id)}
                              disabled={deletingGradeId === grade.id}
                              className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors disabled:opacity-50"
                              title="Usuń"
                            >
                              {deletingGradeId === grade.id ? (
                                <div className="w-4 h-4 animate-spin rounded-full border-2 border-red-300 border-t-red-600" />
                              ) : (
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                </svg>
                              )}
                            </button>
                            <span
                              className={`inline-flex items-center justify-center w-10 h-10 rounded-full font-bold text-sm ${
                                formatGrade(grade.grade) === "+"
                                  ? "bg-green-100 text-green-700"
                                  : formatGrade(grade.grade) === "-"
                                    ? "bg-red-100 text-red-700"
                                    : "bg-indigo-100 text-indigo-700"
                              }`}
                            >
                              {formatGrade(grade.grade)}
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="px-4 py-3 text-sm text-gray-400">
                      Brak ocen
                    </div>
                  ))}
              </div>
            );
          })}
        </div>
      </div>
    </>
  );
};
