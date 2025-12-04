"use client";

import React, { useEffect, useState } from "react";
import { loginSchema } from "@/utils/validation";
import { useAuthStore } from "@/stores/useAuthStore";
import { authService } from "@/services/authService";
import Input from "@/components/UI/Input";
import Button from "@/components/UI/Button";
import z from "zod";

const SigninForm: React.FC = () => {
  const { username, password, loading, setUsername, setPassword, setLoading } =
    useAuthStore();

  const [errors, setErrors] = useState<Record<string, string>>({});

  const [disabled, setDisabled] = useState<boolean>(false);

  useEffect(() => {
    const checkIsDisabled = loading || !username || !password;
    setDisabled(checkIsDisabled);
  }, [loading, password, username]);

  const validateField = async (field: string, value: string) => {
    const result = await loginSchema
      .pick({ [field]: true })
      .safeParseAsync({ [field]: value });

    console.log(field, value);

    try {
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
    } catch (err) {
      return `Failed to parse error when validating input`;
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

    try {
      // Validate entire form
      await loginSchema.parseAsync({ username, password });

      // Clear any existing errors
      setErrors({});

      //   await authService.signIn(username, password);
    } catch (error) {
      if (error instanceof z.ZodError) {
        // Set Zod validation errors
        const fieldErrors: Record<string, string> = {};
        console.log(error);
        setErrors(fieldErrors);
      } else {
      }
    } finally {
      setLoading(false);
    }
  };

  const handleEnter = () => {
    if (!loading) {
      handleSubmit(new Event("submit") as any);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="w-lg mx-auto p-6 bg-white rounded-lg shadow-md">
      <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">
        Welcome 😘
      </h2>

      {/* Username Field */}
      <div className="mb-4">
        <label
          htmlFor="email"
          className="block text-sm font-medium text-gray-700 mb-1">
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
          className="w-full p-3"
        />
      </div>

      {/* Password Field */}
      <div className="mb-6">
        <label
          htmlFor="password"
          className="block text-sm font-medium text-gray-700 mb-1">
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
        isDisabled={disabled}
        variant="primary"
        className="w-full">
        Sign in
      </Button>
    </form>
  );
};

export default SigninForm;
