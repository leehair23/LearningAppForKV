export const Container = (props: { children: React.ReactNode }) => (
  <div className="p-6 h-screen grid grid-cols-1 gap-4 lg:grid-cols-3 lg:gap-8 bg-slate-800">
    {props.children}
  </div>
);
