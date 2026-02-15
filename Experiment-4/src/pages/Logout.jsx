import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Typography } from "@mui/material";
import { useAuth } from "../context/auth";

const Logout = () => {
  const navigate = useNavigate();
  const { setIsAuthenticated } = useAuth();

  useEffect(() => {
    setIsAuthenticated(false);
    navigate("/login");
  }, []);

  return <Typography variant="h6">Logging out...</Typography>;
};

export default React.memo(Logout);
