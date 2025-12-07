import React, { useEffect } from "react";
import Card from "@/components/Card";
import Container from "@/components/Container";
import InfiniteScroll from "react-infinite-scroll-component";
import { useCourseStore } from "@/stores/useCourseStore";
import Loading from "@/components/Loading";

const Courses: React.FC = () => {
  const { courses, loading, hasMoreData } = useCourseStore.getState();
  const fetchMoreData = () => {};
  const courseLength = courses ? courses.length : 0;
  useEffect(() => {}, []);

  return (
    <Container>
      <section className="w-full py-5 flex flex-col bg-gray-50">
        <div className="max-w-9xl mx-auto">
          <h2 className="text-3xl md:text-4xl font-bold mb-12 text-gray-800">
            Here are the courses that might interest you
          </h2>
          <div className="px-1">
            <div className="grid grid-cols-1 xl:grid-cols-3 gap-6 text-md tracking-tight">
              <InfiniteScroll
                dataLength={courseLength}
                next={fetchMoreData}
                hasMore={hasMoreData}
                loader={<Loading />}>
                {courses?.map((item, ind) => (
                  <Card
                    key={ind}
                    title={item.title}
                    description={item.description}
                  />
                ))}
              </InfiniteScroll>
            </div>
          </div>
        </div>
      </section>
    </Container>
  );
};

export default Courses;
