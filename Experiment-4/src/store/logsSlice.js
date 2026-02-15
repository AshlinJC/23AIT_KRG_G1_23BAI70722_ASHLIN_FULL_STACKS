import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import initialLogs from '../data/logs';

// Simulate an asynchronous API call
export const fetchLogs = createAsyncThunk('logs/fetchLogs', async () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(initialLogs);
    }, 1500); // 1.5 second delay to show loading state
  });
});

const logsSlice = createSlice({
  name: 'logs',
  initialState: {
    data: [],
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchLogs.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchLogs.fulfilled, (state, action) => {
        state.loading = false;
        state.data = action.payload;
      })
      .addCase(fetchLogs.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });
  },
});

export default logsSlice.reducer;
