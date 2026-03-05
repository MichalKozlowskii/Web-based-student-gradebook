import React from "react";

export const ExamInstructions: React.FC = () => (
  <div className="bg-white rounded-lg shadow p-6 mt-6">
    <h3 className="text-lg font-semibold text-gray-800 mb-4">
      Jak skorzystać z automatycznego oceniania?
    </h3>
    <ol className="space-y-3 text-sm text-gray-700">
      <li className="flex gap-3">
        <span className="flex-shrink-0 w-6 h-6 bg-indigo-100 text-indigo-700 rounded-full flex items-center justify-center text-xs font-bold">
          1
        </span>
        <span>
          <strong>Wygeneruj tabelkę</strong> — kliknij przycisk powyżej, aby
          pobrać gotową tabelkę punktacji w formacie JPG.
        </span>
      </li>
      <li className="flex gap-3">
        <span className="flex-shrink-0 w-6 h-6 bg-indigo-100 text-indigo-700 rounded-full flex items-center justify-center text-xs font-bold">
          2
        </span>
        <span>
          <strong>Umieść tabelkę w sprawdzianie</strong> — wklej wygenerowaną
          tabelkę do treści sprawdzianu przed jego wydrukowaniem.
        </span>
      </li>
      <li className="flex gap-3">
        <span className="flex-shrink-0 w-6 h-6 bg-indigo-100 text-indigo-700 rounded-full flex items-center justify-center text-xs font-bold">
          3
        </span>
        <span>
          <strong>Studenci wpisują numer indeksu</strong> — podczas pisania
          sprawdzianu każdy student powinien uzupełnić swój numer indeksu w
          wyznaczonym polu tabelki.
        </span>
      </li>
      <li className="flex gap-3">
        <span className="flex-shrink-0 w-6 h-6 bg-indigo-100 text-indigo-700 rounded-full flex items-center justify-center text-xs font-bold">
          4
        </span>
        <div>
          <strong>Uzupełnij punktację</strong> — po sprawdzeniu pracy przy
          każdym zadaniu wpisz wynik, korzystając z oznaczeń:
          <div className="mt-1.5 flex flex-wrap gap-2">
            <span className="inline-flex items-center px-2 py-0.5 bg-green-50 text-green-700 border border-green-200 rounded text-xs font-medium">
              A — 1 pkt
            </span>
            <span className="inline-flex items-center px-2 py-0.5 bg-yellow-50 text-yellow-700 border border-yellow-200 rounded text-xs font-medium">
              B — 0,5 pkt
            </span>
            <span className="inline-flex items-center px-2 py-0.5 bg-red-50 text-red-700 border border-red-200 rounded text-xs font-medium">
              brak — 0 pkt
            </span>
          </div>
        </div>
      </li>
      <li className="flex gap-3">
        <span className="flex-shrink-0 w-6 h-6 bg-indigo-100 text-indigo-700 rounded-full flex items-center justify-center text-xs font-bold">
          5
        </span>
        <span>
          <strong>Sprawdź pracę</strong> — prześlij zdjęcie wypełnionej
          tabelki, a system obliczy wynik i przyzna ocenę uczniowi znajdując go
          w systmie poprzez odczytanie numeru indeksu z tabelki.
        </span>
      </li>
    </ol>
    <div className="mt-4 flex gap-2 p-3 bg-amber-50 border border-amber-200 rounded-lg text-xs text-amber-800">
      <svg
        className="w-4 h-4 flex-shrink-0 mt-0.5"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
        />
      </svg>
      <span>
        Przed wystawieniem oceny system poprosi Cię o zatwierdzenie wyniku
        analizy. Żadna ocena nie zostanie wystawiona bez Twojej akceptacji.
      </span>
    </div>
  </div>
);
