package com.example.map.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportTiming {
    private final int ACCIDENT = 60;
    private final int CAMERA = 20;
    private final int EVENTSONWAY = 80;
    private final int MAPBUGS = 100;
    private final int POLICE = 20;
    private final int ROADLOCATION = 30;
    private final int SPEEDBUMP = 60;
    private final int TRAFFIC = 40;
    private final int WEATHERCONDITIONS = 40;
}
