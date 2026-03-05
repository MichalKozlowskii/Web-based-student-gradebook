import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Navbar } from "@/components/Navbar";
import type { Group } from "@/features/course";
import {
  ExamFormFields,
  getCourseName,
  formatDate,
  type Exam,
  type ScopeRow,
} from "@/features/exams";

export const ExamsPage: React.FC = () => {
  const [exams, setExams] = useState<Exam[]>([]);
  const [groups, setGroups] = useState<Group[]>([]);
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // Form state
  const [title, setTitle] = useState("");
  const [numberOfTasks, setNumberOfTasks] = useState<number | "">("");
  const [courseUnitId, setCourseUnitId] = useState("");
  const [scopeRows, setScopeRows] = useState<ScopeRow[]>([
    { percent: "", grade: 2.5 },
  ]);

  const fetchExams = async () => {
    setLoading(true);
    try {
      const [examsRes, groupsRes] = await Promise.all([
        fetch("/gradebook/exams/api/fetch", {
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        }),
        fetch("/gradebook/groups/api/fetch", {
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        }),
      ]);

      if (examsRes.ok) {
        setExams(await examsRes.json());
      }
      if (groupsRes.ok) {
        setGroups(await groupsRes.json());
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchExams();
  }, []);

  const navigate = useNavigate();

  const handleExamClick = (exam: Exam) => {
    navigate(`/exams/${exam.id}`);
  };

  const resetForm = () => {
    setTitle("");
    setNumberOfTasks("");
    setCourseUnitId("");
    setScopeRows([{ percent: "", grade: 2.5 }]);
    setShowForm(false);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const scope: Record<string, number> = {};
    for (const row of scopeRows) {
      if (row.percent !== "") {
        scope[row.percent] = row.grade;
      }
    }

    const body = {
      title,
      numberOfTasks: Number(numberOfTasks),
      scope,
      courseUnitId,
    };

    setSubmitting(true);
    try {
      const response = await fetch("/gradebook/exams/api/create", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });

      if (response.ok) {
        resetForm();
        await fetchExams();
      } else {
        console.error("Failed to create exam template");
      }
    } catch (error) {
      console.error("Error creating exam template:", error);
    } finally {
      setSubmitting(false);
    }
  };

  const isFormValid =
    title.trim() !== "" &&
    numberOfTasks !== "" &&
    Number(numberOfTasks) > 0 &&
    courseUnitId !== "" &&
    scopeRows.length > 0 &&
    scopeRows.every(
      (r) =>
        r.percent !== "" &&
        Number(r.percent) >= 0 &&
        Number(r.percent) <= 100,
    );

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar showBackButton={true} />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-2">
              <h2 className="text-2xl font-bold text-gray-900">
                Automatyczne Ocenianie Sprawdzianów
              </h2>
              <div className="relative group">
                <div className="w-5 h-5 rounded-full bg-gray-200 text-gray-500 flex items-center justify-center text-xs font-bold cursor-help">
                  ?
                </div>
                <div className="absolute bottom-full left-0 mb-2 w-72 p-2 bg-gray-800 text-white text-xs rounded-lg shadow-lg opacity-0 pointer-events-none group-hover:opacity-100 transition-opacity duration-200 z-10">
                  Utwórz szablon sprawdzianu z kryteriami oceniania, a następnie
                  prześlij prace studentów do automatycznej oceny przez AI.
                </div>
              </div>
            </div>
            <button
              onClick={() => setShowForm(!showForm)}
              className="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-lg transition duration-200"
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
                  d={showForm ? "M6 18L18 6M6 6l12 12" : "M12 4v16m8-8H4"}
                />
              </svg>
              {showForm ? "Anuluj" : "Utwórz nowy szablon"}
            </button>
          </div>

          {/* Create form */}
          <div
            className={`grid transition-all duration-300 ease-in-out ${showForm ? "grid-rows-[1fr] opacity-100 mb-6" : "grid-rows-[0fr] opacity-0"}`}
          >
            <div className="overflow-hidden">
              <form
                onSubmit={handleSubmit}
                className="p-5 bg-gray-50 border border-gray-200 rounded-lg space-y-4"
              >
              <h3 className="text-lg font-semibold text-gray-800">
                Nowy szablon sprawdzianu
              </h3>

              <ExamFormFields
                title={title}
                onTitleChange={setTitle}
                numberOfTasks={numberOfTasks}
                onNumberOfTasksChange={setNumberOfTasks}
                courseUnitId={courseUnitId}
                onCourseUnitIdChange={setCourseUnitId}
                scopeRows={scopeRows}
                onScopeRowsChange={setScopeRows}
                groups={groups}
              />

              <div className="flex justify-end gap-3 pt-2">
                <button
                  type="button"
                  onClick={resetForm}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition duration-200"
                >
                  Anuluj
                </button>
                <button
                  type="submit"
                  disabled={!isFormValid || submitting}
                  className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 rounded-lg transition duration-200"
                >
                  {submitting ? "Tworzenie..." : "Utwórz szablon"}
                </button>
              </div>
              </form>
            </div>
          </div>

          {loading && (
            <div className="flex items-center justify-center py-12">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
              <span className="ml-3 text-gray-600">Ładowanie...</span>
            </div>
          )}

          {!loading && exams.length === 0 && (
            <div className="text-center py-12 text-gray-500">
              <svg
                className="w-12 h-12 mx-auto mb-3 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                />
              </svg>
              <p>Nie masz jeszcze żadnych szablonów sprawdzianów.</p>
              <p className="text-sm mt-1">
                Kliknij „Utwórz nowy szablon", aby rozpocząć.
              </p>
            </div>
          )}

          {!loading && exams.length > 0 && (
            <div className="space-y-3">
              {exams.map((exam) => (
                <button
                  key={exam.id}
                  onClick={() => handleExamClick(exam)}
                  className="w-full text-left p-4 bg-gray-50 hover:bg-indigo-50 border border-gray-200 hover:border-indigo-200 rounded-lg transition-all duration-200 cursor-pointer"
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <h4 className="font-semibold text-gray-900">
                        {exam.title}
                      </h4>
                      <span className="text-sm text-indigo-600 font-medium">
                        {getCourseName(exam.courseUnitId, groups)}
                      </span>
                    </div>
                    <svg
                      className="w-5 h-5 text-gray-400"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M9 5l7 7-7 7"
                      />
                    </svg>
                  </div>
                  <div className="flex gap-6 mt-2 text-xs text-gray-500">
                    <span>Utworzono: {formatDate(exam.createdAt)}</span>
                    <span>Modyfikacja: {formatDate(exam.lastUpdated)}</span>
                  </div>
                </button>
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
};
