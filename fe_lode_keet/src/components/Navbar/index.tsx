import { Constants } from "@/common/constants";
import { Link } from "react-router-dom";
import Button from "../Button";

const Navbar = () => {
  return (
    <header className="bg-slate-800 text-whhite shadow-sm border-b border-gray-200">
      <div className="max-w-6xl mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <div className="shrink-0 flex items-center gap-3">
            <span className="text-xl font-bold text-white-700">Lode Keet</span>
          </div>
          <nav className="hidden md:flex gap-6">
            <Link
              to={Constants.ROUTES.PUBLIC.HOME}
              className="text-white-700 hover:text-white-900 transition-colors font-bold">
              Home
            </Link>{" "}
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
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
