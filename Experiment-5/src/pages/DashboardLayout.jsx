import { Link, Outlet } from "react-router-dom";
import Logout from "./Logout";

const DashboardLayout = () => {
    return (
        <div>
             <hr />
            <h2>Dashboard</h2>

            <nav>
                <Link to = "settings">Settings</Link>
                <Link to = "summary"> Summary</Link>
                <Link to = "analytics"> Analytics</Link>            
            </nav>

            

            <Outlet />
            <Logout />
        </div>

    )
}

export default DashboardLayout;