"use client";

import React, { InputHTMLAttributes, useState, useEffect, useRef } from "react";

interface InputProps
  extends Omit<InputHTMLAttributes<HTMLInputElement>, "onChange"> {
  value: string;
  name: string;
  onChange: (value: string) => void;
  onEnter?: () => void;
  autoFocus?: boolean;
  className?: string;
  type?: string;
  error?: string;
  showError?: boolean;
}

const Input: React.FC<InputProps> = ({
  value,
  onChange,
  onEnter,
  autoFocus = false,
  className = "",
  type = "text",
  error = "",
  showError = false,
  name,
  ...props
}) => {
  const [dirty, setDirty] = useState(false);
  const [touched, setTouched] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  // Auto focus
  useEffect(() => {
    if (autoFocus && inputRef.current) {
      inputRef.current.focus();
    }
  }, [autoFocus]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;
    onChange(newValue);

    if (!dirty && newValue !== "") {
      setDirty(true);
    }
  };

  const handleBlur = () => {
    setTouched(true);
  };

  const handleFocus = () => {
    setTouched(true);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && onEnter) {
      onEnter();
    }
  };

  // Base styles
  const baseStyles = `
    w-80 px-3 py-2 m-4 border border-gray-300 rounded-md shadow-sm
    placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500
    transition-colors duration-200 ease-in-out
    disabled:bg-gray-100 disabled:cursor-not-allowed
  `;

  // Error styles
  const errorStyles = `
    border-red-500 focus:ring-red-500 focus:border-red-500
    bg-red-50 placeholder-red-200
  `;

  // Success styles (when valid and touched)
  const successStyles = `
    border-green-500 focus:ring-green-500 focus:border-green-500
  `;

  const getInputStyles = () => {
    let styles = className.length === 0 ? baseStyles : className;

    if (showError && error) {
      styles += ` ${errorStyles}`;
    } else if (touched && !error && value) {
      styles += ` ${successStyles}`;
    }

    return `${styles} ${className}`;
  };

  return (
    <div className="w-full">
      <input
        ref={inputRef}
        type={type}
        name={name}
        value={value}
        onChange={handleChange}
        onBlur={handleBlur}
        onFocus={handleFocus}
        onKeyDown={handleKeyDown}
        className={getInputStyles()}
        {...props}
      />

      {showError && error && (
        <p className="mt-1 text-sm text-red-600 flex items-center gap-1">
          <span>❌</span>
          {error}
        </p>
      )}
    </div>
  );
};

export default Input;
