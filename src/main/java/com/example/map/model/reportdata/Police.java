package com.example.map.model.reportdata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Police {
    Boolean cop;
    Boolean undercoverCop;
    Boolean oppositeLine;

    public Police(){
        cop = false;
        undercoverCop = false;
        oppositeLine = false;
    }
}
