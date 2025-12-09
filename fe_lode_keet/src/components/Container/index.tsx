const Container = (props: {
  children: React.ReactNode;
  additionalClassName?: string;
}) => {
  const { additionalClassName } = props;
  const combinedClassName = `flex flex-col items-center justify-center bg-slate-800 ${additionalClassName}`;
  return (
    <div className={combinedClassName}>
      {/* p-6  grid grid-cols-1 gap-4 lg:grid-cols-3 lg:gap-8  */}
      {props.children}
    </div>
  );
};

export default Container;
