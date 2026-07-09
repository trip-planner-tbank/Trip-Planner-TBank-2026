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
      {(permissions) =>
        resources.map((resource) => {
          const isAdmin = permissions === "ADMIN";
          const adminManaged = ["cities", "offices", "hotels"].includes(resource.name);
          return (
            <Resource
              key={resource.name}
              {...resource}
              create={adminManaged && !isAdmin ? undefined : resource.create}
              edit={adminManaged && !isAdmin ? undefined : resource.edit}
            />
          );
        })
      }
    </Admin>
  );
}
