import { useState, useEffect } from "react";
import type { Group } from "../types/group.types";

interface UseCourseDetailsReturn {
  course: Group | null;
  loading: boolean;
}

export const useCourseDetails = (
  courseUnitId: string | undefined,
): UseCourseDetailsReturn => {
  const [course, setCourse] = useState<Group | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCourse = async () => {
      setLoading(true);
      try {
        const response = await fetch("/gradebook/groups/api/fetch", {
          method: "GET",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
        });

        if (response.ok) {
          const groups: Group[] = await response.json();
          const found = groups.find((g) => g.courseUnitId === courseUnitId);
          setCourse(found || null);
        }
      } catch (error) {
        console.error("Error fetching course:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchCourse();
  }, [courseUnitId]);

  return { course, loading };
};
