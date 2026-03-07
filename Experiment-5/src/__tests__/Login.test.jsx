import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Login from "../pages/Login";
import { AuthContext } from "../context/AuthContext";

describe("Login Page", () => {
  test("renders login page", () => {
    const mockAuthValue = {
      isAuthenticated: false,
      setIsAuthenticated: jest.fn(),
    };

    render(
      <AuthContext.Provider value={mockAuthValue}>
        <MemoryRouter>
          <Login />
        </MemoryRouter>
      </AuthContext.Provider>
    );

    expect(
      screen.getByRole("heading", { name: /login/i })
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", { name: /login/i })
    ).toBeInTheDocument();
  });
});