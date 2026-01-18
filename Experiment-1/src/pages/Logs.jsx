import { logs } from "../data/logs";

const Log = () => {
  const filteredLogs = logs.filter((log) => log.carbon >= 4);

  return (
    <div>
      <h2>Logs (Carbon ≥ 4)</h2>

      <ul>
        {filteredLogs.map((log) => (
          <li key={log.id}>
            ID: {log.id} — Activity: {log.activity}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Log;