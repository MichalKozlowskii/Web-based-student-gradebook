export { AuthProvider } from "./components/AuthProvider";
export { ProtectedRoute } from "./components/ProtectedRoute";
export {
  RoleRoute,
  StudentRoute,
  LecturerRoute,
} from "./components/RoleRoute";
export { RoleGate, StudentOnly, LecturerOnly } from "./components/RoleGate";
export { RoleBasedRedirect } from "./components/RoleBasedRedirect";
export { useAuth } from "./hooks/useAuth";
export { useRole } from "./hooks/useRole";
export type { User, AuthContextType } from "./types/auth.types";
export { UserRole } from "./types/auth.types";