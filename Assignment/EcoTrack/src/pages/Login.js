import { useNavigate } from "react-router-dom";

const Login = () => {
  const navigate = useNavigate();

  const login = () => {
    localStorage.setItem("token", "fake-login");
    navigate("/dashboard");
  };

  return (
    <div className="page-center">
      <div className="tracker">
        <h2>EcoTrack Login</h2>
        <button onClick={login}>Login</button>
      </div>
    </div>
  );
};

export default Login;