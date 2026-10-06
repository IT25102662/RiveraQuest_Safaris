package com.boatsafari.report;

import java.util.HashMap;
import java.util.Map;

/**
 * Template Method pattern: abstract class that fixes the skeleton (the order of steps)
 * for producing an administrator report. Subclasses only fill in the steps that differ.
 *
 *   generate()  =  loadData()  ->  buildReport()  ->  postProcess()
 *
 * @param <T> the kind of source data a concrete report works on
 */
public abstract class ReportGenerator<T> {

    /** The template method. It is final so no subclass can change the order of the steps. */
    public final Map<String, Object> generate() {
        T data = loadData();
        Map<String, Object> report = new HashMap<>();
        buildReport(data, report);
        postProcess(report);
        return report;
    }

    /** Step 1 (abstract): fetch the data this report is based on. */
    protected abstract T loadData();

    /** Step 2 (abstract): calculate the figures and put them into the report map. */
    protected abstract void buildReport(T data, Map<String, Object> report);

    /** Step 3 (hook): optional extra step; does nothing unless a subclass overrides it. */
    protected void postProcess(Map<String, Object> report) {
    }
}
