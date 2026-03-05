import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Navbar } from "@/components/Navbar";
import {
  CourseTabs,
  GradesTab,
  AttendanceTab,
  SummaryTab,
  SingleGradeModal,
  EditGradeModal,
  ListGradeModal,
  AttendanceModal,
  LecturesModal,
  useGroupDetails,
  useGrades,
  useAttendance,
  useLectures,
  useSummary,
  type TabType,
} from "@/features/course";

export const CourseManagePage: React.FC = () => {
  const { groupId } = useParams<{ groupId: string }>();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<TabType>("grades");

  const { group, loading } = useGroupDetails(groupId);

  const summary = useSummary(group);

  const grades = useGrades(group, {
    onMutate: () => summary.invalidate(),
  });

  const attendance = useAttendance(group, {
    onMutate: () => summary.invalidate(),
  });

  const lectures = useLectures(group, {
    onMutate: () => summary.invalidate(),
    onClose: () => attendance.invalidate(),
  });

  // Lazy-load tab data
  const { fetchGrades, gradesAttempted } = grades;
  const { fetchAttendance, attendanceAttempted } = attendance;
  const { fetchSummary, summaryAttempted } = summary;

  useEffect(() => {
    if (!group) return;

    if (activeTab === "grades" && !gradesAttempted) {
      fetchGrades();
    } else if (activeTab === "attendance" && !attendanceAttempted) {
      fetchAttendance();
    } else if (activeTab === "summary" && !summaryAttempted) {
      fetchSummary();
    }
  }, [
    activeTab,
    group,
    gradesAttempted,
    fetchGrades,
    attendanceAttempted,
    fetchAttendance,
    summaryAttempted,
    fetchSummary,
  ]);

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

  if (!group) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar showBackButton={true} />
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center py-16 text-gray-500">
            <p className="text-lg">Nie znaleziono grupy.</p>
            <button
              onClick={() => navigate("/dashboard")}
              className="mt-4 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-lg transition duration-200"
            >
              Wróć do dashboardu
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
        {/* Course Info Card */}
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 bg-indigo-600 rounded-lg flex items-center justify-center">
              <svg
                className="w-7 h-7 text-white"
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
            <div>
              <h2 className="text-xl font-semibold text-gray-900">
                {group.courseName}
              </h2>
              <p className="text-sm text-gray-600">
                Grupa {group.groupNumber} &middot; {group.participants.length}{" "}
                studentów
              </p>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <CourseTabs activeTab={activeTab} onTabChange={setActiveTab}>
          {activeTab === "grades" && (
            <GradesTab
              participants={group.participants}
              grades={grades.grades}
              gradesLoading={grades.gradesLoading}
              gradesError={grades.gradesError}
              expandedGradeStudents={grades.expandedGradeStudents}
              onToggleStudent={grades.toggleGradeStudent}
              onAddGrade={grades.openSingleGradeModal}
              onEditGrade={grades.openEditGradeModal}
              onDeleteGrade={grades.handleDeleteGrade}
              deletingGradeId={grades.deletingGradeId}
              onOpenListGradeModal={grades.openListGradeModal}
            />
          )}
          {activeTab === "attendance" && (
            <AttendanceTab
              participants={group.participants}
              attendance={attendance.attendance}
              attendanceLoading={attendance.attendanceLoading}
              attendanceError={attendance.attendanceError}
              expandedAttendanceStudents={attendance.expandedAttendanceStudents}
              onToggleStudent={attendance.toggleAttendanceStudent}
              editingAttendanceId={attendance.editingAttendanceId}
              onSetEditingAttendanceId={attendance.setEditingAttendanceId}
              updatingAttendanceId={attendance.updatingAttendanceId}
              onUpdateAttendance={attendance.handleUpdateAttendance}
              onOpenAttendanceModal={attendance.openAttendanceModal}
              onOpenLecturesModal={lectures.fetchLectures}
              lecturesLoading={lectures.lecturesLoading}
            />
          )}
          {activeTab === "summary" && (
            <SummaryTab
              participants={group.participants}
              summaries={summary.summaries}
              summaryLoading={summary.summaryLoading}
              summaryError={summary.summaryError}
            />
          )}
        </CourseTabs>
      </main>

      {/* Modals */}
      {grades.addGradeForStudent && (
        <SingleGradeModal
          participants={group.participants}
          studentId={grades.addGradeForStudent}
          title={grades.addGradeTitle}
          onTitleChange={grades.setAddGradeTitle}
          value={grades.addGradeValue}
          onValueChange={grades.setAddGradeValue}
          submitting={grades.addGradeSubmitting}
          onSubmit={grades.handleAddSingleGrade}
          onClose={grades.closeSingleGradeModal}
        />
      )}

      {grades.editGrade && (
        <EditGradeModal
          participants={group.participants}
          studentId={grades.editGrade.studentId}
          title={grades.editGradeTitle}
          onTitleChange={grades.setEditGradeTitle}
          value={grades.editGradeValue}
          onValueChange={grades.setEditGradeValue}
          submitting={grades.editGradeSubmitting}
          onSubmit={grades.handleEditGrade}
          onClose={grades.closeEditGradeModal}
        />
      )}

      {grades.showListGradeModal && (
        <ListGradeModal
          participants={group.participants}
          title={grades.listGradeTitle}
          onTitleChange={grades.setListGradeTitle}
          grades={grades.listGrades}
          onGradeChange={(studentId, value) =>
            grades.setListGrades((prev) => ({ ...prev, [studentId]: value }))
          }
          submitting={grades.listGradeSubmitting}
          onSubmit={grades.handleAddListGrade}
          onClose={grades.closeListGradeModal}
        />
      )}

      {attendance.showAttendanceModal && (
        <AttendanceModal
          participants={group.participants}
          statuses={attendance.attendanceStatuses}
          onStatusChange={(studentId, status) =>
            attendance.setAttendanceStatuses((prev) => ({
              ...prev,
              [studentId]: status,
            }))
          }
          submitting={attendance.attendanceSubmitting}
          onSubmit={attendance.handleCreateAttendance}
          onClose={attendance.closeAttendanceModal}
        />
      )}

      {lectures.showLecturesModal && (
        <LecturesModal
          participants={group.participants}
          lectures={lectures.lectures}
          expandedLectures={lectures.expandedLectures}
          onToggleLecture={lectures.toggleLecture}
          lectureDetails={lectures.lectureDetails}
          lectureDetailsLoading={lectures.lectureDetailsLoading}
          lectureEdits={lectures.lectureEdits}
          onSetAttendanceEdit={lectures.setLectureAttendanceEdit}
          onClearEdits={lectures.clearLectureEdits}
          onSaveEdits={lectures.handleSaveLectureEdits}
          savingLectureId={lectures.savingLectureId}
          editingAttendanceId={lectures.editingAttendanceId}
          onSetEditingAttendanceId={lectures.setEditingAttendanceId}
          deletingLectureId={lectures.deletingLectureId}
          onDeleteLecture={lectures.handleDeleteLecture}
          onClose={lectures.closeLecturesModal}
        />
      )}
    </div>
  );
};
