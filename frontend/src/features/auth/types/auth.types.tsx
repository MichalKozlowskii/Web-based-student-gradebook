// Role enum for type safety
export enum UserRole {
  STUDENT = "STUDENT",
  LECTURER = "LECTURER",
}

export interface User {
  id: string;
  name: string;
  role: UserRole;
  exp: number; // JWT expiration timestamp (seconds since epoch)
}

export interface AuthContextType {
  user: User | null;
  loading: boolean;
  logout: () => void;
  checkAuth: () => Promise<void>;
  hasRole: (role: UserRole | UserRole[]) => boolean;
  isStudent: () => boolean;
  isLecturer: () => boolean;
}
