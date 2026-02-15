import React, { useEffect, useMemo } from "react";
import { useSelector, useDispatch } from "react-redux";
import { Box, Grid, Typography } from "@mui/material";
import { fetchLogs } from "../store/logsSlice";

const DashboardSummary = () => {
  const dispatch = useDispatch();
  const { data, loading } = useSelector((state) => state.logs);

  const highEmissions = useMemo(() => data.filter((log) => log.carbon >= 4), [data]);
  const lowEmissions = useMemo(() => data.filter((log) => log.carbon < 4), [data]);

  useEffect(() => {
    dispatch(fetchLogs());
  }, [dispatch]);

  if (loading) {
    return (
      <Box>
        <Typography>Loading emissions summary...</Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h5" gutterBottom className="page-title">Dashboard Summary</Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Grid container spacing={1} className="carbon-card card-high">
            <Grid item xs={12}>
              <Typography variant="h6" className="card-title">HIGH CARBON EMISSION</Typography>
            </Grid>
            {highEmissions.map((log) => (
              <Grid item xs={12} key={log.id}>
                <Typography className="card-item text-high">
                  {log.activity} {log.carbon}
                </Typography>
              </Grid>
            ))}
          </Grid>
        </Grid>

        <Grid item xs={12} md={6}>
          <Grid container spacing={1} className="carbon-card card-low">
            <Grid item xs={12}>
              <Typography variant="h6" className="card-title">LOW CARBON EMISSION</Typography>
            </Grid>
            {lowEmissions.map((log) => (
              <Grid item xs={12} key={log.id}>
                <Typography className="card-item text-low">
                  {log.activity} {log.carbon}
                </Typography>
              </Grid>
            ))}
          </Grid>
        </Grid>
      </Grid>
    </Box>
  );
};

export default React.memo(DashboardSummary);
