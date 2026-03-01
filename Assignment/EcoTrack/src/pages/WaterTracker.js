import { useState, useEffect, useCallback } from "react";
import Navbar from "../components/Navbar";
import CounterDisplay from "../components/CounterDisplay";

const WaterTracker = () => {
  const [count, setCount] = useState(0);
  const [goal, setGoal] = useState(8);
  const [tip, setTip] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Load saved count
  useEffect(() => {
    const saved = localStorage.getItem("waterCount");
    if (saved) setCount(Number(saved));
  }, []);

  // Save count
  useEffect(() => {
    localStorage.setItem("waterCount", count);
  }, [count]);

  // Fetch health tip
  useEffect(() => {
    fetch("https://api.adviceslip.com/advice")
      .then((res) => res.json())
      .then((data) => {
        setTip(data.slip.advice);
        setLoading(false);
      })
      .catch(() => {
        setError("Failed to load health tip");
        setLoading(false);
      });
  }, []);

  // Optimized handlers
  const addWater = useCallback(() => {
    setCount((prev) => prev + 1);
  }, []);

  const removeWater = useCallback(() => {
    setCount((prev) => Math.max(prev - 1, 0));
  }, []);

  const reset = useCallback(() => {
    setCount(0);
  }, []);

  return (
    <>
      <Navbar />

      <div className="app-container">
        <div className="page-center">
          <div className="tracker">
            <h2>Daily Water Intake Tracker</h2>

            <CounterDisplay count={count} goal={goal} />

            {/* Progress Box */}
            <div className="progress-box">
              <div className="progress-bar-container">
                <div 
                  className="progress-bar"
                  style={{ width: `${Math.min((count / goal) * 100, 100)}%` }}
                ></div>
              </div>
            </div>

            {/* Buttons with proper spacing */}
            <div className="button-group">
              <button onClick={addWater}>+</button>
              <button onClick={removeWater}>-</button>
              <button onClick={reset}>Reset</button>
            </div>

            {/* Goal input */}
            <div className="goal-row">
              <label>Daily Goal:</label>
              <input
                type="number"
                value={goal}
                onChange={(e) => setGoal(Number(e.target.value))}
                min="1"
              />
              <span className="glasses-label">glasses</span>
            </div>

            {/* Progress text */}
            <p className="progress-text">
              {count} / {goal} glasses completed
            </p>

            {count >= goal && (
              <div className="achievement-banner">
                🎉 Congratulations! You've reached your daily goal!
              </div>
            )}

            <hr />

            {/* Health tip */}
            {loading && <p className="loading-text">✨ Loading health tip...</p>}
            {error && <p className="error-text">⚠️ {error}</p>}
            {!loading && !error && (
              <p className="health-tip">
                <strong>💡 Today's Health Tip:</strong> {tip}
              </p>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default WaterTracker;