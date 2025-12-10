import Container from "../../components/Container";
import SigninForm from "../../components/SigninForm";
import React from "react";

const SignIn: React.FC = () => {
  return (
    <Container additionalClassName="h-screen">
      <SigninForm />
    </Container>
  );
};

export default SignIn;
