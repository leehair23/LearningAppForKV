import type { T_CourseLevel, T_LessonType, T_UserData } from "./types";

export interface ILesson {
  id: string;
  title: string;
  type: T_LessonType;
}

export interface IChapter {
  id: string;
  title: string;
  lessons: ILesson[];
}

export interface IPageable {
  pageNumber: number;
  pageSize: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

export interface IAdditionalData {
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  numberOfElements: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  empty: boolean;
}

export interface ICourse {
  id: string;
  title: string;
  description: string;
  thumbnail: string | null;
  level: T_CourseLevel;
  price: number | null;
  chapters: IChapter[];
  isPublished: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  loading: boolean;
  user: T_UserData | null;
  username: string;
  email: string;
  password: string;
  isAuthenticated: boolean;

  setAuthenticated: (isAuthenticated: boolean) => void;
  setUsername: (username: string) => void;
  setEmail: (email: string) => void;
  setPassword: (password: string) => void;
  resetForm: () => void;
  setAccessToken: (token: string | null) => void;
  setRefreshToken: (token: string | null) => void;
  setLoading: (loading: boolean) => void;
  setUser: (user: T_UserData | null) => void;
  clearState: () => void;
}

export interface CourseState {
  courses: ICourse[] | null;
  selectedCourse: ICourse | null;
  loading: boolean;
  hasMoreData: boolean;
  pageable: Partial<IPageable> | null;
  additionalData: Partial<IAdditionalData> | null;

  setCourses: (data: ICourse[]) => void;
  setSelectedCourse: (data: ICourse) => void;
  setHasMoreData: (hasMoreData: boolean) => void;
  setLoading: (loading: boolean) => void;
  setPageable: (data: Partial<IPageable>) => void;
  setAdditionalData: (data: IAdditionalData) => void;
  clearState: () => void;
}
