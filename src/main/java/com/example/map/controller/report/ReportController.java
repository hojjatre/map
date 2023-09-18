package com.example.map.controller.report;

import com.example.map.dto.report.ReportRequest;
import com.example.map.dto.report.ReportViewRedis;
import com.example.map.dto.report.RoutingRequest;
import com.example.map.repository.ReportRepository;
import com.example.map.service.report.ReportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.locationtech.jts.io.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    private final ReportService reportService;
    private final ReportRepository reportRepository;
    public ReportController(ReportService reportService, ReportRepository reportRepository) {
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createReport(@RequestBody ReportRequest reportRequest) throws ParseException, JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return reportService.createReport(authentication, reportRequest);
    }

    @PostMapping("/like-dislike")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> likeOrDislikeReport(@RequestParam("id") Long id, @RequestParam("decision") String decision) throws ParseException {
        return reportService.likeOrDislikeReport(id, decision);
    }

    @PostMapping("/routing")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReportViewRedis>> routing(@RequestBody RoutingRequest routingRequest) throws ParseException {
        return reportService.routing(routingRequest);
    }
}
