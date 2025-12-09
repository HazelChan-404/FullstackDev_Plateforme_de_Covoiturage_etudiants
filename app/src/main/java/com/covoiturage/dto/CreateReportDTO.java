package com.covoiturage.dto;

import com.covoiturage.model.Report;

public class CreateReportDTO {
    private Long reportedUserId;
    private Long reportedTripId;
    private Long reportedMessageId;
    private Report.ReportType reportType;
    private Report.ReportReason reportReason;
    private String description;
    
    public CreateReportDTO() {}
    
    public CreateReportDTO(Report.ReportType reportType, Report.ReportReason reportReason, String description) {
        this.reportType = reportType;
        this.reportReason = reportReason;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getReportedUserId() { return reportedUserId; }
    public void setReportedUserId(Long reportedUserId) { this.reportedUserId = reportedUserId; }
    
    public Long getReportedTripId() { return reportedTripId; }
    public void setReportedTripId(Long reportedTripId) { this.reportedTripId = reportedTripId; }
    
    public Long getReportedMessageId() { return reportedMessageId; }
    public void setReportedMessageId(Long reportedMessageId) { this.reportedMessageId = reportedMessageId; }
    
    public Report.ReportType getReportType() { return reportType; }
    public void setReportType(Report.ReportType reportType) { this.reportType = reportType; }
    
    public Report.ReportReason getReportReason() { return reportReason; }
    public void setReportReason(Report.ReportReason reportReason) { this.reportReason = reportReason; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
