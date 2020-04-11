package me.kanmodel.gra.pms.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;

/**
 * @description: 系统设置实体
 * @author: KanModel
 * @create: 2019-07-09 08:58
 */
@Entity
@Table(name = "sys_option")
@Data
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ApiModelProperty("键")
    @Column(name = "option_key", columnDefinition = "varchar(50)", nullable = false)
    private String key;

    @ApiModelProperty("值")
    @Column(name = "option_value", columnDefinition = "varchar(100)  default ''")
    private String value;

    @ApiModelProperty("介绍")
    @Column(name = "option_description", columnDefinition = "varchar(100)  default ''")
    private String description;

    @ApiModelProperty("可见")
    @Column(name = "visible", columnDefinition = "bit(1) default 0")
    private Boolean visible = false;

    public Option() {
    }

    public Option(String key, String value, String description) {
        this(key, value, description, true);
    }

    public Option(String key, String value, String description, Boolean show) {
        this.key = key;
        this.value = value;
        this.description = description;
        this.visible = show;
    }
}
