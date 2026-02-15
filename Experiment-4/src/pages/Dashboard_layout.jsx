import React from "react";
import { Outlet } from "react-router-dom";
import { Container, Grid, Typography } from "@mui/material";

const Dashboard_layout = () => {
  return (
    <Container className="container" maxWidth="md">
      <Typography variant="h4" gutterBottom className="page-title">
        DASHBOARD LAYOUT
      </Typography>

      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Outlet />
        </Grid>
      </Grid>
    </Container>
  );
};

export default React.memo(Dashboard_layout);
