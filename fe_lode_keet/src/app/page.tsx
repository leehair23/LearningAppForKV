"use client";

import Container from "@/components/Container";
import Divider from "@/components/Divider";
import Footer from "@/components/Footer";
import Input from "@/components/Input";
import Navbar from "@/components/Navbar";
import { useState } from "react";

export default function Home() {
  const [searchValue, setSearchValue] = useState("");
  const handleSearch = () => {
    console.log("Searching for:", searchValue);
  };
  return (
    <>
      <Navbar></Navbar>
      <Container>
        <div className="h-auto rounded bg-amber-500">Left small container</div>
        <div className="h-auto rounded bg-gray-300 lg:col-span-2">
          <div>
            <label htmlFor=""></label>
            <Input
              value={searchValue}
              onChange={setSearchValue}
              onEnter={handleSearch}
              name="password-input"
              placeholder="Enter password"
              autoFocus
              type="password"></Input>
          </div>
        </div>
      </Container>
      <Divider></Divider>
      <Footer></Footer>
    </>
  );
}
