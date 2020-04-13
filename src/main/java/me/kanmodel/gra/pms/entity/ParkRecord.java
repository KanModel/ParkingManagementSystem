package me.kanmodel.gra.pms.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "park_record")
@Data
public class ParkRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "park_record_id")
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("车牌")
    @Column(name = "car_id", columnDefinition = "varchar(20)  default ''")
    private String carID;

    @ApiModelProperty("进库/出库")
    @Column(name = "enter", columnDefinition = "bit(1) default 0")
    private Boolean enter = true;

    @ApiModelProperty("是否在库中")
    @Column(name = "exist", columnDefinition = "bit(1) default 0")
    private Boolean exist = true;

    @ApiModelProperty("记录时间")
    @Column(name = "record_time", columnDefinition = "datetime default current_timestamp")
    private Timestamp recordTime;

    @ApiModelProperty("是否删除")
    @Column(name = "is_delete", columnDefinition = "bit(1) default 0")
    private Boolean isDelete = false;

    public ParkRecord() {
    }

    public ParkRecord(String carID) {
        this(carID, true, true);
    }

    public ParkRecord(String carID, boolean enter, boolean exist) {
        this.carID = carID;
        this.enter = enter;
        this.exist = exist;
        this.recordTime = new Timestamp(System.currentTimeMillis());
    }

    @ApiIgnore
    public String getParkTimeFormat() {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return time.format(this.recordTime.getTime());
    }
}
