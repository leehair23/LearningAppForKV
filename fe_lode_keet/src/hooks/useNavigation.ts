"use client";

import { useRouter, usePathname, useSearchParams } from "next/navigation";

export function useNavigation() {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const navigationFnc = {
    to: (path: string) => router.push(path),
    replace: (path: string) => router.replace(path),
    back: () => router.back(),
    forward: () => router.forward(),
    refresh: () => router.refresh(),
  };

  const isActive = (path: string, exact = false) => {
    if (exact) {
      return pathname === path;
    }
    return pathname.startsWith(path);
  };

  const createQueryString = (params: Record<string, string>) => {
    const newSearchParams = new URLSearchParams(searchParams.toString());
    Object.entries(params).forEach(([key, value]) => {
      newSearchParams.set(key, value);
    });
    return newSearchParams.toString();
  };

  return {
    ...navigationFnc,
    createQueryString,
    isActive,
    pathname,
    searchParams,
  };
}
