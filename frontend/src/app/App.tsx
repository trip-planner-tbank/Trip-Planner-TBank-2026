import { Admin, Resource } from "react-admin";

import { authProvider } from "./providers/authProvider";
import { dataProvider } from "./providers/dataProvider";
import { appTheme } from "./providers/theme";
import { LoginPage } from "../features/auth/LoginPage";
import { RoleBasedDashboard } from "./RoleBasedDashboard";
import { resources as adminResources } from "../features/admin/resources";
import { placesResource as userPlacesResource } from "../features/userPlaces";
import { hotelsResource as userHotelsResource } from "../features/userHotels";
import { wishlistsResource } from "../features/wishlist";
import { reviewsResource } from "../features/reviews";

export function App() {
  return (
    <Admin
      title="Trip Planner"
      authProvider={authProvider}
      dataProvider={dataProvider}
      theme={appTheme}
      loginPage={LoginPage}
      dashboard={RoleBasedDashboard}
      requireAuth
    >
      {(permissions) => {
        const isAdmin = permissions === "ADMIN";

        if (isAdmin) {
          return adminResources.map((resource) => (
            <Resource key={resource.name} {...resource} />
          ));
        }

        return (
          <>
            <Resource {...userPlacesResource} />
            <Resource {...userHotelsResource} />
            <Resource {...wishlistsResource} />
            <Resource {...reviewsResource} />
          </>
        );
      }}
    </Admin>
  );
}
