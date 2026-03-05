import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useRole, LecturerOnly, StudentOnly } from "@/features/auth";
import { Navbar } from "@/components/Navbar";
import { formatGrade, type Group, type Grade } from "@/features/course";

export const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const { isStudent, isLecturer } = useRole();
  const [groups, setGroups] = useState<Group[]>([]);
  const [loadingGroups, setLoadingGroups] = useState(false);
  const [syncingGroups, setSyncingGroups] = useState(false);
  const [lastActivity, setLastActivity] = useState<Grade[]>([]);
  const [loadingActivity, setLoadingActivity] = useState(false);

  const fetchGroups = async () => {
    setLoadingGroups(true);

    try {
      const response = await fetch("/gradebook/groups/api/fetch", {
        method: "GET",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        const data = await response.json();
        setGroups(data);
      } else {
        setGroups([]);
      }
    } catch (error) {
      console.error("Error fetching groups:", error);
      setGroups([]);
    } finally {
      setLoadingGroups(false);
    }
  };

  useEffect(() => {
    fetchGroups();
  }, []);

  const handleSyncGroups = async () => {
    if (!isLecturer) return; // Only lecturers can sync groups
    setSyncingGroups(true);
    try {
      await fetch("/gradebook/groups/api/update", {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
      });
    } catch (error) {
      console.error("Error syncing groups:", error);
    } finally {
      setSyncingGroups(false);
      await fetchGroups();
    }
  };

  // Fetch last activity for students
  useEffect(() => {
    if (!isStudent) return; // Only fetch for students

    const fetchLastActivity = async () => {
      setLoadingActivity(true);

      try {
        const response = await fetch(
          "/gradebook/grades/api/fetch/lastActivity",
          {
            method: "GET",
            credentials: "include",
            headers: {
              "Content-Type": "application/json",
            },
          },
        );

        if (response.ok) {
          const data = await response.json();
          setLastActivity(data);
        } else {
          setLastActivity([]);
        }
      } catch (error) {
        console.error("Error fetching last activity:", error);
        setLastActivity([]);
      } finally {
        setLoadingActivity(false);
      }
    };

    fetchLastActivity();
  }, [isStudent]);

  // Helper function to format date
  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return "Przed chwilą";
    if (diffMins < 60) return `${diffMins} min temu`;
    if (diffHours < 24) return `${diffHours} godz. temu`;
    if (diffDays < 7) return `${diffDays} dni temu`;

    return date.toLocaleDateString("pl-PL", {
      day: "numeric",
      month: "short",
      year: diffDays < 365 ? undefined : "numeric",
    });
  };

  // Helper function to get course name from courseUnitId
  const getCourseName = (courseUnitId: string): string => {
    const group = groups.find((g) => g.courseUnitId === courseUnitId);
    return group?.courseName || "Nieznany przedmiot";
  };

  const handleGroupSelect = (group: Group) => {
    if (isStudent) {
      navigate(`/course/performance/${group.courseUnitId}`);
    } else if (isLecturer) {
      navigate(`/course/manage/${group.id}`);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Welcome Card */}
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-800 mb-2">Witaj!</h2>
          <StudentOnly>
            <h3 className="text-gray-600">
              Wybierz grupę zajęciową, aby zobaczyć swoje oceny, obecności lub
              podsumowanie
            </h3>
          </StudentOnly>
          <LecturerOnly>
            <h3 className="text-gray-600">
              Wybierz grupę zajęciową, aby zarządzać ocenami i obecnościami
              studentów
            </h3>
          </LecturerOnly>
        </div>

        {/* Automatic Grading Panel - Lecturer Only */}
        <LecturerOnly>
          <div className="bg-white rounded-lg shadow border-l-4 border-indigo-500 p-5 mb-6">
            <div className="flex items-center justify-between gap-4">
              <div className="flex items-center gap-4">
                <div className="w-9 h-9 bg-indigo-50 rounded-md flex items-center justify-center shrink-0">
                  <svg
                    className="w-5 h-5 text-indigo-600"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"
                    />
                  </svg>
                </div>
                <div>
                  <div className="flex items-center gap-2 mb-0.5">
                    <h3 className="text-sm font-semibold text-gray-800">
                      Ocenianie sprawdzianów
                    </h3>
                    <span className="px-1.5 py-0.5 bg-indigo-100 text-indigo-700 text-xs font-medium rounded">
                      AI
                    </span>
                  </div>
                  <p className="text-xs text-gray-500">
                    Automatyczna analiza odpowiedzi i przyznawanie punktów według zdefiniowanych kryteriów
                  </p>
                </div>
              </div>
              <button
                onClick={() => navigate("/exams")}
                className="shrink-0 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-lg transition duration-200"
              >
                Przejdź do oceniania
              </button>
            </div>
          </div>
        </LecturerOnly>

        {/* Groups Section - Visible to both students and lecturers */}
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-800">
              Grupy zajęciowe
            </h3>
            <LecturerOnly>
              <div className="flex items-center gap-2">
                <div className="relative group">
                  <div className="w-5 h-5 rounded-full bg-gray-200 text-gray-500 flex items-center justify-center text-xs font-bold cursor-help">
                    ?
                  </div>
                  <div className="absolute bottom-full right-0 mb-2 w-64 p-2 bg-gray-800 text-white text-xs rounded-lg shadow-lg opacity-0 pointer-events-none group-hover:opacity-100 transition-opacity duration-200 z-10">
                    Synchronizacja pobiera grupy zajęciowe z systemu USOS. Ta
                    operacja może potrwać dłuższą chwilę.
                  </div>
                </div>
                <button
                  onClick={handleSyncGroups}
                  disabled={syncingGroups}
                  className="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 text-white text-sm font-medium rounded-lg transition duration-200"
                >
                  <svg
                    className={`w-4 h-4 ${syncingGroups ? "animate-spin" : ""}`}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
                    />
                  </svg>
                  {syncingGroups ? "Synchronizowanie..." : "Synchronizuj"}
                </button>
              </div>
            </LecturerOnly>
          </div>

          {loadingGroups && (
            <div className="flex items-center justify-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
              <span className="ml-3 text-gray-600">Ładuje...</span>
            </div>
          )}

          {!loadingGroups && groups.length === 0 && (
            <div className="text-center py-8 text-gray-500">
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
                  d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                />
              </svg>
              <StudentOnly>
                <p>
                  Nie należysz jeszcze do żadnej grupy. Wykładowca musi wykonać
                  synchronizację.
                </p>
              </StudentOnly>
              <LecturerOnly>
                <p>
                  Wykonaj synchronizację, aby załadować grupy zajęciowe z
                  systemu USOS.
                </p>
              </LecturerOnly>
            </div>
          )}

          {!loadingGroups && groups.length > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 ">
              {groups.map((group) => (
                <button
                  key={group.id}
                  onClick={() => handleGroupSelect(group)}
                  className="bg-gradient-to-br from-indigo-50 to-blue-50 hover:from-indigo-100 hover:to-blue-100 border-2 border-indigo-200 hover:border-indigo-300 rounded-lg p-5 text-left transition-all duration-200 hover:shadow-md cursor-pointer"
                >
                  <div className="flex items-start justify-between mb-3">
                    <div className="w-10 h-10 bg-indigo-600 rounded-lg flex items-center justify-center">
                      <svg
                        className="w-6 h-6 text-white"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                        />
                      </svg>
                    </div>
                    <span className="px-2 py-1 bg-indigo-600 text-white text-xs font-semibold rounded-md">
                      Grupa {group.groupNumber}
                    </span>
                  </div>
                  <h4 className="font-semibold text-gray-900 mb-1 line-clamp-2">
                    {group.courseName}
                  </h4>
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Recent Activity - Student Only */}
        <StudentOnly>
          <div className="mt-6 bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">
              Ostatnia aktywność
            </h3>

            {loadingActivity && (
              <div className="flex items-center justify-center py-8">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
                <span className="ml-3 text-gray-600">Ładowanie...</span>
              </div>
            )}

            {!loadingActivity && lastActivity.length === 0 && (
                <div className="text-center py-8 text-gray-500">
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
                      d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"
                    />
                  </svg>
                  <p>Brak ostatnich ocen</p>
                </div>
              )}

            {!loadingActivity && lastActivity.length > 0 && (
              <div className="space-y-3">
                {lastActivity.map((grade) => (
                  <div
                    key={grade.id}
                    className="flex items-center justify-between p-4 bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors"
                  >
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <h4 className="font-semibold text-gray-900">
                          {grade.title}
                        </h4>
                        <span className="px-2 py-0.5 bg-indigo-100 text-indigo-700 text-xs font-medium rounded">
                          {getCourseName(grade.courseUnitId)}
                        </span>
                      </div>
                      <p className="text-sm text-gray-500">
                        Zaktualizowano {formatDate(grade.lastUpdated)}
                      </p>
                    </div>
                    <div className="ml-4">
                      <span
                        className={`inline-flex items-center justify-center w-12 h-12 rounded-full font-bold text-lg ${
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
            )}
          </div>
        </StudentOnly>
      </main>
    </div>
  );
};
