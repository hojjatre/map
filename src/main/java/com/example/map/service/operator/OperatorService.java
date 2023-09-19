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
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.redisson.api.RMapCache;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public Point stringToGeometry(String wkt) throws ParseException {
        return (Point) new WKTReader().read(wkt);
    }

    public ResponseEntity<List<ReportViewRedis>> allNotCheckReport(){
        List<ReportViewRedis> reportList = new ArrayList<>();
        reports = reportCache.getReportsNotChecked();
        for (String key:reports.keySet()) {
            if (!reports.get(key).getCheckStatus()) {
                reportList.add(reports.get(key));
            }
        }
        return ResponseEntity.ok(reportList);
    }

    public ResponseEntity<Object> checkedReport(CheckedRequest checkedRequest) throws ParseException {
        reports = reportCache.getReportsNotChecked();
//        Date currentTime = new Date();
        LocalDateTime currentTime = LocalDateTime.now();
        reportMapper = ReportMapper.instance;
        String KEY = "";
        if (checkedRequest.getReportType().equals("camera")){
            KEY = checkedRequest.getUsername() + "," + checkedRequest.getCoordinate() + "," + EReport.CAMERA + "," +
                    checkedRequest.getReportData();
        } else if (checkedRequest.getReportType().equals("road_location")) {
            KEY = checkedRequest.getUsername() + "," + checkedRequest.getCoordinate() + "," + EReport.ROAD_LOCATION + "," +
                    checkedRequest.getReportData();
        } else if (checkedRequest.getReportType().equals("map_bugs")) {
            KEY = checkedRequest.getUsername() + "," + checkedRequest.getCoordinate() + "," + EReport.MAP_BUGS + "," +
                    checkedRequest.getReportData();
        } else if (checkedRequest.getReportType().equals("speed_bump")) {
            KEY = checkedRequest.getUsername() + "," + checkedRequest.getCoordinate() + "," + EReport.SPEED_BUMP + "," +
                    checkedRequest.getReportData();
        } else if (checkedRequest.getReportType().equals("weather_conditions")) {
            KEY = checkedRequest.getUsername() + "," + checkedRequest.getCoordinate() + "," + EReport.WEATHER_CONDITIONS + "," +
                    checkedRequest.getReportData();
        }
        if (reports.containsKey(KEY)){
            ReportViewRedis reportRedis = reports.get(KEY);
            Report report = null;
            if (reportRedis.getReportType().equals("camera")){
                System.out.println(reportRedis.getUsername());
                report = new Report(EReport.CAMERA, stringToGeometry(reportRedis.getCoordinate()), reportRedis.getReportData(),
                        currentTime.plusMinutes(reportTiming.getCAMERA()), true, userRepository.findByUsername(reportRedis.getUsername()));
            } else if (reportRedis.getReportType().equals("map_bugs")) {
                report = new Report(EReport.MAP_BUGS, stringToGeometry(reportRedis.getCoordinate()), reportRedis.getReportData(),
                        currentTime.plusMinutes(reportTiming.getMAPBUGS()), true, userRepository.findByUsername(reportRedis.getUsername()));
            } else if (reportRedis.getReportType().equals("road_location")) {
                report = new Report(EReport.ROAD_LOCATION, stringToGeometry(reportRedis.getCoordinate()), reportRedis.getReportData(),
                        currentTime.plusMinutes(reportTiming.getROADLOCATION()), true, userRepository.findByUsername(reportRedis.getUsername()));
            } else if (reportRedis.getReportType().equals("speed_bump")) {
                report = new Report(EReport.SPEED_BUMP, stringToGeometry(reportRedis.getCoordinate()), reportRedis.getReportData(),
                        currentTime.plusMinutes(reportTiming.getSPEEDBUMP()), true, userRepository.findByUsername(reportRedis.getUsername()));
            } else if (reportRedis.getReportType().equals("weather_conditions")) {
                report = new Report(EReport.WEATHER_CONDITIONS, stringToGeometry(reportRedis.getCoordinate()), reportRedis.getReportData(),
                        currentTime.plusMinutes(reportTiming.getWEATHERCONDITIONS()), true, userRepository.findByUsername(reportRedis.getUsername()));
            }
            reports.remove(KEY);
            reportRepository.save(report);
            reportCache.addReportToCache(reportMapper.entityToDTO(report));
            return ResponseEntity.ok(reportMapper.entityToDTO(report));
        }
        return ResponseEntity.ok("This report not exist");
    }

    public ResponseEntity<Object> removeReportIfDateIsExpire(){
        LocalDateTime currentTime = LocalDateTime.now();
        reportRepository.deleteReportsIfDateIsExpire(currentTime);
        return ResponseEntity.ok("Done");
    }

    public ResponseEntity<List<Object[]>> findMostFrequentHourOfDayAndCount(){
        List<Object[]> result = reportRepository.findMostFrequentHourOfDayAndCount();
//        List<String> resultString = new ArrayList<>();
//        for (Object[] row : result) {
//            Integer hourOfDay = (Integer) row[0];
//            Long reportCount = (Long) row[1];
//            resultString.add("Hour of the day: " + hourOfDay + ", Report Count: " + reportCount);
//        }
        return ResponseEntity.ok(result);
    }
}
