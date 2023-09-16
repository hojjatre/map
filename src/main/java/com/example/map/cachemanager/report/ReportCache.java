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

//    RMapCache<Long,ReportViewRedis> reports;
    RSetCache<ReportViewRedis> reports;
    private final RedisConfig redisConfig;
    private final String nameCache = "report";

    private ReportTiming reportTiming = new ReportTiming();

    public ReportCache(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public void addReportToCache(ReportViewRedis report){
        reports = redisConfig.redissonClient().getSetCache(nameCache);
        if (report.getCheckStatus()){
            // no need for permission
            EReport type = report.getReportType();
            if(type.equals(EReport.ACCIDENT)){
                reports.add(report, reportTiming.getACCIDENT(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.CAMERA)) {
                reports.add(report, reportTiming.getCAMERA(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.MAP_BUGS)) {
                reports.add(report, reportTiming.getMAPBUGS(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.EVENTS_ON_WAY)) {
                reports.add(report, reportTiming.getEVENTSONWAY(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.POLICE)) {
                reports.add(report, reportTiming.getPOLICE(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.ROAD_LOCATION)) {
                reports.add(report, reportTiming.getROADLOCATION(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.SPEED_BUMP)) {
                reports.add(report, reportTiming.getSPEEDBUMP(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.TRAFFIC)) {
                reports.add(report, reportTiming.getTRAFFIC(), TimeUnit.MINUTES);
            } else if (type.equals(EReport.WEATHER_CONDITIONS)) {
                reports.add(report, reportTiming.getWEATHERCONDITIONS(), TimeUnit.MINUTES);
            }
        }
        else {
            // need for permission
            EReport type = report.getReportType();
            if (type.equals(EReport.CAMERA)){
                reports.add(report);
            } else if (type.equals(EReport.MAP_BUGS)) {
                reports.add(report);
            } else if (type.equals(EReport.ROAD_LOCATION)) {
                reports.add(report);
            } else if (type.equals(EReport.SPEED_BUMP)) {
                reports.add(report);
            } else if (type.equals(EReport.WEATHER_CONDITIONS)) {
                reports.add(report);
            }
        }
    }

    public RSetCache<ReportViewRedis> getReports(){
        reports = redisConfig.redissonClient().getSetCache(nameCache);
        return reports;
    }

}
