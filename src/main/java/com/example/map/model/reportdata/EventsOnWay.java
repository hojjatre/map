package com.example.map.model.reportdata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventsOnWay {
    Boolean constructionOperations;
    Boolean hole;
    Boolean roadBlock;

    public EventsOnWay(){
        constructionOperations = false;
        hole = false;
        roadBlock = false;
    }
}
