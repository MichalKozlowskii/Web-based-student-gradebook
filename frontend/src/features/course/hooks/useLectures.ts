import { useState, useCallback } from "react";
import type { GroupDetails } from "../types/group.types";
import type {
  Attendance,
  AttendanceStatus,
  LecturePreview,
  Lecture,
} from "../types/attendance.types";

interface UseLecturesOptions {
  onMutate?: () => void;
  onClose?: () => void;
}

export interface UseLecturesReturn {
  lectures: LecturePreview[];
  lecturesLoading: boolean;

  showLecturesModal: boolean;
  fetchLectures: () => Promise<void>;
  closeLecturesModal: () => void;

  expandedLectures: Set<string>;
  toggleLecture: (lectureId: string) => void;
  lectureDetails: Record<string, Lecture>;
  lectureDetailsLoading: Set<string>;

  lectureEdits: Record<string, Record<string, AttendanceStatus>>;
  setLectureAttendanceEdit: (
    lectureId: string,
    studentId: string,
    status: AttendanceStatus,
  ) => void;
  clearLectureEdits: (lectureId: string) => void;
  handleSaveLectureEdits: (lectureId: string) => Promise<void>;
  savingLectureId: string | null;

  editingAttendanceId: string | null;
  setEditingAttendanceId: (id: string | null) => void;

  deletingLectureId: string | null;
  handleDeleteLecture: (lectureId: string) => Promise<void>;
}

export const useLectures = (
  group: GroupDetails | null,
  options?: UseLecturesOptions,
): UseLecturesReturn => {
  const [lectures, setLectures] = useState<LecturePreview[]>([]);
  const [lecturesLoading, setLecturesLoading] = useState(false);
  const [showLecturesModal, setShowLecturesModal] = useState(false);
  const [expandedLectures, setExpandedLectures] = useState<Set<string>>(
    new Set(),
  );
  const [lectureDetails, setLectureDetails] = useState<Record<string, Lecture>>(
    {},
  );
  const [lectureDetailsLoading, setLectureDetailsLoading] = useState<
    Set<string>
  >(new Set());
  const [lectureEdits, setLectureEdits] = useState<
    Record<string, Record<string, AttendanceStatus>>
  >({});
  const [savingLectureId, setSavingLectureId] = useState<string | null>(null);
  const [deletingLectureId, setDeletingLectureId] = useState<string | null>(
    null,
  );
  const [lecturesModified, setLecturesModified] = useState(false);
  const [editingAttendanceId, setEditingAttendanceId] = useState<string | null>(
    null,
  );

  const fetchLectures = useCallback(async () => {
    if (!group) return;
    setLecturesLoading(true);
    try {
      const response = await fetch(
        `/gradebook/attendance/api/lectures/fetch/${group.courseUnitId}/${group.groupNumber}`,
        {
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        },
      );
      if (response.ok) {
        setLectures(await response.json());
        setShowLecturesModal(true);
      }
    } catch (error) {
      console.error("Error fetching lectures:", error);
    } finally {
      setLecturesLoading(false);
    }
  }, [group]);

  const closeLecturesModal = () => {
    setShowLecturesModal(false);
    setExpandedLectures(new Set());
    setLectureEdits({});
    if (lecturesModified) {
      options?.onClose?.();
      setLecturesModified(false);
    }
  };

  const fetchLectureDetail = async (lectureId: string) => {
    setLectureDetailsLoading((prev) => new Set(prev).add(lectureId));
    try {
      const response = await fetch(
        `/gradebook/attendance/api/lectures/fetch/${lectureId}`,
        {
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        },
      );
      if (response.ok) {
        const data: Lecture = await response.json();
        setLectureDetails((prev) => ({ ...prev, [lectureId]: data }));
      }
    } catch (error) {
      console.error("Error fetching lecture detail:", error);
    } finally {
      setLectureDetailsLoading((prev) => {
        const next = new Set(prev);
        next.delete(lectureId);
        return next;
      });
    }
  };

  const toggleLecture = (lectureId: string) => {
    setExpandedLectures((prev) => {
      const next = new Set(prev);
      if (next.has(lectureId)) {
        next.delete(lectureId);
      } else {
        next.add(lectureId);
        if (!lectureDetails[lectureId]) {
          fetchLectureDetail(lectureId);
        }
      }
      return next;
    });
  };

  const setLectureAttendanceEdit = (
    lectureId: string,
    studentId: string,
    newStatus: AttendanceStatus,
  ) => {
    setLectureEdits((prev) => ({
      ...prev,
      [lectureId]: { ...prev[lectureId], [studentId]: newStatus },
    }));
    setEditingAttendanceId(null);
  };

  const clearLectureEdits = (lectureId: string) => {
    setLectureEdits((prev) => {
      const next = { ...prev };
      delete next[lectureId];
      return next;
    });
  };

  const handleSaveLectureEdits = async (lectureId: string) => {
    const detail = lectureDetails[lectureId];
    if (!detail) return;
    const edits = lectureEdits[lectureId];
    if (!edits || Object.keys(edits).length === 0) return;

    const fullList = detail.attendanceList.map((r: Attendance) => ({
      studentId: r.studentId,
      status: edits[r.studentId] ?? r.status,
    }));

    setSavingLectureId(lectureId);
    try {
      const response = await fetch(
        `/gradebook/attendance/api/lectures/update/${lectureId}`,
        {
          method: "PATCH",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ attendanceList: fullList }),
        },
      );
      if (response.ok) {
        setLectureDetails((prev) => ({
          ...prev,
          [lectureId]: {
            ...detail,
            attendanceList: detail.attendanceList.map((r: Attendance) => ({
              ...r,
              status: edits[r.studentId] ?? r.status,
            })),
          },
        }));
        clearLectureEdits(lectureId);
        setLecturesModified(true);
        options?.onMutate?.();
      }
    } catch (error) {
      console.error("Error saving lecture edits:", error);
    } finally {
      setSavingLectureId(null);
    }
  };

  const handleDeleteLecture = async (lectureId: string) => {
    setDeletingLectureId(lectureId);
    try {
      const response = await fetch(
        `/gradebook/attendance/api/lectures/delete/${lectureId}`,
        {
          method: "DELETE",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        },
      );
      if (response.ok) {
        setLectures((prev) => prev.filter((l) => l.id !== lectureId));
        setLectureDetails((prev) => {
          const next = { ...prev };
          delete next[lectureId];
          return next;
        });
        setLectureEdits((prev) => {
          const next = { ...prev };
          delete next[lectureId];
          return next;
        });
        setExpandedLectures((prev) => {
          const next = new Set(prev);
          next.delete(lectureId);
          return next;
        });
        setLecturesModified(true);
        options?.onMutate?.();
      }
    } catch (error) {
      console.error("Error deleting lecture:", error);
    } finally {
      setDeletingLectureId(null);
    }
  };

  return {
    lectures,
    lecturesLoading,
    showLecturesModal,
    fetchLectures,
    closeLecturesModal,
    expandedLectures,
    toggleLecture,
    lectureDetails,
    lectureDetailsLoading,
    lectureEdits,
    setLectureAttendanceEdit,
    clearLectureEdits,
    handleSaveLectureEdits,
    savingLectureId,
    editingAttendanceId,
    setEditingAttendanceId,
    deletingLectureId,
    handleDeleteLecture,
  };
};
