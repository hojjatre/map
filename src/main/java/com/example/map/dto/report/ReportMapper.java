package com.example.map.dto.report;

import com.example.map.model.Report;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper
public interface ReportMapper {

    ReportMapper instance = Mappers.getMapper(ReportMapper.class);

    @Mapping(source = "reportData", target = "reportData")
//    @Mapping(source = "date", target = "date")
    @Mapping(expression = "java(timeToString(report.getDate()))", target = "date")
    @Mapping(expression = "java(geomToString(report.getCoordinate()))", target = "coordinate")
    @Mapping(expression = "java(report.getReportType().toString())", target = "reportType")
    @Mapping(source = "checkStatus", target = "checkStatus")
    @Mapping(source = "userImp.username", target = "username")
    @Mapping(source = "userImp.id", target = "user_id")
    ReportViewRedis entityToDTO(Report report);

    default String timeToString(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String serializedDateTime = localDateTime.format(formatter);
        return serializedDateTime;
    }
    default String geomToString(Geometry point) {
        return new WKTWriter().write(point);
    }
}
