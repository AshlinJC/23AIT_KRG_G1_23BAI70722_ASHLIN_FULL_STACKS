
import { render, screen } from "@testing-library/react";

test("loads logs asynchronously", async () => {
  render(<div>Cycling</div>);
  const item = await screen.findByText(/Cycling/i);
  expect(item).toBeInTheDocument();
});
