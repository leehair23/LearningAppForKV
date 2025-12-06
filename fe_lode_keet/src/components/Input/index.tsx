import React, { useEffect, useRef, type InputHTMLAttributes } from "react";

interface InputProps
  extends Omit<InputHTMLAttributes<HTMLInputElement>, "onChange"> {
  value: string;
  name: string;
  autoFocus?: boolean;
  className?: string;
  type?: string;
  error?: string;
  showError?: boolean;
  onChange: (value: string) => void;
  onEnter?: () => void;
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
  };

  const handleBlur = () => {};

  const handleFocus = () => {};

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && onEnter) {
      onEnter();
    }
  };

  // Base styles
  const baseStyles = `
      px-3 py-2 mx-0 my-2 border border-gray-300 rounded-md shadow-sm
      placeholder:text-gray-400
      focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500
      transition-colors duration-200 ease-in-out
      disabled:bg-gray-100 disabled:cursor-not-allowed
      [&:not(:focus)]:placeholder:text-gray-400
  `;

  // Error styles
  const errorStyles = `
    border-red-500 focus:ring-red-500 focus:border-red-500 placeholder-gray-400
  `;

  // Success styles (when valid and touched)
  const successStyles = `
    border-green-500 focus:ring-green-500 focus:border-green-500
  `;

  const getInputStyles = () => {
    let styles =
      className.length === 0 ? baseStyles : `${className} ${baseStyles}`;

    if (showError && error) {
      styles += ` ${errorStyles}`;
    } else if (!error && value) {
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
