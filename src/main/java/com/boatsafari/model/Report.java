package com.boatsafari.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Management report compiled by an Administrator. */
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType;

    @Column(name = "generated_date", nullable = false)
    private LocalDate generatedDate = LocalDate.now();

    @Column(name = "summary_data", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String summaryData;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compiled_by", nullable = false)
    private Administrator compiledBy;

    public Report() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate generatedDate) { this.generatedDate = generatedDate; }
    public String getSummaryData() { return summaryData; }
    public void setSummaryData(String summaryData) { this.summaryData = summaryData; }
    public Administrator getCompiledBy() { return compiledBy; }
    public void setCompiledBy(Administrator compiledBy) { this.compiledBy = compiledBy; }
}
