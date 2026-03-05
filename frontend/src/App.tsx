import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import {
  AuthProvider,
  ProtectedRoute,
  RoleBasedRedirect,
  LecturerRoute,
  StudentRoute,
} from "@/features/auth";
import {
  LoginPage,
  DashboardPage,
  LoginSuccessPage,
  LoginErrorPage,
  UnauthorizedPage,
  ExamsPage,
  ExamDetailPage,
  CoursePerformancePage,
  CourseManagePage,
} from "@/pages";

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public routes */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/login/success" element={<LoginSuccessPage />} />
          <Route path="/login/failure" element={<LoginErrorPage />} />
          <Route path="/unauthorized" element={<UnauthorizedPage />} />

          {/* Protected routes - accessible to all authenticated users */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <DashboardPage />
              </ProtectedRoute>
            }
          />

          {/* Student-only routes */}
          <Route
            path="/course/performance/:courseUnitId"
            element={
              <StudentRoute>
                <CoursePerformancePage />
              </StudentRoute>
            }
          />

          {/* Lecturer-only routes */}
          <Route
            path="/course/manage/:groupId"
            element={
              <LecturerRoute>
                <CourseManagePage />
              </LecturerRoute>
            }
          />
          <Route
            path="/exams"
            element={
              <LecturerRoute>
                <ExamsPage />
              </LecturerRoute>
            }
          />
          <Route
            path="/exams/:id"
            element={
              <LecturerRoute>
                <ExamDetailPage />
              </LecturerRoute>
            }
          />

          {/* Smart redirect based on role */}
          <Route path="/" element={<RoleBasedRedirect />} />

          {/* 404 fallback */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
};

export default App;
