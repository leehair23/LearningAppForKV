import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import {
  createBrowserRouter,
  Navigate,
  RouterProvider,
} from "react-router-dom";
import "./index.css";
import App from "./App.tsx";
import NotFoundPage from "./components/NotFoundPage/index.tsx";
import SignIn from "./pages/SignIn/index.tsx";
import { Toaster } from "sonner";
import { ProtectedRoute } from "./components/ProtectedRoute/index.tsx";
import { Constants } from "./common/constants.ts";
import { PublicOnlyRoute } from "./components/PublicOnlyRoute/index.tsx";
import { ProtectedAdminRoute } from "./components/ProtectedAdminRoute/index.tsx";
import Dashboard from "./pages/Dashboard/index.tsx";

const router = createBrowserRouter([
  {
    path: Constants.ROUTES.PUBLIC.HOME,
    element: <App />,
    children: [
      {
        element: <PublicOnlyRoute />,
        children: [
          {
            path: "sign-in",
            element: <SignIn />,
          },
          {
            path: "sign-up",
            element: <SignIn />,
          },
        ],
      },

      // Protected routes (require authentication)
      {
        element: <ProtectedRoute />,
        children: [
          {
            path: "dashboard",
            element: <Dashboard />,
          },
        ],
      },

      // Admin-only routes (require authentication + admin role)
      {
        element: <ProtectedAdminRoute />,
        children: [
          {
            path: "admin",
            element: <App />,
          },
        ],
      },
    ],
  },
  {
    path: "/",
    element: <Navigate to="/dashboard" replace />,
  },
  {
    path: "*",
    element: <NotFoundPage />,
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <RouterProvider router={router} />
    <Toaster />
  </StrictMode>
);
