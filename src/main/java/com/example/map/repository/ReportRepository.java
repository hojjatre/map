package com.example.map.repository;

import com.example.map.dto.report.ReportView;
import com.example.map.model.Report;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findById(Long id);
    @EntityGraph(value = "graph.report", type = EntityGraph.EntityGraphType.FETCH)
    @Query("select r from Report r where r.id = :id")
    ReportView findID(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("DELETE from Report r where r.date < :currentTime")
    void deleteReportsIfDateIsExpire(@Param("currentTime") LocalDateTime currentTime);

    @Query(nativeQuery = true, value = "SELECT EXTRACT(HOUR FROM r.date) AS hour_of_day, COUNT(*) AS report_count " +
            "FROM report as r " +
            "WHERE r.report_type = 'ACCIDENT' " +
            "GROUP BY hour_of_day " +
            "ORDER BY report_count DESC " +
            "LIMIT 1")
    List<Object[]> findMostFrequentHourOfDayAndCount();
//    @Transactional
//    @Modifying
//    @Query(value = "INSERT INTO Report (report_type, coordinate, report_data, date" +
//            ", check_status, user_id) VALUES (:reportType, ST_GeomFromText(:coordinate,4326), :reportData, :reportDate, :check, :user_id)",
//            nativeQuery = true)
//    void insert(EReport reportType, String coordinate, String reportData, Date reportDate, boolean check, Long user_id);


}
