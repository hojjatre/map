package com.example.map.model.reportdata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WeatherConditions {
    Boolean slipRoad;
    Boolean fog;
    Boolean chains;

    public WeatherConditions(){
        slipRoad = false;
        fog = false;
        chains = false;
    }
}
