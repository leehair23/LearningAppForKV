import Button from "../Button";
import type { ICourse } from "@/common/interfaces";
import { Link } from "react-router-dom";
import { Constants } from "@/common/constants";
import { Badge } from "../Badge/badge";
import { useCourseStore } from "@/stores/useCourseStore";

const Card = (props: { data: ICourse }) => {
  const { title, description, chapters, id, level, createdAt } = props.data;
  const hasChapters = chapters.length !== 0;
  const linkTo = `${Constants.ROUTES.DASHBOARD.COURSES}/${id}`;

  const onHandleClickLink = () => {
    useCourseStore.getState().setSelectedCourse(props.data);
  };

  return (
    <div className="bg-slate-600 block min-w-lg max-w-lg py-5 px-6 my-3 border rounded-2xl shadow-xs">
      <h5 className="mb-3 text-3xl font-semibold tracking-tight text-heading leading-8">
        {title}
      </h5>
      <p className="text-body mb-6">{description}</p>
      <div className="flex gap-2 mb-4">
        <Badge variant="default">{level}</Badge>
        <Badge variant="destructive">{createdAt}</Badge>
      </div>
      {!hasChapters ? (
        <p className="my-3 text-wrap">Course is under construction 🛠</p>
      ) : (
        <>
          <Link to={linkTo} onClick={onHandleClickLink}>
            <Button className="px-4 py-2 text-white-600 hover:text-white-800 border border-gray-300 rounded-lg hover:border-gray-400 transition-colors">
              Try it out{" "}
              <svg
                className="w-4 h-4 ms-1.5 rtl:rotate-180 -me-0.5"
                aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                fill="none"
                viewBox="0 0 24 24">
                <path
                  stroke="currentColor"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M19 12H5m14 0-4 4m4-4-4-4"
                />
              </svg>
            </Button>
          </Link>
        </>
      )}
    </div>
  );
};

export default Card;
