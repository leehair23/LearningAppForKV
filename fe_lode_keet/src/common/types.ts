export type T_AuthPayload = {
  iss: string;
  sub: string;
  role: string;
  exp: number;
  iat: number;
  email: string;
  userId: string;
};

export type T_UserData = {
  email: string;
  role: string;
  sub: string;
  userId: string;
};
