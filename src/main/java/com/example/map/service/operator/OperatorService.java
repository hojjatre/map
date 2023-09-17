package com.example.map.service.operator;

import com.example.map.cachemanager.report.ReportCache;
import com.example.map.config.ReportTiming;
import com.example.map.dto.operator.CheckedRequest;
import com.example.map.dto.report.ReportMapper;
import com.example.map.dto.report.ReportViewRedis;
import com.example.map.model.EReport;
import com.example.map.model.Report;
import com.example.map.repository.ReportRepository;
import com.example.map.repository.UserRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.redisson.api.RMapCache;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OperatorService {
    private final ReportCache reportCache;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private ReportTiming reportTiming = new ReportTiming();
    private RMapCache<String,ReportViewRedis> reports;
    private ReportMapper reportMapper;
    public OperatorService(ReportCache reportCache, ReportRepository reportRepository, UserRepository userRepository) {
        this.reportCache = reportCache;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<List<ReportViewRedis>> allNotCheckReport(){
        List<ReportViewRedis> reportList = new ArrayList<>();
        reports = reportCache.getReports();
        for (String key:reports.keySet()) {
            if (!reports.get(key).getCheckStatus()) {
                reportList.add(reports.get(key));
            }
        }
        return ResponseEntity.ok(reportList);
    }

    public ResponseEntity<Object> checkedReport(CheckedRequest checkedRequest){
        reports = reportCache.getReports();
        Date currentTime = new Date();
        reportMapper = ReportMapper.instance;
        String KEY = checkedRequest.getUsername() + "," + checkedRequest.getCoordinate() + "," + EReport.CAMERA + "," +
                checkedRequest.getReportData();
        if (reports.containsKey(KEY)){
            ReportViewRedis reportRedis = reports.get(KEY);
            Report report = null;
            if (reportRedis.getReportType().equals(EReport.CAMERA)){
                System.out.println(reportRedis.getUsername());
                report = new Report(reportRedis.getReportType(), reportRedis.getCoordinate(), reportRedis.getReportData(),
                        DateUtils.addMinutes(currentTime, reportTiming.getCAMERA()), true, userRepository.findByUsername(reportRedis.getUsername()));
            } else if (reportRedis.getReportType().equals(EReport.MAP_BUGS)) {
                report = new Report(reportRedis.getReportType(), reportRedis.getCoordinate(), reportRedis.getReportData(),
                        DateUtils.addMinutes(currentTime, reportTiming.getMAPBUGS()), true, userRepository.findByUsername(reportRedis.getUsername()));
            } else if (reportRedis.getReportType().equals(EReport.ROAD_LOCATION)) {
                report = new Report(reportRedis.getReportType(), reportRedis.getCoordinate(), reportRedis.getReportData(),
                        DateUtils.addMinutes(currentTime, reportTiming.getROADLOCATION()), true, userRepository.findByUsername(reportRedis.getUsername()));
            } else if (reportRedis.getReportType().equals(EReport.SPEED_BUMP)) {
                report = new Report(reportRedis.getReportType(), reportRedis.getCoordinate(), reportRedis.getReportData(),
                        DateUtils.addMinutes(currentTime, reportTiming.getSPEEDBUMP()), true, userRepository.findByUsername(reportRedis.getUsername()));
            } else if (reportRedis.getReportType().equals(EReport.WEATHER_CONDITIONS)) {
                report = new Report(reportRedis.getReportType(), reportRedis.getCoordinate(), reportRedis.getReportData(),
                        DateUtils.addMinutes(currentTime, reportTiming.getWEATHERCONDITIONS()), true, userRepository.findByUsername(reportRedis.getUsername()));
            }
            reports.remove(KEY);
            reportRepository.save(report);
            reportCache.addReportToCache(reportMapper.entityToDTO(report));
            return ResponseEntity.ok(reportMapper.entityToDTO(report));
        }
        return ResponseEntity.ok("This report not exist");
    }
}
