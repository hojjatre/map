package com.example.map.dto.report;


import com.example.map.model.EReport;
import org.springframework.beans.factory.annotation.Value;

public interface ReportView {
    @Value("#{target.id}")
    Long getReportId();
    @Value("#{target.reportType}")
    EReport getReportType();
    @Value("#{target.coordinate}")
    String getCoordinate();
    @Value("#{target.reportData}")
    String getReportData();
    @Value("#{target.checkStatus}")
    boolean getCheckStatus();
    @Value("#{target.date}")
    String getDate();
    @Value("#{target.userImp.username}")
    String getUsername();

}
