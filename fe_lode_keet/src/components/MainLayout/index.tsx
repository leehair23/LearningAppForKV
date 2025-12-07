import Footer from "../Footer";
import Navbar from "../Navbar";

const MainLayout = (props: { children: React.ReactNode }) => {
  return (
    <>
      <div className="min-h-screen transition-all duration-300 w-full ">
        <Navbar />

        {props.children}

        <Footer></Footer>
      </div>
    </>
  );
};

export default MainLayout;
