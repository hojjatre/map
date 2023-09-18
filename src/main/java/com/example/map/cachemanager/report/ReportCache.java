package com.example.map.cachemanager.report;

import com.example.map.config.RedisConfig;
import com.example.map.config.ReportTiming;
import com.example.map.dto.report.ReportView;
import com.example.map.dto.report.ReportViewRedis;
import com.example.map.model.EReport;
import com.example.map.model.Report;
import lombok.Getter;
import org.redisson.api.RList;
import org.redisson.api.RMapCache;
import org.redisson.api.RSetCache;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
public class ReportCache {

    RMapCache<String,ReportViewRedis> reportsNotChecked;
    RMapCache<Long,ReportViewRedis> reports;
//    RSetCache<ReportViewRedis> reports;
    private final RedisConfig redisConfig;
    private final String nameCache = "report";
    private final String nameCache2 = "reportNotChecked";

    private ReportTiming reportTiming = new ReportTiming();

    public ReportCache(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public Boolean checkNotScam(ReportViewRedis report){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        reports = redisConfig.redissonClient().getMapCache(nameCache);
        for (Long id:reports.keySet()) {
            String KEY = report.getUsername() + "," + report.getCoordinate() + "," + report.getReportType() + "," + report.getReportData();
            ReportViewRedis reportViewRedis = reports.get(id);
            String existsKEY = reportViewRedis.getUsername() + "," + reportViewRedis.getCoordinate() + "," + reportViewRedis.getReportType() + "," + reportViewRedis.getReportData();
            if (existsKEY.equals(KEY)){
                ReportViewRedis existReport = reports.get(id);
                long time_difference = LocalDateTime.parse(report.getDate(), formatter).getMinute() -
                        LocalDateTime.parse(existReport.getDate(), formatter).getMinute();
//            long minutes_difference = (time_difference / (1000*60)) % 60;
                System.out.println(time_difference);
                if (time_difference <= 5){
                    System.out.println("Not add");
                    return false;
                }
            }
        }
        return true;
    }
    public void addReportToCache(ReportViewRedis report){
        if (report.getCheckStatus()){
            reports = redisConfig.redissonClient().getMapCache(nameCache);
            Long KEY = report.getId();
            // no need for permission
            EReport type = report.getReportType();
            if(type.equals(EReport.ACCIDENT)){
                reports.put(KEY, report, reportTiming.getACCIDENT(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.CAMERA)) {
                reports.put(KEY, report, reportTiming.getCAMERA(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.MAP_BUGS)) {
                reports.put(KEY, report, reportTiming.getMAPBUGS(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.EVENTS_ON_WAY)) {
                reports.put(KEY, report, reportTiming.getEVENTSONWAY(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.POLICE)) {
                reports.put(KEY, report, reportTiming.getPOLICE(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.ROAD_LOCATION)) {
                reports.put(KEY, report, reportTiming.getROADLOCATION(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.SPEED_BUMP)) {
                reports.put(KEY, report, reportTiming.getSPEEDBUMP(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.TRAFFIC)) {
                reports.put(KEY, report, reportTiming.getTRAFFIC(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.WEATHER_CONDITIONS)) {
                reports.put(KEY, report, reportTiming.getWEATHERCONDITIONS(), TimeUnit.MINUTES);
            }
        }
        else {
            // need for permission
            reportsNotChecked = redisConfig.redissonClient().getMapCache(nameCache2);
            String KEY = report.getUsername() + "," + report.getCoordinate() + "," + report.getReportType() + "," + report.getReportData();
            EReport type = report.getReportType();
            if (type.equals(EReport.CAMERA)){
                reportsNotChecked.put(KEY, report, 60, TimeUnit.MINUTES);
            } else if (type.equals(EReport.MAP_BUGS)) {
                reportsNotChecked.put(KEY, report, 60, TimeUnit.MINUTES);
            } else if (type.equals(EReport.ROAD_LOCATION)) {
                reportsNotChecked.put(KEY, report, 60, TimeUnit.MINUTES);
            } else if (type.equals(EReport.SPEED_BUMP)) {
                reportsNotChecked.put(KEY, report, 60, TimeUnit.MINUTES);
            } else if (type.equals(EReport.WEATHER_CONDITIONS)) {
                reportsNotChecked.put(KEY, report, 60, TimeUnit.MINUTES);
            }
        }
    }


    public RMapCache<Long,ReportViewRedis> getReports(){
        reports = redisConfig.redissonClient().getMapCache(nameCache);
        return reports;
    }
    public RMapCache<String,ReportViewRedis> getReportsNotChecked(){
        reportsNotChecked = redisConfig.redissonClient().getMapCache(nameCache2);
        return reportsNotChecked;
    }

}
