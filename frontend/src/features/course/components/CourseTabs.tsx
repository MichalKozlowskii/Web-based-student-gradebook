import React from "react";
import { COURSE_TABS, type TabType } from "../utils/course.utils";

interface CourseTabsProps {
  activeTab: TabType;
  onTabChange: (tab: TabType) => void;
  children: React.ReactNode;
}

export const CourseTabs: React.FC<CourseTabsProps> = ({
  activeTab,
  onTabChange,
  children,
}) => {
  return (
    <div className="bg-white rounded-lg shadow mb-6">
      <div className="border-b border-gray-200">
        <nav className="flex -mb-px">
          {COURSE_TABS.map((tab) => (
            <button
              key={tab.id}
              onClick={() => onTabChange(tab.id)}
              className={`flex-1 py-4 px-6 text-center font-medium text-sm transition-colors ${
                activeTab === tab.id
                  ? "border-b-2 border-indigo-600 text-indigo-600"
                  : "text-gray-600 hover:text-gray-900 hover:border-b-2 hover:border-gray-300"
              }`}
            >
              <span className="mr-2">{tab.icon}</span>
              {tab.label}
            </button>
          ))}
        </nav>
      </div>

      {children}
    </div>
  );
};
