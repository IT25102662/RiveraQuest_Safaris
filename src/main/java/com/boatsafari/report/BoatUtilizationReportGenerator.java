package com.boatsafari.report;

import com.boatsafari.repository.BoatRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/** Concrete report: how many boats are available, in maintenance or in use. */
@Component
public class BoatUtilizationReportGenerator extends ReportGenerator<Map<String, Integer>> {

    private final BoatRepository boatRepository;

    public BoatUtilizationReportGenerator(BoatRepository boatRepository) {
        this.boatRepository = boatRepository;
    }

    /** Loads the number of boats for each status. */
    @Override
    protected Map<String, Integer> loadData() {
        Map<String, Integer> counts = new HashMap<>();
        for (String status : new String[]{"AVAILABLE", "MAINTENANCE", "IN_USE"}) {
            counts.put(status, boatRepository.findByStatus(status).size());
        }
        return counts;
    }

    @Override
    protected void buildReport(Map<String, Integer> counts, Map<String, Object> report) {
        report.put("availableBoatsCount", counts.get("AVAILABLE"));
        report.put("maintenanceBoatsCount", counts.get("MAINTENANCE"));
        report.put("inUseBoatsCount", counts.get("IN_USE"));
    }
}
