package com.example.map.service.operator;

import com.example.map.cachemanager.report.ReportCache;
import com.example.map.dto.report.ReportViewRedis;
import com.example.map.repository.ReportRepository;
import org.redisson.api.RMapCache;
import org.redisson.api.RSetCache;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OperatorService {
    private final ReportCache reportCache;
    private final ReportRepository reportRepository;
    private RSetCache<ReportViewRedis> reports;
    public OperatorService(ReportCache reportCache, ReportRepository reportRepository) {
        this.reportCache = reportCache;
        this.reportRepository = reportRepository;
    }

    public ResponseEntity<List<ReportViewRedis>> allNotCheckReport(){
        List<ReportViewRedis> reportList = new ArrayList<>();
        reports = reportCache.getReports();
        reports.stream().forEach(report -> {
            if (!report.getCheckStatus()){
                reportList.add(report);
            }
        });
        return ResponseEntity.ok(reportList);
    }
}
