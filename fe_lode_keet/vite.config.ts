import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 3000, // Set the desired port here
  },
  define: {
    "process.env": import.meta,
  },
  resolve: {
    alias: {
      process: "process/browser",
      buffer: "buffer",
      stream: "stream-browserify",
      zlib: "browserify-zlib",
      util: "util/",
      "@": path.resolve(__dirname, "./src"),
    },
  },
});
