import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

const publicRoutes = ["/", "/about", "/sign-in", "/sign-up"];
const protectedRoutes = ["/dashboard", "/admin", "/profile"];
const adminRoutes = ["/admin"];

export const middleware = (req: NextRequest) => {
  const { pathname } = req.nextUrl;
  const token = req.headers.has("Authentication");

  if (!token) {
  }
};

export const config = {
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico).*)"],
};
