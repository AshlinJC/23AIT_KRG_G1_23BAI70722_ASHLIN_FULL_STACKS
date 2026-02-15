import React, { useEffect, useMemo, useCallback } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Container, Grid, Typography, Button } from "@mui/material";
import { fetchLogs } from "../store/logsSlice";

const Logs = () => {
    const dispatch = useDispatch();
    const { data, loading, error } = useSelector((state) => state.logs);

    const handleRefresh = useCallback(() => {
        dispatch(fetchLogs());
    }, [dispatch]);

    const totalCarbon = useMemo(() => {
        return data.reduce((acc, log) => acc + log.carbon, 0);
    }, [data]);

    useEffect(() => {
        dispatch(fetchLogs());
    }, [dispatch]);

    if (loading) {
        return (
            <Container className="container">
                <Typography>Fetching Eco Logs...</Typography>
            </Container>
        );
    }

    if (error) {
        return (
            <Container className="container" sx={{ textAlign: "center", marginTop: "50px" }}>
                <Typography variant="h6" sx={{ color: "#ff4d4d" }}>Error</Typography>
                <Typography>{error}</Typography>
                <Button variant="contained" onClick={handleRefresh}>Retry</Button>
            </Container>
        );
    }

    return (
        <Container className="container">
            <Grid container justifyContent="space-between" alignItems="center" sx={{ marginBottom: 2 }}>
                <Grid item>
                    <Typography variant="h5" className="page-title">Environmental Logs</Typography>
                </Grid>
                <Grid item>
                    <Button onClick={handleRefresh} sx={{ minWidth: "auto", fontSize: "1.5rem" }}>🔄</Button>
                </Grid>
            </Grid>

            <Grid container spacing={1}>
                {data.length > 0 ? (
                    <Grid item xs={12}>
                        <ul style={{ listStyle: "none", padding: 0 }}>
                            {data.map((log) => (
                                <li key={log.id} style={{
                                    padding: "15px",
                                    borderBottom: "1px solid #eee",
                                    display: "flex",
                                    justifyContent: "space-between"
                                }}>
                                    <span>{log.activity}</span>
                                    <span style={{ fontWeight: "bold" }}>{log.carbon} kg CO₂</span>
                                </li>
                            ))}
                        </ul>
                        <Typography variant="h6" sx={{ marginTop: 2 }}>Total Carbon: {totalCarbon} kg CO₂</Typography>
                    </Grid>
                ) : (
                    <Grid item xs={12}>
                        <Typography>No logs found.</Typography>
                    </Grid>
                )}
            </Grid>
        </Container>
    );
};

export default React.memo(Logs);