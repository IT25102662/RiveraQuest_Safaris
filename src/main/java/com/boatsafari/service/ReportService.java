package com.boatsafari.service;

import java.util.Map;

public interface ReportService {
    Map<String, Object> getSystemSummaryReport();
    Map<String, Object> getRevenueReport();
    Map<String, Object> getBoatUtilizationReport();
}
