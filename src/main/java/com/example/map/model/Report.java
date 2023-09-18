package com.example.map.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.Date;
@Entity
@Table(name = "report")
@Getter
@Setter
@NamedEntityGraph(
        name = "graph.report",
        attributeNodes = {
                @NamedAttributeNode("id"),
                @NamedAttributeNode("reportType"),
                @NamedAttributeNode("reportData"),
                @NamedAttributeNode("date"),
                @NamedAttributeNode("checkStatus"),
                @NamedAttributeNode(value = "userImp", subgraph = "graph.user")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "graph.user",
                        attributeNodes = {
                                @NamedAttributeNode("username"),
                                @NamedAttributeNode("email"),
                        }
                )
        }
)
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private EReport reportType;
//    @Type(type = "org.hibernate.spatial.GeometryType")
//    @Column(columnDefinition = "point")
    private Point coordinate;
    @Column(name = "report_data", columnDefinition = "JSON")
    private String reportData;
    private LocalDateTime date;
    @Column(columnDefinition = "BOOLEAN")
    private boolean checkStatus;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserImp userImp;

    public Report(EReport reportType, Point coordinate, String reportData, LocalDateTime date, boolean checkStatus, UserImp userImp) {
        this.reportType = reportType;
        this.coordinate = coordinate;
        this.reportData = reportData;
        this.date = date;
        this.checkStatus = checkStatus;
        this.userImp = userImp;
    }

    public Report(EReport reportType, String reportData, LocalDateTime date, boolean checkStatus, UserImp userImp) {
        this.reportType = reportType;
        this.reportData = reportData;
        this.date = date;
        this.checkStatus = checkStatus;
        this.userImp = userImp;
    }

    public Report() {

    }

    public boolean getCheckStatus(){
        return checkStatus;
    }


}
