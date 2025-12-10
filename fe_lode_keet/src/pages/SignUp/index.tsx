import SignupForm from "@/components/SignUpForm";
import Container from "../../components/Container";
import React from "react";

const SignUp: React.FC = () => {
  return (
    <Container additionalClassName="h-screen">
      <SignupForm />
    </Container>
  );
};

export default SignUp;
