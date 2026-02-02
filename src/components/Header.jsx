import { Link } from "react-router-dom";

const Header = () => {
  return (
    <header
      style={{
        padding: "10px",
        backgroundColor: "#5499f8",
        color: "white",
        textAlign: "center",
      }}
    >
      <h1>EcoTrack</h1>

      <Link to="/">Dashboard</Link>{" | "}
      <Link to="/logs">Logs</Link>{" | "}
      <Link to="/login">Login</Link>{" | "}
      <Link to="/logout">Logout</Link>
    </header>
  );
};

export default Header;
