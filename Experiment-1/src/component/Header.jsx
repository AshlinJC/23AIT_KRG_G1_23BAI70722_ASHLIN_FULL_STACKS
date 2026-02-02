const Header = ({ title }) => {
  return (
    <header
      style={{
        padding: "1rem",
        backgroundColor: "#27ae60",
        color: "white",
        textAlign: "center",
      }}
    >
      <h1>{title}</h1>
    </header>
  );
};

export default Header;