package com.example.map.service.report;

import com.example.map.cachemanager.report.ReportCache;
import com.example.map.config.ReportTiming;
import com.example.map.dto.report.ReportMapper;
import com.example.map.dto.report.ReportRequest;
import com.example.map.dto.report.ReportViewRedis;
import com.example.map.dto.report.RoutingRequest;
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
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.redisson.api.RMapCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportCache reportCache;
    private ReportTiming reportTiming = new ReportTiming();
    private final UserRepository userRepository;
    private ReportMapper reportMapper;
    private RMapCache<Long, ReportViewRedis> reports;
    public ReportService(ReportRepository reportRepository, ReportCache reportCache, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.reportCache = reportCache;
        this.userRepository = userRepository;
    }
    public Point stringToGeometry(String wkt) throws ParseException {
//        GeometryFactory geometryFactory = new GeometryFactory();
//        Coordinate coordinate = new Coordinate(x, y);
//        Point point = geometryFactory.createPoint(coordinate);
//        point.setSRID(4326);
//        return point;
        Geometry point = new WKTReader().read(wkt);
        point.setSRID(4326);
        return (Point) point;
    }

    public ResponseEntity<Object> createReport(Authentication authentication, ReportRequest reportRequest) throws ParseException, JsonProcessingException {
        UserImp userImp = userRepository.findByUsername(authentication.getName());
        LocalDateTime currentTime = LocalDateTime.now();
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
            report = new Report(EReport.ACCIDENT, stringToGeometry(reportRequest.getCoordinate()), jsonData,
                    currentTime.plusMinutes(reportTiming.getACCIDENT()), true, userImp);
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
            report = new Report(EReport.CAMERA, stringToGeometry(reportRequest.getCoordinate()), jsonData,
                    currentTime.plusMinutes(reportTiming.getCAMERA()), false, userImp);
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
            report = new Report(EReport.EVENTS_ON_WAY, stringToGeometry(reportRequest.getCoordinate()), jsonData,
                    currentTime.plusMinutes(reportTiming.getEVENTSONWAY()), true, userImp);
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
            report = new Report(EReport.MAP_BUGS, stringToGeometry(reportRequest.getCoordinate()), jsonData,
                    currentTime.plusMinutes(reportTiming.getMAPBUGS()), false, userImp);
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
            report = new Report(EReport.POLICE, stringToGeometry(reportRequest.getCoordinate()), jsonData,
                    currentTime.plusMinutes(reportTiming.getPOLICE()), true, userImp);
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
            report = new Report(EReport.ROAD_LOCATION, stringToGeometry(reportRequest.getCoordinate()), jsonData,
                    currentTime.plusMinutes(reportTiming.getROADLOCATION()), false, userImp);
            reportCache.addReportToCache(reportMapper.entityToDTO(report));
        } else if (reportRequest.getReportType().equals("speed_bump")) {
            SpeedBump speedBump = new SpeedBump();
            if (reportRequest.getReportData().equals("speedBump")){
                speedBump.setSpeedBump(true);
            }
            String jsonData = objectMapper.writeValueAsString(speedBump);
            report = new Report(EReport.SPEED_BUMP, stringToGeometry(reportRequest.getCoordinate()), jsonData,
                    currentTime.plusMinutes(reportTiming.getSPEEDBUMP()), false, userImp);
            reportCache.addReportToCache(reportMapper.entityToDTO(report));
        } else if (reportRequest.getReportType().equals("traffic")) {
            Traffic traffic = new Traffic();
            if (reportRequest.getReportData().equals("light")){
                traffic.setLight(true);
            } else if (reportRequest.getReportData().equals("semiHeavy")) {
                traffic.setSemiHeavy(true);
            } else if (reportRequest.getReportData().equals("lock")) {
                traffic.setLock(true);
            }
            String jsonData = objectMapper.writeValueAsString(traffic);
            report = new Report(EReport.TRAFFIC, stringToGeometry(reportRequest.getCoordinate()), jsonData,
                    currentTime.plusMinutes(reportTiming.getTRAFFIC()), true, userImp);
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
            report = new Report(EReport.WEATHER_CONDITIONS, stringToGeometry(reportRequest.getCoordinate()), jsonData,
                    currentTime.plusMinutes(reportTiming.getWEATHERCONDITIONS()), false, userImp);
            reportCache.addReportToCache(reportMapper.entityToDTO(report));
        }
        return ResponseEntity.ok(reportMapper.entityToDTO(report));
    }

    public ResponseEntity<Object> likeOrDislikeReport(Long id, String decision) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        Optional<Report> optionalReport = reportRepository.findById(id);
        Report report = optionalReport.get();
        reportMapper = ReportMapper.instance;
        reports = reportCache.getReports();
        if (reports.containsKey(id)){
            ReportViewRedis reportRedis = reports.get(id);
            if (decision.equals("like")){
                report.setDate(LocalDateTime.parse(reportRedis.getDate(), formatter).plusMinutes(5));
                reports.remove(id);
                reportRepository.save(report);
                reports.put(id, reportMapper.entityToDTO(report));
                return new ResponseEntity<>(reportMapper.entityToDTO(report), HttpStatus.OK);
            } else if (decision.equals("dislike")) {
                report.setDate(LocalDateTime.parse(reportRedis.getDate(), formatter).minusMinutes(5));
                reports.remove(id);
                reportRepository.save(report);
                reports.put(id, reportMapper.entityToDTO(report));
                return new ResponseEntity<>(reportMapper.entityToDTO(report), HttpStatus.OK);
            }
        }
        return ResponseEntity.ok("The Report not exist");
    }

    public ResponseEntity<List<ReportViewRedis>> routing(RoutingRequest routingRequest) throws ParseException {
        List<ReportViewRedis> reportsWithinBuffer = new ArrayList<>();
        WKTReader wktReader = new WKTReader();
        LineString userRoute = (LineString) wktReader.read(routingRequest.getCoordinate());
        userRoute.setSRID(3857);
        BufferParameters bufferParameters = new BufferParameters();
        bufferParameters.setSingleSided(false);
        bufferParameters.setEndCapStyle(BufferParameters.CAP_ROUND);
        BufferOp bufferOp = new BufferOp(userRoute, bufferParameters);
        Geometry userRouteGeometry = bufferOp.getResultGeometry(10);
        reports = reportCache.getReports();
        for (Long id :reports.keySet()) {
            ReportViewRedis report = reports.get(id);
            Point reportPoint = (Point) wktReader.read(report.getCoordinate());
//            reportPoint.setSRID(3857);
            System.out.println(id);
            System.out.println(reportPoint);
            System.out.println(reportPoint.intersects(userRouteGeometry));
//            if(reportPoint.intersects(userRouteGeometry)){
            if(userRouteGeometry.intersects(reportPoint)){
                reportsWithinBuffer.add(report);
            }
        }
        return ResponseEntity.ok(reportsWithinBuffer);
    }
}
