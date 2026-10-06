package com.boatsafari.service.impl;

import com.boatsafari.report.BoatUtilizationReportGenerator;
import com.boatsafari.report.RevenueReportGenerator;
import com.boatsafari.report.SystemSummaryReportGenerator;
import com.boatsafari.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * The service no longer calculates anything itself. Each report is produced by a
 * ReportGenerator (Template Method pattern, see the com.boatsafari.report package).
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private SystemSummaryReportGenerator summaryReportGenerator;

    @Autowired
    private RevenueReportGenerator revenueReportGenerator;

    @Autowired
    private BoatUtilizationReportGenerator boatUtilizationReportGenerator;

    @Override
    public Map<String, Object> getSystemSummaryReport() {
        return summaryReportGenerator.generate();
    }

    @Override
    public Map<String, Object> getRevenueReport() {
        return revenueReportGenerator.generate();
    }

    @Override
    public Map<String, Object> getBoatUtilizationReport() {
        return boatUtilizationReportGenerator.generate();
    }
}
