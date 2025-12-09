package com.covoiturage.dto;

import java.time.LocalDateTime;

import com.covoiturage.model.Report;

public class ReportDTO {
    private Long id;
    private UserDTO reporter;
    private UserDTO reportedUser;
    private TripDTO reportedTrip;
    private MessageDTO reportedMessage;
    private Report.ReportType reportType;
    private Report.ReportReason reportReason;
    private String description;
    private Report.ReportStatus reportStatus;
    private String adminNotes;
    private UserDTO reviewedByAdmin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    
    // Constructors
    public ReportDTO() {}
    
    public ReportDTO(Report report) {
        this.id = report.getId();
        this.reporter = report.getReporter() != null ? new UserDTO(report.getReporter()) : null;
        this.reportedUser = report.getReportedUser() != null ? new UserDTO(report.getReportedUser()) : null;
        this.reportedTrip = report.getReportedTrip() != null ? new TripDTO(report.getReportedTrip()) : null;
        this.reportedMessage = report.getReportedMessage() != null ? new MessageDTO(report.getReportedMessage()) : null;
        this.reportType = report.getReportType();
        this.reportReason = report.getReportReason();
        this.description = report.getDescription();
        this.reportStatus = report.getReportStatus();
        this.adminNotes = report.getAdminNotes();
        this.reviewedByAdmin = report.getReviewedByAdmin() != null ? new UserDTO(report.getReviewedByAdmin()) : null;
        this.createdAt = report.getCreatedAt();
        this.updatedAt = report.getUpdatedAt();
        this.resolvedAt = report.getResolvedAt();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public UserDTO getReporter() { return reporter; }
    public void setReporter(UserDTO reporter) { this.reporter = reporter; }
    
    public UserDTO getReportedUser() { return reportedUser; }
    public void setReportedUser(UserDTO reportedUser) { this.reportedUser = reportedUser; }
    
    public TripDTO getReportedTrip() { return reportedTrip; }
    public void setReportedTrip(TripDTO reportedTrip) { this.reportedTrip = reportedTrip; }
    
    public MessageDTO getReportedMessage() { return reportedMessage; }
    public void setReportedMessage(MessageDTO reportedMessage) { this.reportedMessage = reportedMessage; }
    
    public Report.ReportType getReportType() { return reportType; }
    public void setReportType(Report.ReportType reportType) { this.reportType = reportType; }
    
    public Report.ReportReason getReportReason() { return reportReason; }
    public void setReportReason(Report.ReportReason reportReason) { this.reportReason = reportReason; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Report.ReportStatus getReportStatus() { return reportStatus; }
    public void setReportStatus(Report.ReportStatus reportStatus) { this.reportStatus = reportStatus; }
    
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    
    public UserDTO getReviewedByAdmin() { return reviewedByAdmin; }
    public void setReviewedByAdmin(UserDTO reviewedByAdmin) { this.reviewedByAdmin = reviewedByAdmin; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
