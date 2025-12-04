"use client";

import SigninForm from "@/components/Auth/LoginForm";
import Container from "@/components/UI/Container";
import { useState } from "react";

export default function Dashboard() {
  const [searchValue, setSearchValue] = useState("");
  const handleSearch = () => {
    console.log("Searching for:", searchValue);
  };
  return (
    <>
      <Container>
        <div className="h-auto rounded bg-amber-500"></div>
        <div className="h-auto rounded flex items-center justify-center bg-gray-300 lg:col-span-2">
          <SigninForm />
        </div>
      </Container>
    </>
  );
}
