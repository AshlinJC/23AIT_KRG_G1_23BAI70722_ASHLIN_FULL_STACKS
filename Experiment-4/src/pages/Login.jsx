import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Container, Typography, Button as MuiButton } from "@mui/material";
import { useAuth } from "../context/auth";

const Login = () => {
  const navigate = useNavigate();
  const { isAuthenticated, setIsAuthenticated } = useAuth();
  const [isRedirecting, setIsRedirecting] = useState(false);

  function handleLogin() {
    setIsRedirecting(true);
    setIsAuthenticated(true);
    navigate("/");
  }

  if (isRedirecting) return null;

  if (isAuthenticated) {
    return (
      <Container>
        <Typography variant="h5">You are already logged in</Typography>
        <MuiButton variant="contained" onClick={() => navigate("/")} sx={{ marginTop: 2 }}>Go to Dashboard</MuiButton>
      </Container>
    );
  }

  return (
    <Container className="container login-container">
      <Typography variant="h5" className="page-title">Login</Typography>
      <button className="btn-primary" onClick={handleLogin}>Login Now</button>
    </Container>
  );
};

export default React.memo(Login);
