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
import SignUp from "./pages/SignUp/index.tsx";
import Courses from "./pages/Courses/index.tsx";

const router = createBrowserRouter([
  {
    path: "sign-in",
    element: (
      <PublicOnlyRoute>
        <SignIn />
      </PublicOnlyRoute>
    ),
  },
  {
    path: "sign-up",
    element: (
      <PublicOnlyRoute>
        <SignUp />
      </PublicOnlyRoute>
    ),
  },
  {
    path: Constants.ROUTES.PUBLIC.HOME,
    element: <App />,
    children: [
      // Protected routes (require authentication)
      {
        path: "dashboard",
        element: (
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        ),
      },
      {
        path: "courses",
        element: (
          <ProtectedRoute>
            <Courses />
          </ProtectedRoute>
        ),
      },
      {
        path: "courses/:id",
        element: (
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        ),
      },
      {
        path: "exercises",
        element: (
          <ProtectedRoute>
            <p>Exercises</p>
          </ProtectedRoute>
        ),
      },
      {
        path: "exercises/:id",
        element: (
          <ProtectedRoute>
            <p>Exercises</p>
          </ProtectedRoute>
        ),
      },
      {
        path: "profile",
        element: (
          <ProtectedRoute>
            <p>Profile page</p>
          </ProtectedRoute>
        ),
      },
      {
        path: "profile/edit",
        element: (
          <ProtectedRoute>
            <p>Profile page</p>
          </ProtectedRoute>
        ),
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

      // Redirects
      {
        index: true, // This is for the "/" path
        element: <Navigate to="/dashboard" replace />,
      },
    ],
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
