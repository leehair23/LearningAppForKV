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
    const { setLoading, courses, setCourses, setPageable, setAdditionalData } =
      useCourseStore.getState();
    try {
      if (!courses || courses.length === 0) {
        setLoading(true);
        const response = await api.get(`/courses`);
        const {
          content,
          pageable,
          last,
          totalPages,
          totalElements,
          size,
          number,
          first,
          numberOfElements,
          sort,
          empty,
        } = response.data;
        const mappedCourses = this.mapCoursesData(content);
        const mappedPageableCriteria = this.mapPageable(pageable);
        setCourses(mappedCourses);
        setPageable(mappedPageableCriteria);
        setAdditionalData({
          last,
          totalPages,
          totalElements,
          size,
          number,
          first,
          numberOfElements,
          sort,
          empty,
        });
        toast.success("Courses retrieved successfully✅");
      }
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
      pageSize: data.pageSize,
      sort: {
        empty: data.sort.empty,
        sorted: data.sort.sorted,
        unsorted: data.sort.unsorted,
      },
      offset: data.offset,
      paged: data.paged,
      unpaged: data.unpaged,
    };
  }

  private mapCoursesData(courses: ICourse[]) {
    return courses.map((crs) => {
      return {
        id: crs.id,
        title: crs.title,
        description: crs.description,
        thumbnail: crs.thumbnail,
        level: crs.level,
        price: crs.price,
        chapters: crs.chapters,
        isPublished: crs.isPublished,
        createdAt: new Date(crs.createdAt).toLocaleString(),
        updatedAt: new Date(crs.updatedAt).toLocaleString(),
      };
    });
  }
}

export const courseService = CourseService.getInstance();
