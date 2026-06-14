import { Card, CardContent, Typography } from "@mui/material";

export function AdminDashboard() {
  return (
    <Card>
      <CardContent>
        <Typography component="h1" variant="h5" gutterBottom>
          Trip Planner Admin
        </Typography>
        <Typography color="text.secondary">
          Manage cities, offices, hotels, and reviews from one workspace.
        </Typography>
      </CardContent>
    </Card>
  );
}
