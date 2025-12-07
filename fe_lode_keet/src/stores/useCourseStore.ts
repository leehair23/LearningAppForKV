import { create } from "zustand";
import type {
  CourseState,
  IAdditionalData,
  ICourse,
  IPageable,
} from "@/common/interfaces";

export const useCourseStore = create<CourseState>((set) => ({
  courses: [],
  loading: false,
  hasMoreData: true,
  pageable: null,
  additionalData: null,

  clearState: () => {
    set({
      courses: [],
      loading: false,
      hasMoreData: true,
      pageable: null,
      additionalData: null,
    });
  },
  setCourses: (data: ICourse[] | null) => set({ courses: data }),
  setHasMoreData: (hasMoreData: boolean) => set({ hasMoreData }),
  setLoading: (loading: boolean) => set({ loading }),
  setPageable: (pageable: Partial<IPageable> | null) => set({ pageable }),
  setAdditionalData: (data: Partial<IAdditionalData> | null) =>
    set({ additionalData: data }),
}));
