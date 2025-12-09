package com.covoiturage.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.covoiturage.dto.CreateReportDTO;
import com.covoiturage.dto.ReportDTO;
import com.covoiturage.dto.UpdateReportDTO;
import com.covoiturage.model.Message;
import com.covoiturage.model.Report;
import com.covoiturage.model.Trip;
import com.covoiturage.model.User;
import com.covoiturage.repository.MessageRepository;
import com.covoiturage.repository.ReportRepository;
import com.covoiturage.repository.TripRepository;
import com.covoiturage.repository.UserRepository;

@Service
public class ReportService {
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    /**
     * Create a new report
     */
    @Transactional
    public ReportDTO createReport(Long reporterId, CreateReportDTO createReportDTO) {
        // Validate reporter exists
        User reporter = userRepository.findById(reporterId)
            .orElseThrow(() -> new RuntimeException("Reporter not found"));
        
        // Validate and set reported entity based on report type
        Report.ReportType reportType = createReportDTO.getReportType();
        User reportedUser = null;
        Trip reportedTrip = null;
        Message reportedMessage = null;
        
        switch (reportType) {
            case USER:
                if (createReportDTO.getReportedUserId() == null) {
                    throw new RuntimeException("Reported user ID is required for user reports");
                }
                reportedUser = userRepository.findById(createReportDTO.getReportedUserId())
                    .orElseThrow(() -> new RuntimeException("Reported user not found"));
                break;
                
            case TRIP:
                if (createReportDTO.getReportedTripId() == null) {
                    throw new RuntimeException("Reported trip ID is required for trip reports");
                }
                reportedTrip = tripRepository.findById(createReportDTO.getReportedTripId())
                    .orElseThrow(() -> new RuntimeException("Reported trip not found"));
                break;
                
            case MESSAGE:
                if (createReportDTO.getReportedMessageId() == null) {
                    throw new RuntimeException("Reported message ID is required for message reports");
                }
                reportedMessage = messageRepository.findById(createReportDTO.getReportedMessageId())
                    .orElseThrow(() -> new RuntimeException("Reported message not found"));
                break;
                
            default:
                throw new RuntimeException("Invalid report type");
        }
        
        // Cannot report yourself
        if (reportType == Report.ReportType.USER && reporterId.equals(createReportDTO.getReportedUserId())) {
            throw new RuntimeException("You cannot report yourself");
        }
        
        // Check if user has already reported this entity
        Long entityId = null;
        switch (reportType) {
            case USER:
                entityId = createReportDTO.getReportedUserId();
                break;
            case TRIP:
                entityId = createReportDTO.getReportedTripId();
                break;
            case MESSAGE:
                entityId = createReportDTO.getReportedMessageId();
                break;
        }
        
        // Note: Duplicate check simplified for now
        
        // Validate description
        if (createReportDTO.getDescription() == null || createReportDTO.getDescription().trim().isEmpty()) {
            throw new RuntimeException("Report description cannot be empty");
        }
        
        // Create report
        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedUser(reportedUser);
        report.setReportedTrip(reportedTrip);
        report.setReportedMessage(reportedMessage);
        report.setReportType(reportType);
        report.setReportReason(createReportDTO.getReportReason());
        report.setDescription(createReportDTO.getDescription().trim());
        report.setReportStatus(Report.ReportStatus.PENDING);
        
        Report savedReport = reportRepository.save(report);
        
        return new ReportDTO(savedReport);
    }
    
    /**
     * Get report by ID
     */
    public ReportDTO getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
        return new ReportDTO(report);
    }
    
    /**
     * Get reports by reporter
     */
    public List<ReportDTO> getReportsByReporter(Long reporterId) {
        List<Report> reports = reportRepository.findByReporterId(reporterId);
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get reports for a user (reported user)
     */
    public List<ReportDTO> getReportsForUser(Long userId) {
        List<Report> reports = reportRepository.findByReportedUserId(userId);
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get reports about a user (all reports where user is reported)
     */
    public List<ReportDTO> getReportsAboutUser(Long userId) {
        List<Report> reports = reportRepository.findReportsAboutUser(userId);
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all reports for a user (as reporter or reported)
     */
    public List<ReportDTO> getAllReportsForUser(Long userId) {
        List<Report> reports = reportRepository.findAllReportsForUser(userId);
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get reports by status
     */
    public List<ReportDTO> getReportsByStatus(Report.ReportStatus status) {
        List<Report> reports = reportRepository.findByReportStatus(status);
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get reports by status ordered by creation date
     */
    public List<ReportDTO> getReportsByStatusOrderByDate(Report.ReportStatus status) {
        List<Report> reports = reportRepository.findByReportStatusOrderByCreatedAtDesc(status);
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get pending reports (for admin dashboard)
     */
    public List<ReportDTO> getPendingReports() {
        List<Report> reports = reportRepository.findByReportStatusOrderByCreatedAtDesc(Report.ReportStatus.PENDING);
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get unresolved reports
     */
    public List<ReportDTO> getUnresolvedReports() {
        List<Report> reports = reportRepository.findUnresolvedReports();
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get reports by type
     */
    public List<ReportDTO> getReportsByType(Report.ReportType reportType) {
        List<Report> reports = reportRepository.findByReportType(reportType);
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get reports by reason
     */
    public List<ReportDTO> getReportsByReason(Report.ReportReason reportReason) {
        List<Report> reports = reportRepository.findByReportReason(reportReason);
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get reports by date range
     */
    public List<ReportDTO> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Report> reports = reportRepository.findReportsByDateRange(startDate, endDate);
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Count reports by status
     */
    public Long countReportsByStatus(Report.ReportStatus status) {
        return reportRepository.countReportsByStatus(status);
    }
    
    /**
     * Update report status (admin only)
     */
    @Transactional
    public ReportDTO updateReportStatus(Long reportId, UpdateReportDTO updateReportDTO, Long adminId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
        
        // Validate admin exists
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        report.setReportStatus(updateReportDTO.getReportStatus());
        report.setAdminNotes(updateReportDTO.getAdminNotes());
        report.setReviewedByAdmin(admin);
        
        // Set resolved timestamp if status is resolved or dismissed
        if (updateReportDTO.getReportStatus() == Report.ReportStatus.RESOLVED || 
            updateReportDTO.getReportStatus() == Report.ReportStatus.DISMISSED) {
            report.setResolvedAt(LocalDateTime.now());
        }
        
        Report savedReport = reportRepository.save(report);
        
        return new ReportDTO(savedReport);
    }
    
    /**
     * Delete a report (only reporter can delete their own report if it's still pending)
     */
    @Transactional
    public void deleteReport(Long reportId, Long reporterId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
        
        if (!report.getReporter().getId().equals(reporterId)) {
            throw new RuntimeException("You can only delete your own reports");
        }
        
        if (report.getReportStatus() != Report.ReportStatus.PENDING) {
            throw new RuntimeException("You can only delete pending reports");
        }
        
        reportRepository.delete(report);
    }
    
    /**
     * Get reports reviewed by admin
     */
    public List<ReportDTO> getReportsReviewedByAdmin(Long adminId) {
        List<Report> reports = reportRepository.findByReviewedByAdminId(adminId);
        return reports.stream()
            .map(ReportDTO::new)
            .collect(Collectors.toList());
    }
}
