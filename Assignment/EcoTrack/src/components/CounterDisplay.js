import React from "react";

const CounterDisplay = ({ count, goal }) => {
  return (
    <h3>
      {count} / {goal} glasses completed
    </h3>
  );
};

export default React.memo(CounterDisplay);