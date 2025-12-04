export class Constants {
  public static readonly BASE_URL = "http://localhost:8080/app";
  public static readonly REQUEST_METHODS = {
    POST: "POST",
    GET: "GET",
    PUT: "PUT",
    PATCH: "PATCH",
  };
  public static readonly HEADERS = {
    "Content-Type": "application/json",
  };
  public static readonly ROUTES = {
    PUBLIC: {
      HOME: "/",
      ABOUT: "/about",
      SIGN_IN: "/sign-in",
      SIGN_UP: "/sign-up",
      FORGOT_PASSWORD: "/forgot-password",
    },
    DASHBOARD: {
      HOME: "/dashboard",
      COURSES: "/dashboard/courses",
      COURSE_DETAIL: (courseId: string) => `/dashboard/courses/${courseId}`,
      COURSE_MODULE: (courseId: string, moduleId: string) =>
        `/dashboard/courses/${courseId}/modules/${moduleId}`,
      EXERCISES: "/dashboard/exercises",
      EXERCISE_DETAIL: (exerciseId: string) =>
        `/dashboard/exercises/${exerciseId}`,
      LEADERBOARD: "/dashboard/leaderboard",
      PROFILE: "/dashboard/profile",
      PROFILE_EDIT: "/dashboard/profile/edit",
    },
    ADMIN: {
      HOME: "/admin",
      USERS: "/admin/users",
      USER_DETAIL: (userId: string) => `/admin/users/${userId}`,
      USER_CREATE: "/admin/users/create",
      COURSES: "/admin/courses",
      COURSE_DETAIL: (courseId: string) => `/admin/courses/${courseId}`,
      COURSE_CREATE: "/admin/courses/create",
      EXERCISES: "/admin/exercises",
      EXERCISE_DETAIL: (exerciseId: string) => `/admin/exercises/${exerciseId}`,
      EXERCISE_CREATE: "/admin/exercises/create",
      ANALYTICS: "/admin/analytics",
    },
  };
  public static readonly ROUTE_TYPE = typeof this.ROUTES;
  public static readonly LOCAL_STORAGE_KEYS = {
    ACC_TOKEN: "access_token",
    REF_tOKEN: "refresh_token",
  };
}
