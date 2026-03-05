import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/features/auth";

export const LoginPage: React.FC = () => {
  const { user, loading } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (user && !loading) {
      navigate("/dashboard", { replace: true });
    }
  }, [user, loading, navigate]);

  const handleUsosLogin = () => {
    window.location.href = "/gradebook/auth/login/usos";
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <div className="flex flex-col items-center gap-3">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
          <p className="text-gray-600 text-sm">Ładowanie...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="bg-white p-8 rounded-lg shadow-lg max-w-md w-full">
        <div className="text-center mb-8">
          <div className="mb-4">
            <div className="w-16 h-16 bg-indigo-600 rounded-full mx-auto flex items-center justify-center">
              <svg
                className="w-8 h-8 text-white"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                />
              </svg>
            </div>
          </div>
          <h1 className="text-3xl font-bold text-gray-800 mb-2">
            Dziennik Studenta
          </h1>
          <p className="text-gray-600">
            Zaloguj się przez USOS aby mieć dostęp do aplikacji
          </p>
        </div>

        <button
          onClick={handleUsosLogin}
          className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 ease-in-out transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 shadow-md"
        >
          <div className="flex items-center justify-center gap-2">
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
                d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"
              />
            </svg>
            <span>Zaloguj się przez USOS</span>
          </div>
        </button>

        <div className="mt-6 text-center text-sm text-gray-500">
          <p>
            *Studencie- nie przechowujemy twoich danych osobowych, ani klucza
            dostępu do USOS
          </p>
          <p>
            **Wykładowco- nie przechowujemy twoich danych osobowych, twój klucz
            do Usos jest zaszyfrowany i przechowywany przez dwie godziny, lub do
            momentu wylogowania.
          </p>
          <p>***Strona nie jest powiązana z Uniwersytetem wrocławskim.</p>
        </div>
      </div>
    </div>
  );
};
