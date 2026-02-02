import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";

const Logout = () => {
  const { setIsAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    setIsAuthenticated(false);
    navigate("/login");
  }, [setIsAuthenticated, navigate]); // ✅ dependencies added

  return null;
};

export default Logout;
