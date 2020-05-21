package me.kanmodel.gra.pms.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 停车场分布变化记录实体
 */
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
    private Long scatterID;

    @ApiModelProperty("改变为的状态")
    @Column(name = "is_use", columnDefinition = "bit(1) default 0")
    private Boolean use;

    @ApiModelProperty("记录时间")
    @Column(name = "record_time", columnDefinition = "datetime default current_timestamp")
    private Timestamp recordTime;

    @ApiModelProperty("是否删除")
    @Column(name = "is_delete", columnDefinition = "bit(1) default 0")
    private Boolean isDelete = false;

    public ParkScatterRecord(){
    }

    private ParkScatterRecord(Long scatterID, Boolean use, Timestamp recordTime, Boolean isDelete) {
        this.scatterID = scatterID;
        this.use = use;
        this.recordTime = recordTime;
        this.isDelete = isDelete;
    }

    private ParkScatterRecord(Long scatterID, Boolean use, Timestamp recordTime) {
        this(scatterID, use, recordTime, false);
    }

    public ParkScatterRecord(Long scatterID, Boolean use){
        this(scatterID, use, new Timestamp(System.currentTimeMillis()));
    }

    public ParkScatterRecord(Long id, Timestamp recordTime,Long scatterID, Boolean use, Boolean isDelete) {
//        String[] tableHeaders = {"park_scatter_record_id", "record_time", "scatter_id", "is_use", "is_delete"};
        this.id = id;
        this.scatterID = scatterID;
        this.use = use;
        this.recordTime = recordTime;
        this.isDelete = isDelete;
    }

    @ApiIgnore
    public String getParkTimeFormat() {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return time.format(this.recordTime.getTime());
    }
}
