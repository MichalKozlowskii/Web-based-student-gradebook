import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Navbar } from "@/components/Navbar";
import {
  ExamFormFields,
  ExamInfoCard,
  ExamGradingSection,
  ExamInstructions,
  useExamDetail,
  useExamEdit,
  useExamGrading,
  getCourseName,
} from "@/features/exams";

export const ExamDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const { exam, groups, loading, refetch, generating, handleGenerateTable } =
    useExamDetail(id);
  const edit = useExamEdit(exam, { onSave: refetch });
  const grading = useExamGrading(id, exam?.scope ?? {});

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar showBackButton={true} />
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex items-center justify-center py-16">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
            <span className="ml-3 text-gray-600">Ładowanie...</span>
          </div>
        </main>
      </div>
    );
  }

  if (!exam) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar showBackButton={true} />
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center py-16 text-gray-500">
            <p className="text-lg">Nie znaleziono szablonu sprawdzianu.</p>
            <button
              onClick={() => navigate("/exams")}
              className="mt-4 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-lg transition duration-200"
            >
              Wróć do listy
            </button>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar showBackButton={true} />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow p-6">
          {/* Header */}
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-2xl font-bold text-gray-900">
                {exam.title}
              </h2>
              <span className="text-sm text-indigo-600 font-medium">
                {getCourseName(exam.courseUnitId, groups)}
              </span>
            </div>
            <div className="flex items-center gap-3">
              {/* Generate table */}
              <button
                onClick={handleGenerateTable}
                disabled={generating}
                className="flex items-center gap-2 px-4 py-2 bg-green-600 hover:bg-green-700 disabled:bg-green-400 text-white text-sm font-medium rounded-lg transition duration-200"
              >
                <svg
                  className={`w-4 h-4 ${generating ? "animate-spin" : ""}`}
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  {generating ? (
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
                    />
                  ) : (
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                    />
                  )}
                </svg>
                {generating ? "Generowanie..." : "Wygeneruj tabelkę"}
              </button>

              {/* Edit toggle */}
              <button
                onClick={() =>
                  edit.editing
                    ? edit.handleCancelEdit()
                    : edit.setEditing(true)
                }
                className={`flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-lg transition duration-200 ${
                  edit.editing
                    ? "bg-gray-200 hover:bg-gray-300 text-gray-700"
                    : "bg-indigo-600 hover:bg-indigo-700 text-white"
                }`}
              >
                <svg
                  className="w-4 h-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d={
                      edit.editing
                        ? "M6 18L18 6M6 6l12 12"
                        : "M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                    }
                  />
                </svg>
                {edit.editing ? "Anuluj" : "Edytuj"}
              </button>
            </div>
          </div>

          {/* Info (read-only) or Edit form */}
          {edit.editing ? (
            <form onSubmit={edit.handleUpdate} className="space-y-4">
              <ExamFormFields
                title={edit.title}
                onTitleChange={edit.setTitle}
                numberOfTasks={edit.numberOfTasks}
                onNumberOfTasksChange={edit.setNumberOfTasks}
                courseUnitId={edit.courseUnitId}
                onCourseUnitIdChange={edit.setCourseUnitId}
                scopeRows={edit.scopeRows}
                onScopeRowsChange={edit.setScopeRows}
                groups={groups}
              />
              <div className="flex justify-end gap-3 pt-2">
                <button
                  type="button"
                  onClick={edit.handleCancelEdit}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition duration-200"
                >
                  Anuluj
                </button>
                <button
                  type="submit"
                  disabled={!edit.isFormValid || edit.submitting}
                  className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 rounded-lg transition duration-200"
                >
                  {edit.submitting ? "Zapisywanie..." : "Zapisz zmiany"}
                </button>
              </div>
            </form>
          ) : (
            <ExamInfoCard exam={exam} />
          )}
        </div>

        <ExamGradingSection {...grading} />
        <ExamInstructions />
      </main>
    </div>
  );
};
