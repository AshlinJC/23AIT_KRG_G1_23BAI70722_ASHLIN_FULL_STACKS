import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

const Logout = () => {
    const { setIsAuthenticated } = useAuth();   
    const navigate = useNavigate(); 

    const handleLogout = () => {
        
        setIsAuthenticated(false);
        navigate("/login");
    };

    return (
        <div>
            <button onClick={handleLogout}>Logout</button>
        </div>
    );
};

export default Logout;
