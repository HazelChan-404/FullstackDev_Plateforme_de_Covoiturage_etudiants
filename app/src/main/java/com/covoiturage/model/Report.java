package com.covoiturage.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_trip_id")
    private Trip reportedTrip;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_message_id")
    private Message reportedMessage;
    
    @Column(name = "report_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;
    
    @Column(name = "report_reason", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "report_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus = ReportStatus.PENDING;
    
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_admin_id")
    private User reviewedByAdmin;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    public enum ReportType {
        USER,
        TRIP,
        MESSAGE
    }
    
    public enum ReportReason {
        FRAUD,
        HARASSMENT,
        SPAM,
        INAPPROPRIATE_CONTENT,
        NO_SHOW,
        CANCELLATION_ABUSE,
        SAFETY_CONCERN,
        OTHER
    }
    
    public enum ReportStatus {
        PENDING,
        UNDER_REVIEW,
        RESOLVED,
        DISMISSED
    }
    
    // Constructors
    public Report() {}
    
    public Report(User reporter, ReportType reportType, ReportReason reportReason, String description) {
        this.reporter = reporter;
        this.reportType = reportType;
        this.reportReason = reportReason;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getReporter() { return reporter; }
    public void setReporter(User reporter) { this.reporter = reporter; }
    
    public User getReportedUser() { return reportedUser; }
    public void setReportedUser(User reportedUser) { this.reportedUser = reportedUser; }
    
    public Trip getReportedTrip() { return reportedTrip; }
    public void setReportedTrip(Trip reportedTrip) { this.reportedTrip = reportedTrip; }
    
    public Message getReportedMessage() { return reportedMessage; }
    public void setReportedMessage(Message reportedMessage) { this.reportedMessage = reportedMessage; }
    
    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }
    
    public ReportReason getReportReason() { return reportReason; }
    public void setReportReason(ReportReason reportReason) { this.reportReason = reportReason; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ReportStatus getReportStatus() { return reportStatus; }
    public void setReportStatus(ReportStatus reportStatus) { this.reportStatus = reportStatus; }
    
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    
    public User getReviewedByAdmin() { return reviewedByAdmin; }
    public void setReviewedByAdmin(User reviewedByAdmin) { this.reviewedByAdmin = reviewedByAdmin; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
