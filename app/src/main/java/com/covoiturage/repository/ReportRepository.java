package com.covoiturage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.covoiturage.model.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByReporterId(Long reporterId);
    List<Report> findByReportedUserId(Long reportedUserId);
    List<Report> findByReportedTripId(Long reportedTripId);
    List<Report> findByReportedMessageId(Long reportedMessageId);
    List<Report> findByReportStatus(Report.ReportStatus reportStatus);
    List<Report> findByReportType(Report.ReportType reportType);
    List<Report> findByReportReason(Report.ReportReason reportReason);
    
    @Query("SELECT r FROM Report r WHERE r.reporter.id = :userId OR r.reportedUser.id = :userId ORDER BY r.createdAt DESC")
    List<Report> findAllReportsForUser(@Param("userId") Long userId);
    
    List<Report> findByReportStatusOrderByCreatedAtDesc(Report.ReportStatus reportStatus);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportStatus = :status")
    Long countReportsByStatus(@Param("status") Report.ReportStatus status);
    
    @Query("SELECT r FROM Report r WHERE r.reportedUser.id = :userId ORDER BY r.createdAt DESC")
    List<Report> findReportsAboutUser(@Param("userId") Long userId);
    
    List<Report> findByReviewedByAdminId(Long adminId);
    
    @Query("SELECT r FROM Report r WHERE r.createdAt BETWEEN :startDate AND :endDate ORDER BY r.createdAt DESC")
    List<Report> findReportsByDateRange(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
    
    @Query("SELECT r FROM Report r WHERE r.reportStatus != 'RESOLVED' AND r.reportStatus != 'DISMISSED' ORDER BY r.createdAt ASC")
    List<Report> findUnresolvedReports();
}
