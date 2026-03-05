import React from "react";
import { getStudentName, getStudent, sortedStudentIds } from "../utils/course.utils";
import type { StudentDto } from "../types/group.types";

const SINGLE_GRADE_OPTIONS = [
  { value: "5.0", label: "5.0" },
  { value: "4.5", label: "4.5" },
  { value: "4.0", label: "4.0" },
  { value: "3.5", label: "3.5" },
  { value: "3.0", label: "3.0" },
  { value: "2.5", label: "2.5" },
  { value: "2.0", label: "2.0" },
  { value: "0.1", label: "+ (plus)" },
  { value: "0.01", label: "- (minus)" },
];

const LIST_GRADE_OPTIONS = [
  { value: "", label: "—" },
  { value: "5.0", label: "5.0" },
  { value: "4.5", label: "4.5" },
  { value: "4.0", label: "4.0" },
  { value: "3.5", label: "3.5" },
  { value: "3.0", label: "3.0" },
  { value: "2.5", label: "2.5" },
  { value: "2.0", label: "2.0" },
  { value: "0.1", label: "+ (plus)" },
];

// --- Single Grade Modal ---
interface SingleGradeModalProps {
  participants: StudentDto[];
  studentId: string;
  title: string;
  onTitleChange: (v: string) => void;
  value: string;
  onValueChange: (v: string) => void;
  submitting: boolean;
  onSubmit: () => void;
  onClose: () => void;
}

export const SingleGradeModal: React.FC<SingleGradeModalProps> = ({
  participants,
  studentId,
  title,
  onTitleChange,
  value,
  onValueChange,
  submitting,
  onSubmit,
  onClose,
}) => (
  <div className="fixed inset-0 z-50 flex items-center justify-center">
    <div className="absolute inset-0 bg-black/40" onClick={onClose} />
    <div className="relative bg-white rounded-lg shadow-xl w-full max-w-md mx-4 p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-1">Dodaj ocenę</h3>
      <p className="text-sm text-gray-500 mb-4">
        {getStudentName(participants, studentId)}
      </p>

      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Tytuł
          </label>
          <input
            type="text"
            value={title}
            onChange={(e) => onTitleChange(e.target.value)}
            placeholder="np. Kolokwium 1"
            className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Ocena
          </label>
          <select
            value={value}
            onChange={(e) => onValueChange(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value="">Wybierz ocenę</option>
            {SINGLE_GRADE_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="flex justify-end gap-3 mt-6">
        <button
          onClick={onClose}
          className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
        >
          Anuluj
        </button>
        <button
          onClick={onSubmit}
          disabled={submitting || !title.trim() || !value}
          className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 rounded-lg transition-colors"
        >
          {submitting ? "Zapisywanie..." : "Zapisz"}
        </button>
      </div>
    </div>
  </div>
);

// --- Edit Grade Modal ---
interface EditGradeModalProps {
  participants: StudentDto[];
  studentId: string;
  title: string;
  onTitleChange: (v: string) => void;
  value: string;
  onValueChange: (v: string) => void;
  submitting: boolean;
  onSubmit: () => void;
  onClose: () => void;
}

export const EditGradeModal: React.FC<EditGradeModalProps> = ({
  participants,
  studentId,
  title,
  onTitleChange,
  value,
  onValueChange,
  submitting,
  onSubmit,
  onClose,
}) => (
  <div className="fixed inset-0 z-50 flex items-center justify-center">
    <div className="absolute inset-0 bg-black/40" onClick={onClose} />
    <div className="relative bg-white rounded-lg shadow-xl w-full max-w-md mx-4 p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-1">
        Edytuj ocenę
      </h3>
      <p className="text-sm text-gray-500 mb-4">
        {getStudentName(participants, studentId)}
      </p>

      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Tytuł
          </label>
          <input
            type="text"
            value={title}
            onChange={(e) => onTitleChange(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Ocena
          </label>
          <select
            value={value}
            onChange={(e) => onValueChange(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          >
            {SINGLE_GRADE_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="flex justify-end gap-3 mt-6">
        <button
          onClick={onClose}
          className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
        >
          Anuluj
        </button>
        <button
          onClick={onSubmit}
          disabled={submitting || !value}
          className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 rounded-lg transition-colors"
        >
          {submitting ? "Zapisywanie..." : "Zapisz"}
        </button>
      </div>
    </div>
  </div>
);

// --- List Grade Modal ---
interface ListGradeModalProps {
  participants: StudentDto[];
  title: string;
  onTitleChange: (v: string) => void;
  grades: Record<string, string>;
  onGradeChange: (studentId: string, value: string) => void;
  submitting: boolean;
  onSubmit: () => void;
  onClose: () => void;
}

export const ListGradeModal: React.FC<ListGradeModalProps> = ({
  participants,
  title,
  onTitleChange,
  grades,
  onGradeChange,
  submitting,
  onSubmit,
  onClose,
}) => (
  <div className="fixed inset-0 z-50 flex items-center justify-center">
    <div className="absolute inset-0 bg-black/40" onClick={onClose} />
    <div className="relative bg-white rounded-lg shadow-xl w-full max-w-2xl mx-4 p-6 max-h-[90vh] overflow-y-auto">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">
        Dodaj oceny z listy
      </h3>

      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Tytuł
        </label>
        <input
          type="text"
          value={title}
          onChange={(e) => onTitleChange(e.target.value)}
          placeholder="np. Kolokwium 1"
          className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
        />
      </div>

      <div className="border border-gray-200 rounded-lg overflow-hidden">
        <div className="grid grid-cols-[1fr_140px] px-4 py-2 bg-gray-100 text-xs font-semibold text-gray-600 uppercase">
          <span>Student</span>
          <span>Ocena</span>
        </div>
        <div className="divide-y divide-gray-100">
          {sortedStudentIds(participants, participants.map((p) => p.id)).map(
            (studentId) => {
              const student = getStudent(participants, studentId);
              return (
                <div
                  key={studentId}
                  className="grid grid-cols-[1fr_140px] items-center px-4 py-2"
                >
                  <div>
                    <span className="text-sm font-medium text-gray-900">
                      {getStudentName(participants, studentId)}
                    </span>
                    {student && (
                      <span className="ml-2 text-xs text-gray-500">
                        ({student.studentNumber})
                      </span>
                    )}
                  </div>
                  <select
                    value={grades[studentId] ?? ""}
                    onChange={(e) => onGradeChange(studentId, e.target.value)}
                    className="px-2 py-1.5 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                  >
                    {LIST_GRADE_OPTIONS.map((opt) => (
                      <option key={opt.value} value={opt.value}>
                        {opt.label}
                      </option>
                    ))}
                  </select>
                </div>
              );
            },
          )}
        </div>
      </div>

      <div className="flex justify-end gap-3 mt-6">
        <button
          onClick={onClose}
          className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
        >
          Anuluj
        </button>
        <button
          onClick={onSubmit}
          disabled={
            submitting ||
            !title.trim() ||
            Object.values(grades).every((v) => v === "")
          }
          className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 rounded-lg transition-colors"
        >
          {submitting ? "Zapisywanie..." : "Zapisz"}
        </button>
      </div>
    </div>
  </div>
);
