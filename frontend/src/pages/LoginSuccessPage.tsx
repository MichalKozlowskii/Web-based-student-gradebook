import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/features/auth";

export const LoginSuccessPage: React.FC = () => {
  const { checkAuth } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const handleSuccess = async () => {
      await checkAuth();
      setTimeout(() => {
        navigate("/dashboard", { replace: true });
      }, 500);
    };

    handleSuccess();
  }, []);

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-green-50 to-emerald-100">
      <div className="bg-white p-8 rounded-lg shadow-lg max-w-md w-full">
        <div className="flex flex-col items-center">
          {/* Success Icon */}
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-4">
            <svg
              className="w-8 h-8 text-green-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 13l4 4L19 7"
              />
            </svg>
          </div>

          {/* Success Message */}
          <h1 className="text-2xl font-bold text-gray-800 mb-2">
            Zalogowano pomyślnie!
          </h1>
          <p className="text-gray-600 mb-6">
            Uwierzytelnienie przebiegło pomyślnie.
          </p>

          {/* Loading Spinner */}
          <div className="w-12 h-12 border-4 border-green-500 border-t-transparent rounded-full animate-spin mb-4"></div>

          <p className="text-sm text-gray-500">Przekierowywanie do panelu...</p>
        </div>
      </div>
    </div>
  );
};
