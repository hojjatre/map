package com.example.map.model.reportdata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Traffic {
    Boolean light;
    Boolean semiHeavy;
    Boolean lock;

    public Traffic(){
        light = false;
        semiHeavy = false;
        lock = false;
    }
}
