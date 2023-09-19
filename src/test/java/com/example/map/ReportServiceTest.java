package com.example.map;

import com.example.map.cachemanager.report.ReportCache;
import com.example.map.config.RedisConfig;
import com.example.map.config.ReportTiming;
import com.example.map.dto.report.ReportRequest;
import com.example.map.dto.report.ReportViewRedis;
import com.example.map.dto.report.RoutingRequest;
import com.example.map.model.*;
import com.example.map.model.reportdata.Accident;
import com.example.map.repository.ReportRepository;
import com.example.map.repository.UserRepository;
import com.example.map.service.report.ReportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private ReportCache reportCache;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ReportService reportService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private RedisConfig redisConfig;

    private ReportTiming reportTiming = new ReportTiming();

    public Point stringToGeometry(String wkt) throws ParseException {
        Geometry point = new WKTReader().read(wkt);
        point.setSRID(4326);
        return (Point) point;
    }

    public String timeToString(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String serializedDateTime = localDateTime.format(formatter);
        return serializedDateTime;
    }

    @Test
    public void createReportTest() throws ParseException, JsonProcessingException {
        //current time
        LocalDateTime currentTime = LocalDateTime.now();
        //create user
        UserImp userImp = new UserImp("HojjatRE", "hojjat@gmail.com", encoder.encode("Hojjat123"));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_USER));
        userImp.setRoles(roles);
        when(userRepository.findByUsername(any())).thenReturn(userImp);

        //security
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //create our report
        ReportRequest reportRequest = new ReportRequest("accident", "POINT(-84.0673828125 36.56260003738545)", "light");
        ObjectMapper objectMapper = new ObjectMapper();
        Accident accident = new Accident();
        accident.setLight(true);
        String jsonData = objectMapper.writeValueAsString(accident);
        Report report = new Report(EReport.ACCIDENT, stringToGeometry(reportRequest.getCoordinate()), jsonData,
                currentTime.plusMinutes(reportTiming.getACCIDENT()), true, userImp);

        //handle null
        when(reportRepository.save(any())).thenReturn(report);
        when(authentication.getName()).thenReturn(userImp.getUsername());
        when(reportCache.checkNotScam(any())).thenReturn(true);

        try {
            ResponseEntity<Object> response = reportService.createReport(authentication, reportRequest);
            assertThat(((ReportViewRedis) response.getBody()).getUsername()).isEqualTo("HojjatRE");
            assertThat(((ReportViewRedis) response.getBody()).getReportType()).isEqualTo("accident");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void like_dislikeTest() throws JsonProcessingException, ParseException {
        UserImp userImp = new UserImp("HojjatRE", "hojjat@gmail.com", encoder.encode("Hojjat123"));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_USER));
        userImp.setRoles(roles);
        ObjectMapper objectMapper = new ObjectMapper();
        Accident accident = new Accident();
        accident.setLight(true);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        String jsonData = objectMapper.writeValueAsString(accident);
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime pastDate = LocalDateTime.parse("2017-01-14T15:32:56.000");
        Report expireReport = new Report(EReport.ACCIDENT, stringToGeometry("POINT(-74.8388671875 39.47012512235818)"), jsonData,
                pastDate.plusMinutes(reportTiming.getACCIDENT()), true, userImp);
        expireReport.setId(1L);
        when(reportRepository.findById(any())).thenReturn(Optional.of(expireReport));
        ResponseEntity<Object> response = reportService.likeOrDislikeReport(1L, "like");
        assertThat(response.getBody()).isEqualTo("This report is expire.");

        Report report = new Report(EReport.ACCIDENT, stringToGeometry("POINT(-74.8388671875 39.47012512235818)"), jsonData,
                currentTime.plusMinutes(reportTiming.getACCIDENT()), true, userImp);
        report.setId(2L);

        ReportViewRedis reportViewRedis = new ReportViewRedis(2L,"accident", "POINT(-74.8388671875 39.47012512235818)",
                "light", timeToString(currentTime.plusMinutes(reportTiming.getACCIDENT())), true, userImp.getUsername(), userImp.getId());

        when(reportRepository.findById(any())).thenReturn(Optional.of(report));
        when(reportCache.getReportById(any())).thenReturn(reportViewRedis);
        when(reportCache.getLock()).thenReturn(redisConfig.redissonClient().getLock("Lock"));

        response = reportService.likeOrDislikeReport(2L, "like");
        assertThat(LocalDateTime.parse(((ReportViewRedis) response.getBody()).getDate())).isAfter(
                LocalDateTime.parse(reportViewRedis.getDate())
        );
    }

    @Test
    public void routingTest() throws ParseException {
        UserImp userImp = new UserImp("HojjatRE", "hojjat@gmail.com", encoder.encode("Hojjat123"));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_USER));
        userImp.setRoles(roles);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LocalDateTime currentTime = LocalDateTime.now();
        ReportViewRedis report1 = new ReportViewRedis(2L,"accident", "POINT(59.53069735318422 36.32097623897812)",
                "light", timeToString(currentTime.plusMinutes(reportTiming.getACCIDENT())), true, userImp.getUsername(), userImp.getId());

        ReportViewRedis report2 = new ReportViewRedis(3L,"accident", "POINT(59.53071277588606 36.320973267483964)",
                "light", timeToString(currentTime.plusMinutes(reportTiming.getACCIDENT())), true, userImp.getUsername(), userImp.getId());

        ReportViewRedis report3 = new ReportViewRedis(4L,"accident", "POINT(59.53067321330308 36.32097542857066)",
                "light", timeToString(currentTime.plusMinutes(reportTiming.getACCIDENT())), true, userImp.getUsername(), userImp.getId());

        ReportViewRedis report4 = new ReportViewRedis(4L,"accident", "POINT(59.53048277646303 36.32101135662781)",
                "light", timeToString(currentTime.plusMinutes(reportTiming.getACCIDENT())), true, userImp.getUsername(), userImp.getId());


        ReportViewRedis report5 = new ReportViewRedis(4L,"accident", "POINT(59.530990049242966 36.320930586009894)",
                "light", timeToString(currentTime.plusMinutes(reportTiming.getACCIDENT())), true, userImp.getUsername(), userImp.getId());

        List<ReportViewRedis> reports = new ArrayList<>();
        reports.add(report1);
        reports.add(report2);
        reports.add(report3);
        reports.add(report4);
        reports.add(report5);

        RoutingRequest routingRequest = new RoutingRequest();
        routingRequest.setCoordinate("LINESTRING(59.53078150749205 36.32116776504455,59.53061386942863 36.32082091072249)");
        when(reportCache.getListReports()).thenReturn(reports);
        ResponseEntity<List<ReportViewRedis>> response = reportService.routing(routingRequest);

        assertThat(response.getBody()).contains(report1);
        assertThat(response.getBody()).contains(report2);
        assertThat(response.getBody()).contains(report3);
        assertThat(response.getBody()).doesNotContain(report4);
        assertThat(response.getBody()).doesNotContain(report5);

    }
}
