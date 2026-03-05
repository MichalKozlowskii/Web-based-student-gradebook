import React, { useState, useEffect } from "react";
import type { ReactNode } from "react";
import { AuthContext } from "../context/authContext";
import type { User, AuthContextType } from "../types/auth.types";
import { UserRole } from "../types/auth.types";

interface AuthProviderProps {
  children: ReactNode;
}

// Cache state - survives remounts but not page refreshes
let isCheckingAuth = false;

// LocalStorage keys
const USER_CACHE_KEY = "auth_user_cache";
const EXPIRY_CACHE_KEY = "auth_expiry_cache";

// Helper functions for cache persistence
const getCachedUser = (): User | null => {
  try {
    const cached = localStorage.getItem(USER_CACHE_KEY);
    return cached ? JSON.parse(cached) : null;
  } catch {
    return null;
  }
};

const getCachedExpiry = (): number => {
  try {
    const cached = localStorage.getItem(EXPIRY_CACHE_KEY);
    return cached ? parseInt(cached, 10) : 0;
  } catch {
    return 0;
  }
};

const setCachedUser = (user: User | null) => {
  if (user) {
    localStorage.setItem(USER_CACHE_KEY, JSON.stringify(user));
  } else {
    localStorage.removeItem(USER_CACHE_KEY);
  }
};

const setCachedExpiry = (expiry: number) => {
  if (expiry > 0) {
    localStorage.setItem(EXPIRY_CACHE_KEY, expiry.toString());
  } else {
    localStorage.removeItem(EXPIRY_CACHE_KEY);
  }
};

const clearCache = () => {
  localStorage.removeItem(USER_CACHE_KEY);
  localStorage.removeItem(EXPIRY_CACHE_KEY);
};

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(getCachedUser());
  const [loading, setLoading] = useState(true);

  const checkAuth = async (force = false) => {
    // Prevent concurrent requests
    if (isCheckingAuth) {
      console.log("⏸️ Auth check already in progress, skipping...");
      return;
    }

    try {
      const now = Date.now();
      const cachedExpiry = getCachedExpiry();

      // Use cache if valid
      if (!force && now < cachedExpiry) {
        const cachedUser = getCachedUser();
        if (cachedUser) {
          console.log(
            `✅ Using cached auth (valid for ${Math.floor((cachedExpiry - now) / 1000 / 60)} more minutes)`,
          );
          setUser(cachedUser);
          setLoading(false);
          return;
        }
      }

      isCheckingAuth = true;
      setLoading(true);

      const response = await fetch("/gradebook/auth/me", {
        method: "GET",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        const userData = await response.json();
        const expiryMs = userData.exp * 1000;

        // Cache user data and expiry in localStorage
        setCachedUser(userData);
        setCachedExpiry(expiryMs);
        setUser(userData);

        console.log(
          "✅ Auth verified - JWT valid until:",
          new Date(expiryMs).toLocaleTimeString(),
        );
      } else {
        console.log("❌ Auth failed");
        clearCache();
        setUser(null);
      }
    } catch (error) {
      console.error("Auth check failed:", error);
      clearCache();
      setUser(null);
    } finally {
      setLoading(false);
      isCheckingAuth = false;
    }
  };

  const logout = async () => {
    try {
      await fetch("/gradebook/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      clearCache();
      setUser(null);
      window.location.href = "/login";
    }
  };

  // Check auth on mount
  useEffect(() => {
    checkAuth();
  }, []); // Empty array - runs once per mount (twice in Strict Mode, but checkAuth handles it!)

  // Role-checking methods
  const hasRole = (role: UserRole | UserRole[]): boolean => {
    if (!user) return false;
    if (Array.isArray(role)) {
      return role.includes(user.role);
    }
    return user.role === role;
  };

  const isStudent = (): boolean => {
    return user?.role === UserRole.STUDENT;
  };

  const isLecturer = (): boolean => {
    return user?.role === UserRole.LECTURER;
  };

  const value: AuthContextType = {
    user,
    loading,
    logout,
    checkAuth: () => checkAuth(false), // Use cache by default
    hasRole,
    isStudent,
    isLecturer,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
