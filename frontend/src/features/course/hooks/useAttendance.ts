import { useState, useCallback } from "react";
import type { GroupDetails } from "../types/group.types";
import type { Attendance, AttendanceStatus } from "../types/attendance.types";

interface UseAttendanceOptions {
  onMutate?: () => void;
}

export interface UseAttendanceReturn {
  attendance: Record<string, Attendance[]>;
  attendanceLoading: boolean;
  attendanceError: string | null;
  fetchAttendance: () => Promise<void>;
  attendanceAttempted: boolean;
  invalidate: () => void;

  expandedAttendanceStudents: Set<string>;
  toggleAttendanceStudent: (studentId: string) => void;

  editingAttendanceId: string | null;
  setEditingAttendanceId: (id: string | null) => void;
  updatingAttendanceId: string | null;
  handleUpdateAttendance: (
    record: Attendance,
    studentId: string,
    newStatus: AttendanceStatus,
  ) => Promise<void>;

  showAttendanceModal: boolean;
  attendanceStatuses: Record<string, AttendanceStatus>;
  setAttendanceStatuses: React.Dispatch<
    React.SetStateAction<Record<string, AttendanceStatus>>
  >;
  attendanceSubmitting: boolean;
  openAttendanceModal: () => void;
  closeAttendanceModal: () => void;
  handleCreateAttendance: () => Promise<void>;
}

export const useAttendance = (
  group: GroupDetails | null,
  options?: UseAttendanceOptions,
): UseAttendanceReturn => {
  const [attendance, setAttendance] = useState<Record<string, Attendance[]>>(
    {},
  );
  const [attendanceLoading, setAttendanceLoading] = useState(false);
  const [attendanceError, setAttendanceError] = useState<string | null>(null);
  const [attendanceAttempted, setAttendanceAttempted] = useState(false);
  const [expandedAttendanceStudents, setExpandedAttendanceStudents] = useState<
    Set<string>
  >(new Set());

  const [editingAttendanceId, setEditingAttendanceId] = useState<string | null>(
    null,
  );
  const [updatingAttendanceId, setUpdatingAttendanceId] = useState<
    string | null
  >(null);

  // Create modal
  const [showAttendanceModal, setShowAttendanceModal] = useState(false);
  const [attendanceStatuses, setAttendanceStatuses] = useState<
    Record<string, AttendanceStatus>
  >({});
  const [attendanceSubmitting, setAttendanceSubmitting] = useState(false);

  const fetchAttendance = useCallback(async () => {
    if (!group) return;
    setAttendanceAttempted(true);
    setAttendanceLoading(true);
    setAttendanceError(null);
    try {
      const response = await fetch(
        `/gradebook/attendance/api/fetch/${group.courseUnitId}/${group.groupNumber}`,
        {
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        },
      );
      if (response.ok) {
        setAttendance(await response.json());
      } else {
        setAttendanceError("Nie udało się załadować obecności");
      }
    } catch (error) {
      console.error("Error fetching attendance:", error);
      setAttendanceError("Nie udało się załadować obecności");
    } finally {
      setAttendanceLoading(false);
    }
  }, [group]);

  const invalidate = useCallback(() => {
    setAttendanceAttempted(false);
  }, []);

  const toggleAttendanceStudent = (studentId: string) => {
    setExpandedAttendanceStudents((prev) => {
      const next = new Set(prev);
      if (next.has(studentId)) next.delete(studentId);
      else next.add(studentId);
      return next;
    });
  };

  const handleUpdateAttendance = async (
    record: Attendance,
    studentId: string,
    newStatus: AttendanceStatus,
  ) => {
    setUpdatingAttendanceId(record.id);
    try {
      const response = await fetch(
        `/gradebook/attendance/api/lectures/update/${record.lectureId}`,
        {
          method: "PATCH",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            attendanceList: [{ studentId, status: newStatus }],
          }),
        },
      );
      if (response.ok) {
        setAttendance((prev) => {
          const records = prev[studentId] ?? [];
          return {
            ...prev,
            [studentId]: records.map((r) =>
              r.id === record.id ? { ...r, status: newStatus } : r,
            ),
          };
        });
        options?.onMutate?.();
      }
    } catch (error) {
      console.error("Error updating attendance:", error);
    } finally {
      setUpdatingAttendanceId(null);
      setEditingAttendanceId(null);
    }
  };

  const openAttendanceModal = () => {
    setShowAttendanceModal(true);
    const initial: Record<string, AttendanceStatus> = {};
    group?.participants.forEach((p) => {
      initial[p.id] = "ABSENT";
    });
    setAttendanceStatuses(initial);
  };

  const closeAttendanceModal = () => {
    setShowAttendanceModal(false);
    setAttendanceStatuses({});
  };

  const handleCreateAttendance = async () => {
    if (!group) return;
    const items = Object.entries(attendanceStatuses).map(
      ([studentId, status]) => ({ studentId, status }),
    );
    if (items.length === 0) return;

    setAttendanceSubmitting(true);
    try {
      const response = await fetch(
        "/gradebook/attendance/api/lectures/create",
        {
          method: "POST",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            courseUnitId: group.courseUnitId,
            groupNumber: group.groupNumber,
            attendanceList: items,
          }),
        },
      );
      if (response.ok) {
        closeAttendanceModal();
        setAttendanceAttempted(false);
        options?.onMutate?.();
      }
    } catch (error) {
      console.error("Error creating attendance:", error);
    } finally {
      setAttendanceSubmitting(false);
    }
  };

  return {
    attendance,
    attendanceLoading,
    attendanceError,
    fetchAttendance,
    attendanceAttempted,
    invalidate,
    expandedAttendanceStudents,
    toggleAttendanceStudent,
    editingAttendanceId,
    setEditingAttendanceId,
    updatingAttendanceId,
    handleUpdateAttendance,
    showAttendanceModal,
    attendanceStatuses,
    setAttendanceStatuses,
    attendanceSubmitting,
    openAttendanceModal,
    closeAttendanceModal,
    handleCreateAttendance,
  };
};
