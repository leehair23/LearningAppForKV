export const Divider = (props: { title?: string }) => {
  return (
    <span className="flex items-center">
      <span className="h-px flex-1 bg-gray-300 dark:bg-gray-600"></span>

      <span className="shrink-0 px-4 text-gray-900 dark:text-white">
        <p>{props.title}</p>
      </span>

      <span className="h-px flex-1 bg-gray-300 dark:bg-gray-600"></span>
    </span>
  );
};
