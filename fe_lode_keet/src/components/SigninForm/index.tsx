import React, { useState } from "react";
import { loginSchema } from "@/utils/validation";
import { useAuthStore } from "@/stores/useAuthStore";
import { authService } from "@/services/authService";
import { useNavigate } from "react-router-dom";
import Input from "../Input";
import Button from "../Button";
import { Constants } from "@/common/constants";
import { toast } from "sonner";

const SigninForm: React.FC = () => {
  const { username, password, loading, setUsername, setPassword } =
    useAuthStore();
  const navigate = useNavigate();
  const [errors, setErrors] = useState<Record<string, string>>({});
  const checkIsDisabled = loading || !username || !password;

  const validateField = async (field: string, value: string) => {
    const result = await loginSchema
      .pick({ [field]: true })
      .safeParseAsync({ [field]: value });

    if (result.success) {
      setErrors((prev) => {
        const newErrors = { ...prev };
        delete newErrors[field];
        return newErrors;
      });
    } else {
      const zodErr = result.error;
      const jsoniFiedErrors = JSON.parse(zodErr.message);
      if (jsoniFiedErrors[0]) {
        setErrors((prev) => {
          const newErrors = { ...prev };
          newErrors[field] = jsoniFiedErrors[0]?.message || "Unknown error";
          return newErrors;
        });
      }
    }
  };

  const handleUsernameChange = (value: string) => {
    setUsername(value);
    validateField("username", value);
  };

  const handlePasswordChange = (value: string) => {
    setPassword(value);
    validateField("password", value);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const result = await loginSchema.safeParseAsync({ username, password });

    if (result.success) {
      setErrors({});
      const result = await authService.signIn(username, password);
      if (result) {
        navigate(Constants.ROUTES.DASHBOARD.HOME);
        toast.success("Sign in successfully✅");
        return; // Return to stop form submission twice
      }
    } else {
      toast.error("❌Error occurred during signing in!");
      const zodErr = result.error;
      const jsoniFiedErrors = JSON.parse(zodErr.message);
      if (jsoniFiedErrors[0]) {
        setErrors((prev) => {
          const newErrors = { ...prev };
          const field = jsoniFiedErrors[0]?.path[0];
          newErrors[field] = jsoniFiedErrors[0]?.message || "Unknown error";
          return newErrors;
        });
      }
    }
  };

  const handleEnter = () => {
    if (!loading) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      handleSubmit(new Event("submit") as any);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="w-lg mx-auto p-6 bg-opacity-50 text-white border-2 border-solid rounded-lg shadow-md">
      <h2 className="text-2xl font-bold text-center text-white mb-6">
        Welcome 😘
      </h2>

      {/* Username Field */}
      <div className="mb-6">
        <label
          htmlFor="email"
          className="block text-xl text-white font-semibold mb-1">
          User name
        </label>
        <Input
          id="username"
          name="username"
          type="text"
          value={username}
          onChange={handleUsernameChange}
          placeholder="Enter your user name"
          error={errors.username}
          showError={!!errors.username}
          autoFocus
          className="w-full p-3 text-white"
        />
      </div>

      {/* Password Field */}
      <div className="mb-6">
        <label
          htmlFor="password"
          className="block text-xl text-white font-semibold mb-1">
          Password
        </label>
        <Input
          id="password"
          name="password"
          type="password"
          value={password}
          onChange={handlePasswordChange}
          onEnter={handleEnter}
          placeholder="Enter your password"
          error={errors.password}
          showError={!!errors.password}
          className="w-full p-3"
        />
      </div>

      <Button
        type="submit"
        loading={loading}
        isDisabled={checkIsDisabled}
        variant="primary"
        className="w-full">
        Sign in
      </Button>
    </form>
  );
};

export default SigninForm;
