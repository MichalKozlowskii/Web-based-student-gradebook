import type { Group } from "@/features/course";

export const getCourseName = (
  courseUnitId: string,
  groups: Group[],
): string => {
  const group = groups.find((g) => g.courseUnitId === courseUnitId);
  return group?.courseName || "Nieznany przedmiot";
};

export const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString("pl-PL", {
    day: "numeric",
    month: "long",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

export const getUniqueGroups = (groups: Group[]): Group[] => {
  return groups.filter(
    (g, i, arr) =>
      arr.findIndex((x) => x.courseUnitId === g.courseUnitId) === i,
  );
};
