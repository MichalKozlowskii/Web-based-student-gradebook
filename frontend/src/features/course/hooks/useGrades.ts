import { useState, useCallback } from "react";
import type { GroupDetails } from "../types/group.types";
import type { Grade, GradeListItem, GradeList } from "../types/grade.types";

interface UseGradesOptions {
  onMutate?: () => void;
}

export interface UseGradesReturn {
  grades: Record<string, Grade[]>;
  gradesLoading: boolean;
  gradesError: string | null;
  fetchGrades: () => Promise<void>;
  gradesAttempted: boolean;
  invalidate: () => void;

  expandedGradeStudents: Set<string>;
  toggleGradeStudent: (studentId: string) => void;

  addGradeForStudent: string | null;
  addGradeTitle: string;
  setAddGradeTitle: (v: string) => void;
  addGradeValue: string;
  setAddGradeValue: (v: string) => void;
  addGradeSubmitting: boolean;
  openSingleGradeModal: (studentId: string) => void;
  closeSingleGradeModal: () => void;
  handleAddSingleGrade: () => Promise<void>;

  showListGradeModal: boolean;
  listGradeTitle: string;
  setListGradeTitle: (v: string) => void;
  listGrades: Record<string, string>;
  setListGrades: React.Dispatch<React.SetStateAction<Record<string, string>>>;
  listGradeSubmitting: boolean;
  openListGradeModal: () => void;
  closeListGradeModal: () => void;
  handleAddListGrade: () => Promise<void>;

  editGrade: Grade | null;
  editGradeTitle: string;
  setEditGradeTitle: (v: string) => void;
  editGradeValue: string;
  setEditGradeValue: (v: string) => void;
  editGradeSubmitting: boolean;
  openEditGradeModal: (grade: Grade) => void;
  closeEditGradeModal: () => void;
  handleEditGrade: () => Promise<void>;

  deletingGradeId: string | null;
  handleDeleteGrade: (gradeId: string) => Promise<void>;
}

