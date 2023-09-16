package com.example.map.dto.report;

import com.example.map.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReportMapper {
    ReportMapper instance = Mappers.getMapper(ReportMapper.class);

    @Mapping(source = "reportData", target = "reportData")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "coordinate", target = "coordinate")
    @Mapping(source = "reportType", target = "reportType")
    @Mapping(source = "checkStatus", target = "checkStatus")
    @Mapping(source = "userImp.username", target = "username")
    @Mapping(source = "userImp.id", target = "user_id")
    ReportViewRedis entityToDTO(Report report);
}
