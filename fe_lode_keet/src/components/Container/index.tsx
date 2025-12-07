const Container = (props: { children: React.ReactNode }) => (
  <div className="flex flex-col items-center justify-center bg-slate-800">
    {/* p-6  grid grid-cols-1 gap-4 lg:grid-cols-3 lg:gap-8  */}
    {props.children}
  </div>
);

export default Container;
