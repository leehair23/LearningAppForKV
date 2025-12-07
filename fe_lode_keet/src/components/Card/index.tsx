import Button from "../Button";

const Card = (props: { title: string; description: string }) => {
  const { title, description } = props;
  return (
    <div className="bg-slate-600 block max-w-sm p-6 border rounded-2xl shadow-xs">
      <h5 className="mb-3 text-2xl font-semibold tracking-tight text-heading leading-8">
        {title}
      </h5>
      <p className="text-body mb-6">{description}</p>
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
    </div>
  );
};

export default Card;
