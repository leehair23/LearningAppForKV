import Container from "../../components/Container";
import SigninForm from "../../components/SignInForm";
import React from "react";

const SignIn: React.FC = () => {
  return (
    <Container additionalClassName="h-screen">
      <SigninForm />
    </Container>
  );
};

export default SignIn;
