package com.covoiturage.dto;

import com.covoiturage.model.Report;

public class UpdateReportDTO {
    private Report.ReportStatus reportStatus;
    private String adminNotes;
    
    public UpdateReportDTO() {}
    
    // Getters and Setters
    public Report.ReportStatus getReportStatus() { return reportStatus; }
    public void setReportStatus(Report.ReportStatus reportStatus) { this.reportStatus = reportStatus; }
    
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}