export const useGrades = (
  group: GroupDetails | null,
  options?: UseGradesOptions,
): UseGradesReturn => {
  const [grades, setGrades] = useState<Record<string, Grade[]>>({});
  const [gradesLoading, setGradesLoading] = useState(false);
  const [gradesError, setGradesError] = useState<string | null>(null);
  const [gradesAttempted, setGradesAttempted] = useState(false);
  const [expandedGradeStudents, setExpandedGradeStudents] = useState<
    Set<string>
  >(new Set());

  // Single grade modal
  const [addGradeForStudent, setAddGradeForStudent] = useState<string | null>(
    null,
  );
  const [addGradeTitle, setAddGradeTitle] = useState("");
  const [addGradeValue, setAddGradeValue] = useState("");
  const [addGradeSubmitting, setAddGradeSubmitting] = useState(false);

  // List grade modal
  const [showListGradeModal, setShowListGradeModal] = useState(false);
  const [listGradeTitle, setListGradeTitle] = useState("");
  const [listGrades, setListGrades] = useState<Record<string, string>>({});
  const [listGradeSubmitting, setListGradeSubmitting] = useState(false);

  // Edit grade modal
  const [editGrade, setEditGrade] = useState<Grade | null>(null);
  const [editGradeTitle, setEditGradeTitle] = useState("");
  const [editGradeValue, setEditGradeValue] = useState("");
  const [editGradeSubmitting, setEditGradeSubmitting] = useState(false);

  // Delete
  const [deletingGradeId, setDeletingGradeId] = useState<string | null>(null);

  const fetchGrades = useCallback(async () => {
    if (!group) return;
    setGradesAttempted(true);
    setGradesLoading(true);
    setGradesError(null);
    try {
      const response = await fetch(
        `/gradebook/grades/api/fetch/course/${group.courseUnitId}/${group.groupNumber}`,
        {
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        },
      );
      if (response.ok) {
        setGrades(await response.json());
      } else {
        setGradesError("Nie udało się załadować ocen");
      }
    } catch (error) {
      console.error("Error fetching grades:", error);
      setGradesError("Nie udało się załadować ocen");
    } finally {
      setGradesLoading(false);
    }
  }, [group]);

  const invalidate = useCallback(() => {
    setGradesAttempted(false);
  }, []);

  const toggleGradeStudent = (studentId: string) => {
    setExpandedGradeStudents((prev) => {
      const next = new Set(prev);
      if (next.has(studentId)) next.delete(studentId);
      else next.add(studentId);
      return next;
    });
  };

  // --- Single grade ---
  const openSingleGradeModal = (studentId: string) => {
    setAddGradeForStudent(studentId);
    setAddGradeTitle("");
    setAddGradeValue("");
  };

  const closeSingleGradeModal = () => {
    setAddGradeForStudent(null);
    setAddGradeTitle("");
    setAddGradeValue("");
  };

  const handleAddSingleGrade = async () => {
    if (!group || !addGradeForStudent || !addGradeTitle.trim() || !addGradeValue)
      return;
    setAddGradeSubmitting(true);
    try {
      const response = await fetch("/gradebook/grades/api/add", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          courseUnitId: group.courseUnitId,
          studentId: addGradeForStudent,
          title: addGradeTitle.trim(),
          grade: addGradeValue,
        }),
      });
      if (response.ok) {
        closeSingleGradeModal();
        setGradesAttempted(false);
        options?.onMutate?.();
      }
    } catch (error) {
      console.error("Error adding grade:", error);
    } finally {
      setAddGradeSubmitting(false);
    }
  };

  // --- List grade ---
  const openListGradeModal = () => {
    setShowListGradeModal(true);
    setListGradeTitle("");
    const initial: Record<string, string> = {};
    group?.participants.forEach((p) => {
      initial[p.id] = "";
    });
    setListGrades(initial);
  };

  const closeListGradeModal = () => {
    setShowListGradeModal(false);
    setListGradeTitle("");
    setListGrades({});
  };

  const handleAddListGrade = async () => {
    if (!group || !listGradeTitle.trim()) return;
    const items: GradeListItem[] = Object.entries(listGrades)
      .filter(([, grade]) => grade !== "")
      .map(([studentId, grade]) => ({ studentId, grade }));
    if (items.length === 0) return;

    const payload: GradeList = {
      title: listGradeTitle.trim(),
      courseUnitId: group.courseUnitId,
      groupNumber: group.groupNumber,
      list: items,
    };

    setListGradeSubmitting(true);
    try {
      const response = await fetch("/gradebook/grades/api/add/list", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      if (response.ok) {
        closeListGradeModal();
        setGradesAttempted(false);
        options?.onMutate?.();
      }
    } catch (error) {
      console.error("Error adding grades from list:", error);
    } finally {
      setListGradeSubmitting(false);
    }
  };

  // --- Edit grade ---
  const openEditGradeModal = (grade: Grade) => {
    setEditGrade(grade);
    setEditGradeTitle(grade.title);
    setEditGradeValue(grade.grade);
  };

  const closeEditGradeModal = () => {
    setEditGrade(null);
    setEditGradeTitle("");
    setEditGradeValue("");
  };

  const handleEditGrade = async () => {
    if (!editGrade || !editGradeValue) return;
    setEditGradeSubmitting(true);
    try {
      const body: { grade: string; title?: string } = { grade: editGradeValue };
      if (editGradeTitle.trim() !== editGrade.title) {
        body.title = editGradeTitle.trim();
      }
      const response = await fetch(
        `/gradebook/grades/api/update/${editGrade.id}`,
        {
          method: "PATCH",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(body),
        },
      );
      if (response.ok) {
        closeEditGradeModal();
        setGradesAttempted(false);
        options?.onMutate?.();
      }
    } catch (error) {
      console.error("Error editing grade:", error);
    } finally {
      setEditGradeSubmitting(false);
    }
  };

  // --- Delete grade ---
  const handleDeleteGrade = async (gradeId: string) => {
    setDeletingGradeId(gradeId);
    try {
      const response = await fetch(`/gradebook/grades/api/delete/${gradeId}`, {
        method: "DELETE",
        credentials: "include",
      });
      if (response.ok) {
        setGradesAttempted(false);
        options?.onMutate?.();
      }
    } catch (error) {
      console.error("Error deleting grade:", error);
    } finally {
      setDeletingGradeId(null);
    }
  };

  return {
    grades,
    gradesLoading,
    gradesError,
    fetchGrades,
    gradesAttempted,
    invalidate,
    expandedGradeStudents,
    toggleGradeStudent,
    addGradeForStudent,
    addGradeTitle,
    setAddGradeTitle,
    addGradeValue,
    setAddGradeValue,
    addGradeSubmitting,
    openSingleGradeModal,
    closeSingleGradeModal,
    handleAddSingleGrade,
    showListGradeModal,
    listGradeTitle,
    setListGradeTitle,
    listGrades,
    setListGrades,
    listGradeSubmitting,
    openListGradeModal,
    closeListGradeModal,
    handleAddListGrade,
    editGrade,
    editGradeTitle,
    setEditGradeTitle,
    editGradeValue,
    setEditGradeValue,
    editGradeSubmitting,
    openEditGradeModal,
    closeEditGradeModal,
    handleEditGrade,
    deletingGradeId,
    handleDeleteGrade,
  };
};
