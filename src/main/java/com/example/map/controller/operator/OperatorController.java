package com.example.map.controller.operator;

import com.example.map.dto.operator.CheckedRequest;
import com.example.map.dto.report.ReportViewRedis;
import com.example.map.service.operator.OperatorService;
import org.locationtech.jts.io.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operator")
public class OperatorController {
    private final OperatorService operatorService;

    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @GetMapping("/not-checked")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportViewRedis>> allNotCheckReport(){
        return operatorService.allNotCheckReport();
    }

    @PostMapping("/checked-report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> checkedReport(@RequestBody CheckedRequest checkedRequest) throws ParseException {
        return operatorService.checkedReport(checkedRequest);
    }

    @GetMapping("/remove-reports-expire")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> removeReportsIfDateIsExpire(){
        return operatorService.removeReportIfDateIsExpire();
    }
}
