import React from "react";
import Container from "@/components/Container";
import { useCourseStore } from "@/stores/useCourseStore";
import { courseService } from "@/services/courseService";
import { useParams } from "react-router-dom";

const CourseDetail: React.FC = () => {
  const { selectedCourse } = useCourseStore();
  const { id } = useParams();
  const courseChapters = selectedCourse ? selectedCourse.chapters : [];
  console.log(selectedCourse, id);

  return (
    <>
      <Container additionalClassName="min-h-screen text-white">
        <ol className="relative space-y-8 before:absolute before:-ml-px before:h-full before:w-0.5 before:rounded-full before:bg-gray-200 dark:before:bg-gray-700">
          {courseChapters.map((item, idx) => {
            return (
              <li
                className="relative -ms-1.5 flex items-start gap-4"
                key={item.id}>
                <span className="size-3 shrink-0 rounded-full bg-blue-600"></span>
                <h2>{item.title}</h2>
                <div className="-mt-2">
                  {item.lessons.map((chap, index) => {
                    const chapterNum = `№${index + 1}`;
                    return (
                      <p key={index} className="py-2">
                        <span>📘 Lesson {chapterNum}</span>- {chap.title} - Type{" "}
                        {chap.type}
                      </p>
                    );
                  })}
                </div>
              </li>
            );
          })}
        </ol>
      </Container>
    </>
  );
};

export default CourseDetail;
