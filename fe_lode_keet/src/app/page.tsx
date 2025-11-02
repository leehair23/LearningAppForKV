import { Container } from "@/components/Container";
import { Divider } from "@/components/Divider";
import { Footer } from "@/components/Footer";

export default function Home() {
  return (
    <>
      <Container>
        <div className="h-auto rounded bg-amber-500">Left small container</div>
        <div className="h-auto rounded bg-gray-300 lg:col-span-2">
          Right big container
        </div>
      </Container>
      <Divider></Divider>
      <Footer></Footer>
    </>
  );
}
