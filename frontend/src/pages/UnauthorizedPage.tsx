import React from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/features/auth";

export const UnauthorizedPage: React.FC = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-8">
        <div className="text-center">
          {/* Icon */}
          <div className="mx-auto w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mb-4">
            <svg
              className="w-8 h-8 text-red-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
              />
            </svg>
          </div>

          {/* Title */}
          <h1 className="text-2xl font-bold text-gray-900 mb-2">
            Brak dostępu
          </h1>

          {/* Message */}
          <p className="text-gray-600 mb-6">
            Nie masz uprawnień do wyświetlenia tej strony. Ten obszar jest
            dostępny tylko dla wybranych ról użytkowników.
          </p>

          {/* User Info */}
          {user && (
            <div className="bg-gray-50 rounded-lg p-4 mb-6">
              <p className="text-sm text-gray-600">
                Zalogowany jako{" "}
                <span className="font-medium">{user.name}</span>
              </p>
              <p className="text-sm text-gray-600">
                Rola:{" "}
                <span className="font-medium capitalize">
                  {user.role.toLowerCase()}
                </span>
              </p>
            </div>
          )}

          {/* Actions */}
          <div className="flex flex-col gap-3">
            <button
              onClick={() => navigate("/dashboard")}
              className="w-full bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md font-medium transition duration-200"
            >
              Przejdź do panelu
            </button>
            <button
              onClick={() => navigate(-1)}
              className="w-full bg-gray-200 hover:bg-gray-300 text-gray-700 px-4 py-2 rounded-md font-medium transition duration-200"
            >
              Wróć
            </button>
            <button
              onClick={logout}
              className="w-full text-red-600 hover:text-red-700 px-4 py-2 rounded-md font-medium transition duration-200"
            >
              Wyloguj się
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
