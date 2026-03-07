import { render } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import DashboardAnalytics from "../pages/DashboardAnalytics";

test("DashboardAnalytics matches snapshot", () => {
  const { asFragment } = render(
    <MemoryRouter>
      <DashboardAnalytics />
    </MemoryRouter>
  );
  expect(asFragment()).toMatchSnapshot();
});