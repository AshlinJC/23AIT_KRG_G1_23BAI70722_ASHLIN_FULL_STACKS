import React from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { Typography } from "@mui/material";
import { useAuth } from "../context/auth";

const Header = () => {
    const { isAuthenticated, setIsAuthenticated } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        setIsAuthenticated(false);
        navigate('/login');
    };

    const navLinkClass = ({ isActive }) => `nav-item ${isActive ? "nav-item--active" : ""}`;

    return (
        <header className="header-banner">
            <Typography component="h1" variant="h4" className="header-title">ECO TRACK</Typography>
            <nav className="nav-links">
                {!isAuthenticated ? (
                    <NavLink to="/login" className={navLinkClass}>Login</NavLink>
                ) : (
                    <>
                        <NavLink to="/summary" end className={navLinkClass}>Summary</NavLink>
                        <NavLink to="/analytics" className={navLinkClass}>Analytics</NavLink>
                        <NavLink to="/logs" className={navLinkClass}>Logs</NavLink>
                        <button onClick={handleLogout} className="logout-btn">Logout</button>
                    </>
                )}
            </nav>
        </header>
    );
};

export default React.memo(Header);