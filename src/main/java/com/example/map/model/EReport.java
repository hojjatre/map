package com.example.map.model;

public enum EReport {
    TRAFFIC,
    CAMERA,
    ACCIDENT,
    POLICE,
    SPEED_BUMP,
    MAP_BUGS,
    ROAD_LOCATION,
    EVENTS_ON_WAY,
    WEATHER_CONDITIONS;

    @Override
    public String toString() {
        switch (this){
            case TRAFFIC -> {
                return "traffic";
            }
            case CAMERA -> {
                return "camera";
            }
            case ACCIDENT -> {
                return "accident";
            }
            case POLICE -> {
                return "police";
            }
            case SPEED_BUMP -> {
                return "speed_bump";
            }
            case MAP_BUGS -> {
                return "map_bugs";
            }
            case ROAD_LOCATION -> {
                return "road_location";
            }
            case EVENTS_ON_WAY -> {
                return "events_on_way";
            }
            case WEATHER_CONDITIONS -> {
                return "weather_conditions";
            }
            default -> {
                return "None";
            }
        }
    }
}
