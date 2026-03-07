import reducer, { fetchLogs } from "../logsSlice";

describe("logsSlice reducer", () => {

  const initialState = {
    data: [],
    status: "idle",
    error: null,
  };

  test("should return the initial state", () => {
    expect(reducer(undefined, { type: undefined })).toEqual(initialState);
  });

  test("should handle fetchLogs.pending", () => {
    const action = { type: fetchLogs.pending.type };
    const state = reducer(initialState, action);

    expect(state.status).toBe("loading");
  });

  test("should handle fetchLogs.fulfilled", () => {
    const mockPayload = [
      { id: 1, activity: "Car Travel", carbon: 4 }
    ];

    const action = {
      type: fetchLogs.fulfilled.type,
      payload: mockPayload,
    };

    const state = reducer(initialState, action);

    expect(state.status).toBe("succeeded");
    expect(state.data).toEqual(mockPayload);
  });

  test("should handle fetchLogs.rejected", () => {
    const action = {
      type: fetchLogs.rejected.type,
      error: { message: "Failed" },
    };

    const state = reducer(initialState, action);

    expect(state.status).toBe("failed");
    expect(state.error).toBe("Failed");
  });

});