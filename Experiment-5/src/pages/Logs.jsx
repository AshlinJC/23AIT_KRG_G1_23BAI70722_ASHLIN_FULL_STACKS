import { useSelector, useDispatch } from "react-redux";
import { fetchLogs } from "../logsSlice";
import { useEffect } from "react";

const Logs = () => {
  const dispatch = useDispatch();
  const { data, status, error } = useSelector((state) => state.logs);

  useEffect(() => {
    if (status === "idle") {
      dispatch(fetchLogs());
    }
  }, [status, dispatch]);

  const handleRefresh=()=>{
    dispatch(fetchLogs());
  };

  if (status === "loading") {
    return <p>Loading Logs...</p>;
  }

  if (status === "failed") {
    return <p>Error: {error}</p>;
  }

  return (
    <div style={{ padding: "1rem" }}>
      <h3>Daily Logs (Redux)</h3>

      
      <ul>
        {data.map((log) => (
          <li key={log.id}>
            {log.activity} — {log.carbon} kg CO₂
          </li>
        ))}
      </ul>

      <button
      onClick={handleRefresh}
      style={{
        marginBottom: "1rem",
        padding: "0.5rem 1rem",
        backgroundColor: "#2ecc71",
        color: "#fff",
        border: "none",
        borderRadius: "6px",
        cursor: "pointer",
        fontWeight: "600",
        transition: "background-color 0.2s ease, transform 0.1s ease"
      }}
      onMouseOver={(e) => (e.target.style.backgroundColor = "#27ae60")}
      onMouseOut={(e) => (e.target.style.backgroundColor = "#2ecc71")}
      >
      Refresh
      </button>

    </div>
  );
};

export default Logs;
