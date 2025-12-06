import { Constants } from "@/common/constants";
import { useAuthStore } from "@/stores/useAuthStore";
import { Navigate, Outlet, useLocation } from "react-router-dom";

export const ProtectedRoute = ({
  redirectTo = Constants.ROUTES.PUBLIC.SIGN_IN,
  requireAuth = true,
}: {
  redirectTo?: string;
  requireAuth?: boolean;
}) => {
  const { isAuthenticated, user } = useAuthStore();
  const location = useLocation();
  const userRole = user?.role || "USER";
  const isAdminRoute = location.pathname.includes(Constants.ROUTES.ADMIN.HOME);

  // Protected route - requires auth
  if (requireAuth && !isAuthenticated) {
    return <Navigate to={redirectTo} replace />;
  }

  // Role-based access for admin routes
  if (isAuthenticated && isAdminRoute && userRole !== "ADMIN") {
    // User is trying to access /admin but is not an ADMIN
    return <Navigate to={Constants.ROUTES.DASHBOARD.HOME} replace />;
  }

  // Public-only route - requires no auth
  if (!requireAuth && isAuthenticated) {
    return <Navigate to={Constants.ROUTES.DASHBOARD.HOME} replace />;
  }

  return <Outlet />;
};
