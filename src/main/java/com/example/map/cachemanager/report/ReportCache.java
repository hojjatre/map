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

import java.util.concurrent.TimeUnit;

@Component
public class ReportCache {

    RMapCache<String,ReportViewRedis> reports;
//    RSetCache<ReportViewRedis> reports;
    private final RedisConfig redisConfig;
    private final String nameCache = "report";

    private ReportTiming reportTiming = new ReportTiming();

    public ReportCache(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public Boolean checkNotScam(ReportViewRedis report){
        reports = redisConfig.redissonClient().getMapCache(nameCache);
        String KEY = report.getUsername() + "," + report.getCoordinate() + "," + report.getReportType() + "," + report.getReportData();
        if (reports.containsKey(KEY)){
            ReportViewRedis existReport = reports.get(KEY);
            long time_difference = existReport.getDate().getTime() - report.getDate().getTime();
            long minutes_difference = (time_difference / (1000*60)) % 60;
            System.out.println(minutes_difference);
            if (minutes_difference <= 5){
                System.out.println("Not add");
                return false;
            }
        }
        return true;
    }
    public void addReportToCache(ReportViewRedis report){
        reports = redisConfig.redissonClient().getMapCache(nameCache);
        String KEY = report.getUsername() + "," + report.getCoordinate() + "," + report.getReportType() + "," + report.getReportData();
        if (report.getCheckStatus()){
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
            EReport type = report.getReportType();
            if (type.equals(EReport.CAMERA)){
                reports.put(KEY, report);
            } else if (type.equals(EReport.MAP_BUGS)) {
                reports.put(KEY, report);
            } else if (type.equals(EReport.ROAD_LOCATION)) {
                reports.put(KEY, report);
            } else if (type.equals(EReport.SPEED_BUMP)) {
                reports.put(KEY, report);
            } else if (type.equals(EReport.WEATHER_CONDITIONS)) {
                reports.put(KEY, report);
            }
        }
    }

    public RMapCache<String,ReportViewRedis> getReports(){
        reports = redisConfig.redissonClient().getMapCache(nameCache);
        return reports;
    }

}
