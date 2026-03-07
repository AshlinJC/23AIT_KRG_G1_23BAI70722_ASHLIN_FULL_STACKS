import { render } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import DashboardSettings from "../pages/DashboardSettings";

test("DashboardSettings matches snapshot", () => {
  const { asFragment } = render(
    <MemoryRouter>
      <DashboardSettings />
    </MemoryRouter>
  );
  expect(asFragment()).toMatchSnapshot();
});