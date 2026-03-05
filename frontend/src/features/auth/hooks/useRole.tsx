import { useMemo } from "react";
import { useAuth } from "./useAuth";
import { UserRole } from "../types/auth.types";

export interface UseRoleReturn {
  role: UserRole | null;
  hasRole: (requiredRole: UserRole | UserRole[]) => boolean;
  isStudent: boolean;
  isLecturer: boolean;
  hasAnyRole: (roles: UserRole[]) => boolean;
  hasAllRoles: (roles: UserRole[]) => boolean;
}

export const useRole = (): UseRoleReturn => {
  const { user } = useAuth();

  const hasRole = useMemo(
    () =>
      (requiredRole: UserRole | UserRole[]): boolean => {
        if (!user) return false;

        if (Array.isArray(requiredRole)) {
          return requiredRole.includes(user.role);
        }

        return user.role === requiredRole;
      },
    [user]
  );

  const hasAnyRole = useMemo(
    () =>
      (roles: UserRole[]): boolean => {
        if (!user) return false;
        return roles.includes(user.role);
      },
    [user]
  );

  const hasAllRoles = useMemo(
    () =>
      (roles: UserRole[]): boolean => {
        if (!user) return false;
        // For single-role system, user must have at least one of the roles
        return roles.includes(user.role);
      },
    [user]
  );

  return {
    role: user?.role || null,
    hasRole,
    isStudent: user?.role === UserRole.STUDENT,
    isLecturer: user?.role === UserRole.LECTURER,
    hasAnyRole,
    hasAllRoles,
  };
};
