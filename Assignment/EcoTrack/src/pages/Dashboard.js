import Navbar from "../components/Navbar";

const Dashboard = () => {
  return (
    <>
      <Navbar />
      <div className="app-container">
        <div className="page-center">
          <div className="tracker">
            <h2>Welcome to EcoTrack</h2>
            <p style={{ fontSize: "16px", color: "#374151", marginBottom: "20px" }}>
              Track your water intake and stay healthy!
            </p>
            <a href="/dashboard/water" style={{ color: "#94BBE9", textDecoration: "none", fontWeight: "600", fontSize: "16px" }}>
              Go to Water Tracker →
            </a>
          </div>
        </div>
      </div>
    </>
  );
};

export default Dashboard;