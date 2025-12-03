"use client";

import Container from "@/components/UI/Container";
import Divider from "@/components/UI/Divider";
import Footer from "@/components/UI/Footer";
import Navbar from "@/components/UI/Navbar";

export default function Dashboard() {
  return (
    <>
      <Navbar></Navbar>
      <Container>
        <p>Dashboard</p>
      </Container>
      <Divider></Divider>
      <Footer></Footer>
    </>
  );
}
