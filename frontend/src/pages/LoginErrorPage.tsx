import React, { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

export const LoginErrorPage: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [countdown, setCountdown] = useState(5);

  const error = searchParams.get("error");

  const getErrorMessage = (errorCode: string | null) => {
    switch (errorCode) {
      case "rate_limited":
        return "Zbyt wiele prób logowania. Spróbuj ponownie później.";
      case "usos_unavailable":
        return "Serwis USOS jest obecnie niedostępny. Spróbuj ponownie później.";
      case "invalid_or_expired":
        return "Sesja logowania wygasła. Spróbuj ponownie.";
      case "token_mismatch":
        return "Uwierzytelnienie nie powiodło się z powodu niezgodności tokenu.";
      case "authentication_failed":
        return "Uwierzytelnienie nie powiodło się. Spróbuj ponownie.";
      case "server_error":
        return "Wystąpił błąd serwera. Spróbuj ponownie.";
      default:
        return "Wystąpił nieznany błąd podczas logowania.";
    }
  };

  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          navigate("/login", { replace: true });
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [navigate]);

  const handleRetry = () => {
    navigate("/login", { replace: true });
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-red-50 to-rose-100">
      <div className="bg-white p-8 rounded-lg shadow-lg max-w-md w-full">
        <div className="flex flex-col items-center">
          {/* Error Icon */}
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mb-4">
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
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </div>

          {/* Error Title */}
          <h1 className="text-2xl font-bold text-gray-800 mb-2">
            Logowanie nie powiodło się
          </h1>

          {/* Error Message */}
          <p className="text-gray-600 text-center mb-6">
            {getErrorMessage(error)}
          </p>

          {/* Error Code Badge */}
          {error && (
            <div className="bg-red-50 border border-red-200 rounded-md px-4 py-2 mb-6 w-full">
              <p className="text-sm text-red-800 text-center">
                Kod błędu:{" "}
                <span className="font-mono font-semibold">{error}</span>
              </p>
            </div>
          )}

          {/* Retry Button */}
          <button
            onClick={handleRetry}
            className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 mb-3 flex items-center justify-center gap-2"
          >
            <svg
              className="w-5 h-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
              />
            </svg>
            <span>Spróbuj ponownie</span>
          </button>

          {/* Auto Redirect Message */}
          <p className="text-sm text-gray-500">
            Przekierowanie do logowania za{" "}
            <span className="font-semibold">{countdown}</span>{" "}
            {countdown === 1 ? "sekundę" : countdown < 5 ? "sekundy" : "sekund"}...
          </p>
        </div>
      </div>
    </div>
  );
};
