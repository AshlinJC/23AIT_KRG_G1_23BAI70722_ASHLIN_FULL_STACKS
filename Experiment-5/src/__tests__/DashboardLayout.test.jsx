import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import DashboardLayout from "../pages/DashboardLayout";

test("renders DashboardLayout", () => {
  const mockAuthValue = {
    isAuthenticated: true,
    setIsAuthenticated: jest.fn()
  };

  render(
    <AuthContext.Provider value={mockAuthValue}>
      <MemoryRouter>
        <DashboardLayout />
      </MemoryRouter>
    </AuthContext.Provider>
  );

  expect(screen.getByText(/dashboard/i)).toBeInTheDocument();
});