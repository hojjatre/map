package com.example.map;

import com.example.map.cachemanager.report.ReportCache;
import com.example.map.config.ReportTiming;
import com.example.map.dto.operator.CheckedRequest;
import com.example.map.dto.report.ReportViewRedis;
import com.example.map.model.ERole;
import com.example.map.model.Report;
import com.example.map.model.Role;
import com.example.map.model.UserImp;
import com.example.map.repository.ReportRepository;
import com.example.map.repository.UserRepository;
import com.example.map.service.operator.OperatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.io.ParseException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OperatorServiceTest {
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReportCache reportCache;
    @InjectMocks
    private OperatorService operatorService;
    @Autowired
    private PasswordEncoder encoder;
    private ReportTiming reportTiming = new ReportTiming();

    public String timeToString(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String serializedDateTime = localDateTime.format(formatter);
        return serializedDateTime;
    }

    @Test
    public void allNotCheckedTest(){
        UserImp userImp = new UserImp("HojjatRE", "hojjat@gmail.com", encoder.encode("Hojjat123"));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_USER));
        userImp.setRoles(roles);


        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LocalDateTime currentTime = LocalDateTime.now();

        ReportViewRedis report1 = new ReportViewRedis(1L,"weather_conditions",
                "POINT(59.53069735318422 36.32097623897812)",
                "slipRoad", timeToString(currentTime.plusMinutes(reportTiming.getWEATHERCONDITIONS())),
                false, userImp.getUsername(), userImp.getId());


        ReportViewRedis report2 = new ReportViewRedis(2L,"weather_conditions",
                "POINT(59.53069735318422 36.32097623897812)",
                "slipRoad", timeToString(currentTime.plusMinutes(reportTiming.getWEATHERCONDITIONS())),
                false, userImp.getUsername(), userImp.getId());

        ReportViewRedis report3 = new ReportViewRedis(3L,"accident", "POINT(59.53071277588606 36.320973267483964)",
                "light", timeToString(currentTime.plusMinutes(reportTiming.getACCIDENT())), true, userImp.getUsername(), userImp.getId());

        List<ReportViewRedis> list = new ArrayList<>();
        list.add(report1);
        list.add(report2);
        list.add(report3);

        when(reportCache.getListReportsNotChecked()).thenReturn(list);
        ResponseEntity<List<ReportViewRedis>> response = operatorService.allNotCheckReport();
        assertThat(response.getBody()).contains(report1);
        assertThat(response.getBody()).contains(report2);
        assertThat(response.getBody()).doesNotContain(report3);
    }

    @Test
    public void checkedReportTest() throws ParseException {
        UserImp userImp = new UserImp("HojjatRE", "hojjat@gmail.com", encoder.encode("Hojjat123"));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_USER));
        userImp.setRoles(roles);


        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LocalDateTime currentTime = LocalDateTime.now();

        ReportViewRedis report1 = new ReportViewRedis(1L,"weather_conditions",
                "POINT (59.53069735318422 36.32097623897812)",
                "slipRoad", timeToString(currentTime.plusMinutes(reportTiming.getWEATHERCONDITIONS())),
                false, userImp.getUsername(), userImp.getId());


        ReportViewRedis report2 = new ReportViewRedis(2L,"weather_conditions",
                "POINT (59.53069735318422 36.32097623897812)",
                "slipRoad", timeToString(currentTime.plusMinutes(reportTiming.getWEATHERCONDITIONS())),
                false, userImp.getUsername(), userImp.getId());

        ReportViewRedis report3 = new ReportViewRedis(3L,"accident", "POINT (59.53071277588606 36.320973267483964)",
                "light", timeToString(currentTime.plusMinutes(reportTiming.getACCIDENT())), true, userImp.getUsername(), userImp.getId());

        List<ReportViewRedis> list = new ArrayList<>();
        list.add(report1);
        list.add(report2);
        list.add(report3);

        CheckedRequest checkedRequest = new CheckedRequest();
        checkedRequest.setUsername("HojjatRE");
        checkedRequest.setCoordinate("POINT(59.53069735318422 36.32097623897812)");
        checkedRequest.setReportType("weather_conditions");
        checkedRequest.setReportData(report2.getReportData());
        when(reportCache.getReportNotCheckByKEY(any())).thenReturn(report2);
        when(reportRepository.save(any())).thenReturn(new Report());
        when(userRepository.findByUsername(any())).thenReturn(userImp);
        ResponseEntity<Object> response = operatorService.checkedReport(checkedRequest);
        assertThat(((ReportViewRedis) response.getBody()).getCheckStatus()).isEqualTo(true);
        assertThat(((ReportViewRedis) response.getBody()).getCoordinate()).isEqualTo(report2.getCoordinate());
    }
}
