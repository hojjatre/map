package com.example.map.service.report;

import com.example.map.cachemanager.report.ReportCache;
import com.example.map.config.ReportTiming;
import com.example.map.dto.report.ReportMapper;
import com.example.map.dto.report.ReportRequest;
import com.example.map.model.EReport;
import com.example.map.model.Report;
import com.example.map.model.UserImp;
import com.example.map.model.reportdata.*;
import com.example.map.repository.ReportRepository;
import com.example.map.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.redisson.api.RMapCache;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportCache reportCache;
    private ReportTiming reportTiming = new ReportTiming();
    private final UserRepository userRepository;
    private ReportMapper reportMapper;
    public ReportService(ReportRepository reportRepository, ReportCache reportCache, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.reportCache = reportCache;
        this.userRepository = userRepository;
    }
    public Geometry stringToGeometry(String wkt) throws ParseException {
//        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return new WKTReader().read(wkt);
    }

    public ResponseEntity<Object> createReport(Authentication authentication, ReportRequest reportRequest) throws ParseException, JsonProcessingException {
        UserImp userImp = userRepository.findByUsername(authentication.getName());
        Date currentTime = new Date();
        reportMapper = ReportMapper.instance;
        ObjectMapper objectMapper = new ObjectMapper();
        Report report = null;
        if (reportRequest.getReportType().equals("accident")){
            Accident accident = new Accident();
            if (reportRequest.getReportData().equals("light")) {
                accident.setLight(true);
            } else if (reportRequest.getReportData().equals("heavy")) {
                accident.setHeavy(true);
            } else if (reportRequest.getReportData().equals("oppositeLine")) {
                accident.setOppositeLine(true);
            }
            String jsonData = objectMapper.writeValueAsString(accident);
            report = new Report(EReport.ACCIDENT, reportRequest.getCoordinate(), jsonData,
                    DateUtils.addMinutes(currentTime, reportTiming.getACCIDENT()), true, userImp);
            if (reportCache.checkNotScam(reportMapper.entityToDTO(report))){
                reportRepository.save(report);
                reportCache.addReportToCache(reportMapper.entityToDTO(report));
            } else {
                return ResponseEntity.ok("It's SCAM.");
            }
        } else if (reportRequest.getReportType().equals("camera")) {
            Camera camera = new Camera();
            if(reportRequest.getReportData().equals("speedControl")){
                camera.setSpeedControl(true);
            } else if (reportRequest.getReportData().equals("redLight")) {
                camera.setRedLight(true);
            }
            String jsonData = objectMapper.writeValueAsString(camera);
            report = new Report(EReport.CAMERA, reportRequest.getCoordinate(), jsonData,
                    DateUtils.addMinutes(currentTime, reportTiming.getCAMERA()), false, userImp);
            reportCache.addReportToCache(reportMapper.entityToDTO(report));
        } else if (reportRequest.getReportType().equals("events_on_way")) {
            EventsOnWay eventsOnWay = new EventsOnWay();
            if (reportRequest.getReportData().equals("constructionOperations")){
                eventsOnWay.setConstructionOperations(true);
            } else if (reportRequest.getReportData().equals("hole")) {
                eventsOnWay.setHole(true);
            } else if (reportRequest.getReportData().equals("roadBlock")) {
                eventsOnWay.setRoadBlock(true);
            }
            String jsonData = objectMapper.writeValueAsString(eventsOnWay);
            report = new Report(EReport.EVENTS_ON_WAY, reportRequest.getCoordinate(), jsonData,
                    DateUtils.addMinutes(currentTime, reportTiming.getEVENTSONWAY()), true, userImp);
            if (reportCache.checkNotScam(reportMapper.entityToDTO(report))){
                reportRepository.save(report);
                reportCache.addReportToCache(reportMapper.entityToDTO(report));
            }else {
                return ResponseEntity.ok("It's SCAM.");
            }
        } else if (reportRequest.getReportType().equals("map_bugs")) {
            MapBugs mapBugs = new MapBugs();
            if (reportRequest.getReportData().equals("noEntry")){
                mapBugs.setNoEntry(true);
            } else if (reportRequest.getReportData().equals("deadend")) {
                mapBugs.setDeadend(true);
            } else if (reportRequest.getReportData().equals("flowDirection")) {
                mapBugs.setFlowDirection(true);
            } else if (reportRequest.getReportData().equals("dun")) {
                mapBugs.setDun(true);
            } else if (reportRequest.getReportData().equals("noCarPath")) {
                mapBugs.setDun(true);
            } else if (reportRequest.getReportData().equals("other")) {
                mapBugs.setOther(true);
            }
            String jsonData = objectMapper.writeValueAsString(mapBugs);
            report = new Report(EReport.MAP_BUGS, reportRequest.getCoordinate(), jsonData,
                    DateUtils.addMinutes(currentTime, reportTiming.getMAPBUGS()), false, userImp);
            reportCache.addReportToCache(reportMapper.entityToDTO(report));
        } else if (reportRequest.getReportType().equals("police")) {
            Police police = new Police();
            if (reportRequest.getReportData().equals("cop")){
                police.setCop(true);
            } else if (reportRequest.getReportData().equals("undercoverCop")) {
                police.setCop(true);
            } else if (reportRequest.getReportData().equals("oppositeLine")) {
                police.setOppositeLine(true);
            }
            String jsonData = objectMapper.writeValueAsString(police);
            report = new Report(EReport.POLICE, reportRequest.getCoordinate(), jsonData,
                    DateUtils.addMinutes(currentTime, reportTiming.getPOLICE()), true, userImp);
            if (reportCache.checkNotScam(reportMapper.entityToDTO(report))){
                reportRepository.save(report);
                reportCache.addReportToCache(reportMapper.entityToDTO(report));
            }else {
                return ResponseEntity.ok("It's SCAM.");
            }
        } else if (reportRequest.getReportType().equals("road_location")) {
            RoadLocation roadLocation = new RoadLocation();
            if (reportRequest.getReportData().equals("gasStation")){
                roadLocation.setGasStation(true);
            } else if (reportRequest.getReportData().equals("fillingStation")) {
                roadLocation.setFillingStation(true);
            } else if (reportRequest.getReportData().equals("redCrescent")) {
                roadLocation.setRedCrescent(true);
            } else if (reportRequest.getReportData().equals("parking")) {
                roadLocation.setParking(true);
            } else if (reportRequest.getReportData().equals("welfareServices")) {
                roadLocation.setWelfareServices(true);
            } else if (reportRequest.getReportData().equals("police")) {
                roadLocation.setPolice(true);
            }
            String jsonData = objectMapper.writeValueAsString(roadLocation);
            report = new Report(EReport.ROAD_LOCATION, reportRequest.getCoordinate(), jsonData,
                    DateUtils.addMinutes(currentTime, reportTiming.getROADLOCATION()), false, userImp);
            reportCache.addReportToCache(reportMapper.entityToDTO(report));
        } else if (reportRequest.getReportType().equals("speed_bump")) {
            SpeedBump speedBump = new SpeedBump();
            if (reportRequest.getReportData().equals("speedBump")){
                speedBump.setSpeedBump(true);
            }
            String jsonData = objectMapper.writeValueAsString(speedBump);
            report = new Report(EReport.SPEED_BUMP, reportRequest.getCoordinate(), jsonData,
                    DateUtils.addMinutes(currentTime, reportTiming.getSPEEDBUMP()), false, userImp);
            reportCache.addReportToCache(reportMapper.entityToDTO(report));
        } else if (reportRequest.getReportType().equals("traffid")) {
            Traffic traffic = new Traffic();
            if (reportRequest.getReportData().equals("light")){
                traffic.setLight(true);
            } else if (reportRequest.getReportData().equals("semiHeavy")) {
                traffic.setSemiHeavy(true);
            } else if (reportRequest.getReportData().equals("lock")) {
                traffic.setLock(true);
            }
            String jsonData = objectMapper.writeValueAsString(traffic);
            report = new Report(EReport.TRAFFIC, reportRequest.getCoordinate(), jsonData,
                    DateUtils.addMinutes(currentTime, reportTiming.getTRAFFIC()), true, userImp);
            if (reportCache.checkNotScam(reportMapper.entityToDTO(report))){
                reportRepository.save(report);
                reportCache.addReportToCache(reportMapper.entityToDTO(report));
            }else {
                return ResponseEntity.ok("It's SCAM.");
            }
        } else if (reportRequest.getReportType().equals("weather_conditions")) {
            WeatherConditions weatherConditions = new WeatherConditions();
            if (reportRequest.getReportData().equals("slipRoad")){
                weatherConditions.setSlipRoad(true);
            } else if (reportRequest.getReportData().equals("fog")) {
                weatherConditions.setFog(true);
            } else if (reportRequest.getReportData().equals("chains")) {
                weatherConditions.setChains(true);
            }
            String jsonData = objectMapper.writeValueAsString(weatherConditions);
            report = new Report(EReport.WEATHER_CONDITIONS, reportRequest.getCoordinate(), jsonData,
                    DateUtils.addMinutes(currentTime, reportTiming.getWEATHERCONDITIONS()), false, userImp);
            reportCache.addReportToCache(reportMapper.entityToDTO(report));
        }
        return ResponseEntity.ok(reportMapper.entityToDTO(report));
    }
}
