import React, { useEffect } from "react";
import Card from "@/components/Card";
import Container from "@/components/Container";
import InfiniteScroll from "react-infinite-scroll-component";
import Loading from "@/components/Loading";
import { useCourseStore } from "@/stores/useCourseStore";
import { courseService } from "@/services/courseService";

const Courses: React.FC = () => {
  const { courses, hasMoreData } = useCourseStore();
  const courseLength = courses ? courses.length : 0;
  const fetchMoreData = () => {};

  const fetchCourses = async () => {
    await courseService.getCourses();
  };

  useEffect(() => {
    fetchCourses();
  }, []);

  return (
    <Container>
      <section className="w-full min-h-screen py-5 flex flex-col bg-gray-50">
        <div className="max-w-9xl mx-auto">
          <h2 className="text-3xl md:text-4xl font-bold mb-12 text-gray-800">
            Here are the courses that might interest you
          </h2>
          <div className="px-1">
            <InfiniteScroll
              dataLength={courseLength}
              next={fetchMoreData}
              hasMore={hasMoreData}
              loader={<Loading withText text=" Loading ..." />}>
              <div className="grid grid-cols-1 xl:grid-cols-2 gap-6 text-md tracking-tight">
                {courses?.map((item, ind) => (
                  <Card key={ind} data={item} />
                ))}
              </div>
            </InfiniteScroll>
          </div>
        </div>
      </section>
    </Container>
  );
};

export default Courses;
