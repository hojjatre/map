package com.example.map.model.reportdata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoadLocation {
    Boolean gasStation;
    Boolean fillingStation;
    Boolean redCrescent;
    Boolean parking;
    Boolean welfareServices;
    Boolean police;

    public RoadLocation(){
        gasStation = false;
        fillingStation = false;
        redCrescent = false;
        parking = false;
        welfareServices = false;
        police = false;
    }
}