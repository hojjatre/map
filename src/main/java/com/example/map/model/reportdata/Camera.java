package com.example.map.model.reportdata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Camera {
    Boolean speedControl;
    Boolean redLight;

    public Camera(){
        speedControl = false;
        redLight = false;
    }
}
