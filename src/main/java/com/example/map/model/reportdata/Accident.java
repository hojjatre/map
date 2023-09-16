package com.example.map.model.reportdata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Accident {
    Boolean light;
    Boolean heavy;
    Boolean oppositeLine;

    public Accident() {
        light = false;
        heavy = false;
        oppositeLine = false;
    }
}
