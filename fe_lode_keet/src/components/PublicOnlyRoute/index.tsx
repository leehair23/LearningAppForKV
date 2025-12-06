import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "@/stores/useAuthStore";
import { Constants } from "@/common/constants";
import Loading from "../Loading";

export const PublicOnlyRoute = () => {
  const { isAuthenticated, accessToken, user, loading } = useAuthStore();

  if (loading) {
    return <Loading />;
  }

  if (isAuthenticated && accessToken && user) {
    return <Navigate to={Constants.ROUTES.DASHBOARD.HOME} replace />;
  }

  return <Outlet />;
};
