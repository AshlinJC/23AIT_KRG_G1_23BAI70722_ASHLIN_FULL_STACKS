import { logs } from "../data/logs";

console.log("Dashboard loaded", logs);

const Dashboard = () => {
  const totalCarbon = logs.reduce(
    (sum, log) => sum + log.carbon,
    0
  );

  return (
    <div>
      <h2>Dashboard</h2>
      <p>Total Carbon Footprint: {totalCarbon} Kg</p>
    </div>
  );
};

export default Dashboard;