import { Route, Routes } from "react-router-dom";
import { Suspense, lazy } from "react";
import Header from "./components/Header";
import ProtectedRoute from "./routes/ProtectedRoute";
import Login from "./pages/Login";
import DashboardLayout from "./pages/DashboardLayout";
import DashboardSummary from "./pages/DashboardSummary";
import DashboardAnalytics from "./pages/DashboardAnalytics";
import DashboardSettings from "./pages/DashboardSettings";
import Logs from "./pages/Logs";

const PerformanceDemo = lazy(() => import("./pages/PerformanceDemo"));

function App() {
  return (
    <>
      <Header />
        <Routes>
          
          <Route path="/login" element={<Login />} />
          <Route
            path="/performance"
            element={
              <ProtectedRoute>
                <Suspense fallback={<div>Loading performance demo...</div>}>
                  <PerformanceDemo />
                </Suspense>
              </ProtectedRoute>
            }
          />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <DashboardLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<DashboardSummary />} />
            <Route path="summary" element={<DashboardSummary />} />
            <Route path="analytics" element={<DashboardAnalytics />} />
            <Route path="settings" element={<DashboardSettings />} />
          </Route>
          <Route
            path="/logs"
            element={
              <ProtectedRoute>
                <Logs />
              </ProtectedRoute>
            }
          />
        </Routes>
    </>
  );
}

export default App;