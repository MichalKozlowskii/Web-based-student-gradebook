import React from "react";
import { ProtectedRoute } from "./ProtectedRoute";
import { UserRole } from "../types/auth.types";

interface RoleRouteProps {
  children: React.ReactNode;
  allowedRoles: UserRole[];
  fallbackPath?: string;
}

export const RoleRoute: React.FC<RoleRouteProps> = ({
  children,
  allowedRoles,
  fallbackPath,
}) => {
  return (
    <ProtectedRoute allowedRoles={allowedRoles} fallbackPath={fallbackPath}>
      {children}
    </ProtectedRoute>
  );
};

// Convenience components for common use cases
export const StudentRoute: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => <RoleRoute allowedRoles={[UserRole.STUDENT]}>{children}</RoleRoute>;

export const LecturerRoute: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => <RoleRoute allowedRoles={[UserRole.LECTURER]}>{children}</RoleRoute>;
