import { redirect } from "next/navigation";
import { cookies } from "next/headers";
import { Connstants } from "@/common/constants";
import { authStore } from "@/stores/authStore";

export default async function Home() {
  const cookieStore = await cookies();
  const token = authStore().accessToken;
  const { ROUTES } = Connstants;

  /**
   * NOTE: this is wrong approach, due to Zustand being a client side only
   * => need to find a better way, probably in middlewares we will use localStorage
   * => it would not be the best but time is not on our side
   */

  // Check for token is not valid, then redirect to /signin
  redirect(ROUTES.PUBLIC.SIGN_IN);

  // Check for token is valid, then redirect to /dashboard
}
