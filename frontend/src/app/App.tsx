import { Admin, Resource } from "react-admin";

import { authProvider } from "./providers/authProvider";
import { dataProvider } from "./providers/dataProvider";
import { appTheme } from "./providers/theme";
import { LoginPage } from "../features/auth/LoginPage";
import { DiscoveryDashboard } from "../features/discovery/DiscoveryDashboard";
import { placesResource } from "../features/places";
import { hotelsResource } from "../features/hotels";
import { bookingsResource } from "../features/bookings";
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
      dashboard={DiscoveryDashboard}
      requireAuth
    >
      <Resource {...placesResource} />
      <Resource {...hotelsResource} />
      <Resource {...bookingsResource} />
      <Resource {...wishlistsResource} />
      <Resource {...reviewsResource} />
    </Admin>
  );
}
