import { Suspense, lazy } from "react";
import Header from "./components/Header";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import AuthProtectedRoutes from "./Routes/AuthProtectedRoutes";

const Logs = lazy(() => import("./pages/Logs"));
const Login = lazy(() => import("./pages/Login"));
const Logout = lazy(() => import("./pages/Logout"));
const Dashboard_layout = lazy(() => import("./pages/Dashboard_layout"));
const DashboardAnalytics = lazy(() => import("./pages/DashboardAnalytics"));
const DashboardSummary = lazy(() => import("./pages/DashboardSummary"));

export default function App() {
  return (
    <BrowserRouter>
      <Header />

      <Suspense fallback={null}>
        <Routes>
        <Route path="/login" element={<Login />} />

        <Route
          path="/logout"
          element={
            <AuthProtectedRoutes>
              <Logout />
            </AuthProtectedRoutes>
          }
        />

        <Route
          path="/"
          element={
            <AuthProtectedRoutes>
              <Dashboard_layout />
            </AuthProtectedRoutes>
          }
        >
          <Route index element={<DashboardSummary />} />
          <Route path="summary" element={<DashboardSummary />} />
          <Route path="analytics" element={<DashboardAnalytics />} />
        </Route>

        <Route
          path="/logs"
          element={
            <AuthProtectedRoutes>
              <Logs />
            </AuthProtectedRoutes>
          }
        />
      </Routes>
      </Suspense>
    </BrowserRouter>
  );
}
