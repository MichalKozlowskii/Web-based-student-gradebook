import React from "react";
import { useRole } from "../hooks/useRole";
import { UserRole } from "../types/auth.types";

interface RoleGateProps {
  children: React.ReactNode;
  allowedRoles: UserRole[];
  fallback?: React.ReactNode;
  mode?: "any" | "all";
}

export const RoleGate: React.FC<RoleGateProps> = ({
  children,
  allowedRoles,
  fallback = null,
  mode = "any",
}) => {
  const { hasRole, hasAllRoles } = useRole();

  const hasAccess =
    mode === "all" ? hasAllRoles(allowedRoles) : hasRole(allowedRoles);

  return hasAccess ? <>{children}</> : <>{fallback}</>;
};

// Convenience components
export const StudentOnly: React.FC<{
  children: React.ReactNode;
  fallback?: React.ReactNode;
}> = ({ children, fallback }) => (
  <RoleGate allowedRoles={[UserRole.STUDENT]} fallback={fallback}>
    {children}
  </RoleGate>
);

export const LecturerOnly: React.FC<{
  children: React.ReactNode;
  fallback?: React.ReactNode;
}> = ({ children, fallback }) => (
  <RoleGate allowedRoles={[UserRole.LECTURER]} fallback={fallback}>
    {children}
  </RoleGate>
);
