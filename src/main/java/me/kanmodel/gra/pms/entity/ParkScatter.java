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
    @Column(name = "park_record_id")
    private Long id;

    @ApiModelProperty("X坐标")
    @Column(name = "x", columnDefinition = "double")
    private double x;

    @ApiModelProperty("Y坐标")
    @Column(name = "y", columnDefinition = "double")
    private double y;

    ParkScatter() {}

    public ParkScatter(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
