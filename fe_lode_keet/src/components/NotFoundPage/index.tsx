import { Link } from "react-router-dom";
import Button from "../Button";
import Container from "../Container";

const NotFoundPage = () => (
  <Container>
    <p className="py-5 text-5xl">Page is not found ❌</p>

    <div className="pt-8">
      <Link to={"/"}>
        <Button>Go back Home</Button>
      </Link>
    </div>
  </Container>
);

export default NotFoundPage;
