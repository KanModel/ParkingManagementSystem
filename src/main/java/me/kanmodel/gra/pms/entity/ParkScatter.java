package me.kanmodel.gra.pms.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "park_scatter")
@Data
public class ParkScatter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "park_scatter_id")
    private Long id;

    @ApiModelProperty("X坐标")
    @Column(name = "x", columnDefinition = "double")
    private double x;

    @ApiModelProperty("Y坐标")
    @Column(name = "y", columnDefinition = "double")
    private double y;

    @ApiModelProperty("使用中")
    @Column(name = "is_use", columnDefinition = "bit(1) default 0")
    private Boolean use;

    @ApiModelProperty("设备编号")
    @Column(name = "device_id", columnDefinition = "varchar(20)  default ''")
    private String deviceID;

    public void update(double x, double y, String deviceID){
        this.x = x;
        this.y = y;
        this.deviceID = deviceID;
    }

    public ParkScatter() {
    }

    public ParkScatter(double x, double y, Boolean use) {
        this.x = x;
        this.y = y;
        this.use = use;
    }

    public ParkScatter(double x, double y, Boolean use, String deviceID) {
        this.x = x;
        this.y = y;
        this.use = use;
        this.deviceID = deviceID;
    }
}
