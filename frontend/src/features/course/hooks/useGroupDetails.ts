import { useState, useEffect } from "react";
import type { GroupDetails } from "../types/group.types";

interface UseGroupDetailsReturn {
  group: GroupDetails | null;
  loading: boolean;
}

export const useGroupDetails = (
  groupId: string | undefined,
): UseGroupDetailsReturn => {
  const [group, setGroup] = useState<GroupDetails | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchGroup = async () => {
      setLoading(true);
      try {
        const response = await fetch(
          `/gradebook/groups/api/fetch/${groupId}`,
          {
            credentials: "include",
            headers: { "Content-Type": "application/json" },
          },
        );
        if (response.ok) {
          const data: GroupDetails = await response.json();
          setGroup(data);
        }
      } catch (error) {
        console.error("Error fetching group:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchGroup();
  }, [groupId]);

  return { group, loading };
};
