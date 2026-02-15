import { Navigate } from "react-router-dom";
import { useAuth } from "../context/auth";

export default function AuthProtectedRoutes({ children }) {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
}
