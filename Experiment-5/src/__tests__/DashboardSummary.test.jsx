import { render } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import DashboardSummary from "../pages/DashboardSummary";

test("DashboardSummary matches snapshot", () => {
  const { asFragment } = render(
    <MemoryRouter>
      <DashboardSummary />
    </MemoryRouter>
  );
  expect(asFragment()).toMatchSnapshot();
});