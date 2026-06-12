import { Admin, Resource } from "react-admin";

import { resources } from "../resources";
import { AdminDashboard } from "../widgets/dashboard/AdminDashboard";
import { authProvider } from "./providers/authProvider";
import { dataProvider } from "./providers/dataProvider";
import { appTheme } from "./providers/theme";

export function App() {
  return (
    <Admin
      title="Trip Planner Admin"
      dataProvider={dataProvider}
      authProvider={authProvider}
      dashboard={AdminDashboard}
      theme={appTheme}
      requireAuth
    >
      {resources.map((resource) => (
        <Resource key={resource.name} {...resource} />
      ))}
    </Admin>
  );
}
