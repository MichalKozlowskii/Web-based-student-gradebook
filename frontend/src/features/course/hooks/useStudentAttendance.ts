import { useState, useCallback } from "react";
import type { Attendance } from "../types/attendance.types";

interface UseStudentAttendanceReturn {
  attendance: Attendance[];
  attendanceLoading: boolean;
  attendanceError: string | null;
  fetchAttendance: () => Promise<void>;
  attendanceAttempted: boolean;
}

export const useStudentAttendance = (
  courseUnitId: string | undefined,
): UseStudentAttendanceReturn => {
  const [attendance, setAttendance] = useState<Attendance[]>([]);
  const [attendanceLoading, setAttendanceLoading] = useState(false);
  const [attendanceError, setAttendanceError] = useState<string | null>(null);
  const [attendanceAttempted, setAttendanceAttempted] = useState(false);

  const fetchAttendance = useCallback(async () => {
    if (!courseUnitId) return;
    setAttendanceAttempted(true);
    setAttendanceLoading(true);
    setAttendanceError(null);
    try {
      const response = await fetch(
        `/gradebook/attendance/api/fetch/${courseUnitId}`,
        {
          method: "GET",
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
  }, [courseUnitId]);

  return {
    attendance,
    attendanceLoading,
    attendanceError,
    fetchAttendance,
    attendanceAttempted,
  };
};
