package com.example.map.controller.operator;

import com.example.map.dto.report.ReportViewRedis;
import com.example.map.service.operator.OperatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
