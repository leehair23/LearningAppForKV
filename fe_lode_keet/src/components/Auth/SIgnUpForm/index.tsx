"use client";

import React, { useEffect, useState } from "react";
import { loginSchema } from "@/utils/validation";
import { useAuthStore } from "@/stores/useAuthStore";
import { authService } from "@/services/authService";
import Input from "@/components/UI/Input";
import Button from "@/components/UI/Button";
import z from "zod";

const SignupForm: React.FC = () => {
  const {
    email,
    username,
    password,
    loading,
    error: storeError,
    setEmail,
    setUsername,
    setPassword,
    setLoading,
    setError,
  } = useAuthStore();

  const [errors, setErrors] = useState<Record<string, string>>({});

  const [disabled, setDisabled] = useState<boolean>(false);

  useEffect(() => {
    const checkIsDisabled = loading || !username || !password;
    setDisabled(checkIsDisabled);
  }, [loading, password, username]);

  const validateField = async (field: string, value: string) => {
    /**
     *  NEED TO CHECK AGAIN FOR THIS
     */
    try {
      await loginSchema.pick({ [field]: true }).parseAsync({ [field]: value });
      setErrors((prev) => {
        const newErrors = { ...prev };
        console.log(newErrors);
        delete newErrors[field];
        return newErrors;
      });
      setError(null);
    } catch (error) {
      //   if (error instanceof z.ZodError) {
      //     setErrors((prev) => ({
      //       ...prev,
      //       [field]: error?.errors[0].message,
      //     }));
      //   }
    }
  };

  const handleUsernameChange = (value: string) => {
    setUsername(value);
    validateField("username", value);
  };

  const handleEmailChange = (value: string) => {
    setEmail(value);
    validateField("email", value);
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
      setError(null);

      await authService.signIn(username, password);
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
      className="max-w-md mx-auto p-6 bg-white rounded-lg shadow-md">
      <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">
        Welcome Back
      </h2>

      {/* Global error message */}
      {storeError && (
        <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-md">
          <p className="text-sm text-red-600">{storeError}</p>
        </div>
      )}

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
          className="w-full"
        />
      </div>

      {/* Email Field */}
      <div className="mb-4">
        <label
          htmlFor="email"
          className="block text-sm font-medium text-gray-700 mb-1">
          Email
        </label>
        <Input
          id="email"
          name="email"
          type="email"
          value={email}
          onChange={handleEmailChange}
          placeholder="you@example.com"
          error={errors.email}
          showError={!!errors.email}
          autoFocus
          className="w-full"
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
          className="w-full"
        />
      </div>

      <Button
        type="submit"
        loading={loading}
        isDisabled={disabled}
        variant="primary"
        className="w-full">
        Sign up
      </Button>
    </form>
  );
};

export default SignupForm;
