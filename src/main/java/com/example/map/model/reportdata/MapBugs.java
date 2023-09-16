package com.example.map.model.reportdata;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapBugs {
    Boolean noEntry;
    Boolean deadend;
    Boolean flowDirection;
    Boolean dun;
    Boolean noCarPath;
    Boolean other;

    public MapBugs(){
        noEntry =false;
        deadend = false;
        flowDirection = false;
        dun = false;
        noCarPath = false;
        other = false;
    }

}
