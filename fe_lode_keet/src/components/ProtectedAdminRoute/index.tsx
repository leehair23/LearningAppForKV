import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "@/stores/useAuthStore";
import { Constants } from "@/common/constants";
import Loading from "../Loading";

export const AdminRoute = () => {
  const { isAuthenticated, user, loading } = useAuthStore();

  if (loading) {
    return <Loading />;
  }

  // Not authenticated at all
  if (!isAuthenticated) {
    return <Navigate to={Constants.ROUTES.PUBLIC.SIGN_IN} replace />;
  }

  // Authenticated but not admin
  if (user?.role !== "ADMIN") {
    return <Navigate to={Constants.ROUTES.DASHBOARD.HOME} replace />;
  }

  return <Outlet />;
};
