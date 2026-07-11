import { usePermissions } from "react-admin";

import { DiscoveryDashboard } from "../features/discovery/DiscoveryDashboard";
import { AdminDashboard } from "../features/admin/AdminDashboard";

export function RoleBasedDashboard() {
  const { permissions, isLoading } = usePermissions();

  if (isLoading) {
    return null;
  }

  return permissions === "ADMIN" ? <AdminDashboard /> : <DiscoveryDashboard />;
}
