package com.example.map.dto.operator;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckedRequest {
    private String username;
    private String coordinate;
    private String reportType;
    private String reportData;
}
