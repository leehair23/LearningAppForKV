export type T_AuthPayload = {
  iss: string;
  sub: string;
  role: string;
  exp: number;
  iat: number;
  email: string;
  userId: string;
};

export type T_UserData = {
  email: string;
  role: string;
  sub: string;
  userId: string;
  username: string;
};

export type T_LessonType = "CODING" | "QUIZ" | "THEORY"; // Add other types as needed

export type T_CourseLevel = "BEGINNER" | "INTERMEDIATE" | "ADVANCED";
