import { useCourseStore } from "@/stores/useCourseStore";
import { toast } from "sonner";
import api from "@/utils/axios";
import type { ICourse, IPageable } from "@/common/interfaces";

export class CourseService {
  private static instance: CourseService;

  // Private constructor for singleton pattern
  private constructor() {}

  // Singleton getter
  public static getInstance(): CourseService {
    if (!CourseService.instance) {
      CourseService.instance = new CourseService();
    }
    return CourseService.instance;
  }

  public async getCourses() {
    const { setLoading, setCourses, setPageable, setAdditionalData } =
      useCourseStore.getState();
    try {
      setLoading(true);
      const response = await api.get(`/courses`);
      const {
        content,
        pageable,
        totalPages,
        totalElements,
        size,
        number,
        numberOfElements,
      } = await response.data;
      const mappedCourses = this.mapCoursesData(content);
      const mappedPageableCriteria = this.mapPageable(pageable);
      setCourses(mappedCourses);
      setPageable(mappedPageableCriteria);
      setAdditionalData({
        totalPages,
        totalElements,
        size,
        number,
        numberOfElements,
      });
      toast.success("Courses retrieved successfully✅");
      return;
    } catch (error) {
      console.error("Error when requesting courses:", error);
      toast.error("❌Error occurred during requesting data!");
      return;
    } finally {
      setLoading(false);
    }
  }

  private mapPageable(data: IPageable) {
    return {
      pageNumber: data.pageNumber,
      size: data.pageSize,
      sorted: data.sort.sorted,
      offset: data.offset,
      paged: data.paged,
    };
  }

  private mapCoursesData(courses: ICourse[]) {
    return courses.map((crs) => {
      return {
        id: crs.id,
        title: crs.title,
        description: crs.description,
        level: crs.level,
        chapters: crs.chapters,
        price: crs.price,
        updatedAt: new Date(crs?.updatedAt).toLocaleString(),
        isPublished: crs.isPublished,
      };
    });
  }
}

export const courseService = CourseService.getInstance();
