export const NavbarItem = (props: {
  path?: string;
  title?: string;
  isBlockContainer?: boolean;
}) => {
  let customClass =
    "text-white py-[3px] px-[15px] hover:bg-white hover:text-black rounded-lg";

  if (props.isBlockContainer) {
    customClass += " block";
  }

  return (
    <a href={props.path} className={customClass}>
      {props.title}
    </a>
  );
};
