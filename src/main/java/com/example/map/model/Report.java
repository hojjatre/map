package com.example.map.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.Date;
@Entity
@Table(name = "report")
@Getter
@Setter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private EReport reportType;
    @Column(columnDefinition = "geometry")
    private Geometry coordinate;
    @Column(name = "report_data", columnDefinition = "JSON")
    private String reportData;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserImp userImp;
}
