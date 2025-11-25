const Footer = () => {
  const currentYear = new Date().getFullYear();
  return (
    <footer className="w-full flex items-center bg-slate-800 justify-center text-center sm:text-left">
      <div className="text-center w-full h-fit px-10 py-7">
        <p className="text-sm text-white sm:text-base">
          Built by my nigga{" "}
          <a
            className="text-white underline"
            href="https://github.com/Kieuviet34">
            KieuViet34
          </a>{" "}
          and{" "}
          <a
            className="text-white underline"
            href="https://github.com/leehair23">
            LeeHair23
          </a>
        </p>
        <div className="text-center text-white pt-6">
          © {currentYear} Lode Keet - All rights reserved.
        </div>
      </div>
    </footer>
  );
};

export default Footer;
