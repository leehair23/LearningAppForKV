import { redirect } from "next/navigation";
import { Constants } from "@/common/constants";
import Footer from "@/components/UI/Footer";
import Divider from "@/components/UI/Divider";
import Navbar from "@/components/UI/Navbar";

export default function Home({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const { ROUTES } = Constants;

  const accessToken = localStorage.getItem(
    Constants.LOCAL_STORAGE_KEYS.ACC_TOKEN
  );

  if (!accessToken) {
    redirect(ROUTES.PUBLIC.SIGN_IN);
  }
  return (
    <>
      <Navbar></Navbar>
      {children}
      <Divider></Divider>
      <Footer></Footer>
    </>
  );
}
