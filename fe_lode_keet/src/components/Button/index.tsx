import Loading from "../Loading";
import React, {
  forwardRef,
  useState,
  useEffect,
  type ButtonHTMLAttributes,
} from "react";

interface ButtonProps
  extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, "onChange"> {
  children: React.ReactNode;
  variant?: "primary" | "secondary" | "danger" | "ghost";
  size?: "sm" | "md" | "lg";
  loading?: boolean;
  isDisabled?: boolean;
  autoFocus?: boolean;
  className?: string;
  onChange?: (value: string) => void;
}

const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      children,
      variant = "primary",
      size = "md",
      loading = false,
      isDisabled = false,
      autoFocus = false,
      className = "",
      onChange,
      onClick,
      ...props
    },
    ref
  ) => {
    const [dirty, setDirty] = useState(false);
    const [touched, setTouched] = useState(false);
    const [isHovered, setIsHovered] = useState(false);

    // Handle auto focus
    useEffect(() => {
      if (autoFocus && ref && "current" in ref && ref.current) {
        ref.current.focus();
      }
    }, [autoFocus, ref]);

    const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
      if (!dirty) setDirty(true);
      if (!touched) setTouched(true);

      // Handle value change if onChange is provided
      if (onChange) {
        onChange(e.currentTarget.value || "clicked");
      }

      onClick?.(e);
    };

    const handleMouseEnter = () => {
      setIsHovered(true);
    };

    const handleMouseLeave = () => {
      setIsHovered(false);
    };

    const handleBlur = () => {
      setTouched(true);
    };

    // Base styles
    const baseStyles = `
      inline-flex items-center justify-center
      font-medium rounded-lg
      transition-all duration-200 ease-in-out
      focus:outline-none focus:ring-2 focus:ring-offset-2
      disabled:opacity-50 disabled:cursor-not-allowed
      active:scale-95
    `;

    // Variant styles
    const variantStyles = {
      primary: `
        bg-blue-600 text-white
        hover:bg-blue-700
        focus:ring-blue-500
        ${isHovered ? "shadow-md" : ""}
      `,
      secondary: `
        bg-gray-200 text-gray-800
        hover:bg-gray-300
        focus:ring-gray-400
        ${isHovered ? "shadow" : ""}
      `,
      danger: `
        bg-red-600 text-white
        hover:bg-red-700
        focus:ring-red-500
        ${isHovered ? "shadow-md" : ""}
      `,
      ghost: `
        bg-transparent text-gray-700 border border-gray-300
        hover:bg-gray-50
        focus:ring-gray-400
        ${isHovered ? "border-gray-400" : ""}
      `,
    };

    // Size styles
    const sizeStyles = {
      sm: "px-3 py-1.5 text-sm",
      md: "px-4 py-2 text-base",
      lg: "px-6 py-3 text-lg",
    };

    // Loading styles
    const loadingStyles = loading ? "opacity-70 cursor-wait" : "";

    // Dirty/Touched indicator styles
    const stateStyles = dirty ? "ring-1 ring-blue-300" : "";
    const touchedStyles = touched ? "ring-offset-1" : "";

    // Combine all styles
    const buttonClasses = `
      ${baseStyles}
      ${variantStyles[variant]}
      ${sizeStyles[size]}
      ${loadingStyles}
      ${stateStyles}
      ${touchedStyles}
      ${className}
    `
      .replace(/\s+/g, " ")
      .trim();

    return (
      <button
        ref={ref}
        className={buttonClasses}
        onClick={handleClick}
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
        onBlur={handleBlur}
        disabled={isDisabled || loading}
        {...props}>
        {loading ? (
          <>
            <Loading withText text="Loading" />
          </>
        ) : (
          children
        )}
      </button>
    );
  }
);

export default Button;
