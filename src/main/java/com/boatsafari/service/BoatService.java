package com.boatsafari.service;
import com.boatsafari.model.Boat;
import com.boatsafari.model.MaintenanceLog;
import java.util.List;
public interface BoatService {
Boat createBoat(Long fleetManagerId, Boat boat);
List<Boat> getAllBoats();
Boat getBoatById(Long id);
Boat updateBoat(Long id, Boat updatedBoat);
void deleteBoat(Long id);
MaintenanceLog addMaintenanceLog(Long boatId, MaintenanceLog log);
List<MaintenanceLog> getMaintenanceLogsForBoat(Long boatId);
List<MaintenanceLog> getAllMaintenanceLogs();
}
