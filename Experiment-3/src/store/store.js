import { configureStore } from "@reduxjs/toolkit";
import logsReducer from "./logsSlice";

export const store = configureStore({
  reducer: {
    logs: logsReducer,
  },
});
