import { Navigate } from "react-router-dom";
import { useAuthStore } from "@/stores/useAuthStore";
import { Constants } from "@/common/constants";
import Loading from "../Loading";

export const PublicOnlyRoute = (props: { children: React.ReactNode }) => {
  const { isAuthenticated, user, loading } = useAuthStore();

  if (loading) {
    return <Loading />;
  }

  if (isAuthenticated && user) {
    return <Navigate to={Constants.ROUTES.DASHBOARD.HOME} replace />;
  }

  return <>{props.children}</>;
};
