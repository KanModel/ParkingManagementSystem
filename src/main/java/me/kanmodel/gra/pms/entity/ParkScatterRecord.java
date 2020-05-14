package me.kanmodel.gra.pms.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "park_scatter_record")
@Data
public class ParkScatterRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "park_scatter_record_id")
    private Long id;

    @ApiModelProperty("分布点ID")
    @Column(name = "scatter_id")
    private Long scatterId;

    @ApiModelProperty("改变为的状态")
    @Column(name = "is_use", columnDefinition = "bit(1) default 0")
    private Boolean use;

    @ApiModelProperty("记录时间")
    @Column(name = "record_time", columnDefinition = "datetime default current_timestamp")
    private Timestamp recordTime;

    public ParkScatterRecord(){
    }
}
