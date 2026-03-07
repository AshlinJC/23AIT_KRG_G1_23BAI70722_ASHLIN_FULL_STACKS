import React from "react";

const CounterChild = React.memo(({ onIncrement, total }) => {
  console.log("Child Rendered");

  return (
    <div style={{ marginTop: "20px" }}>
      <h3>Total: {total}</h3>
      <button onClick={onIncrement}>Increment Count</button>
    </div>
  );
});

export default CounterChild;