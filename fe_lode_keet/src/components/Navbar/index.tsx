import { Constants } from "@/common/constants";
import { Link, useNavigate } from "react-router-dom";
import Button from "../Button";
import { useAuthStore } from "@/stores/useAuthStore";
import { authService } from "@/services/authService";

const Navbar = () => {
  const { isAuthenticated } = useAuthStore();
  const navigate = useNavigate();
  const handleLogoutClick = () => {
    authService.signOut();
    navigate(Constants.ROUTES.PUBLIC.SIGN_IN);
  };

  return (
    <header className="bg-slate-800 text-whhite shadow-sm border-b border-gray-200">
      <div className="max-w-6xl mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <div className="shrink-0 flex items-center gap-3">
            <Link
              to={Constants.ROUTES.DASHBOARD.HOME}
              className="text-white-700 hover:text-white-900 transition-colors font-bold">
              <span className="text-xl font-bold text-white-700">
                Lode Keet
              </span>
            </Link>
          </div>
          <nav className="hidden md:flex gap-6">
            <Link
              to={Constants.ROUTES.DASHBOARD.COURSES}
              className="text-white-700 hover:text-white-900 transition-colors font-bold">
              Courses
            </Link>{" "}
            <Link
              to={Constants.ROUTES.DASHBOARD.EXERCISES}
              className="text-white-700 hover:text-white-900 transition-colors font-bold">
              Exercises
            </Link>{" "}
            <Link
              to={Constants.ROUTES.DASHBOARD.LEADERBOARD}
              className="text-white-700 hover:text-white-900 transition-colors font-bold">
              Leader Board
            </Link>
            <Link
              to={Constants.ROUTES.DASHBOARD.PROFILE}
              className="text-white-700 hover:text-white-900 transition-colors font-bold">
              Profile
            </Link>
          </nav>
          <div className="flex items-center gap-3">
            <div className="hidden md:flex gap-2">
              {isAuthenticated ? (
                <Button
                  className="px-4 py-2"
                  variant="ghost"
                  onClick={handleLogoutClick}>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                    className="w-6 h-6 text-white-500">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9"
                    />
                  </svg>
                </Button>
              ) : (
                <>
                  <Link to={Constants.ROUTES.PUBLIC.SIGN_IN} className="">
                    <Button className="px-4 py-2 text-white-600 hover:text-white-800 border border-gray-300 rounded-lg hover:border-gray-400 transition-colors">
                      Login
                    </Button>
                  </Link>
                  <Link to={Constants.ROUTES.PUBLIC.SIGN_UP} className="">
                    <Button className="px-4 py-2 bg-primary-700 text-white border-gray-300 rounded-lg hover:bg-primary-800 transition-colors">
                      Sign Up
                    </Button>
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
