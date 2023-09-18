package com.example.map.dto.report;

import com.example.map.model.EReport;
import com.example.map.model.UserImp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportViewRedis {
    private Long id;
    private EReport reportType;
    private String coordinate;
    private String reportData;
    private String date;
    private boolean checkStatus;
    private String username;
    private Long user_id;

    public boolean getCheckStatus(){
        return checkStatus;
    }
}
