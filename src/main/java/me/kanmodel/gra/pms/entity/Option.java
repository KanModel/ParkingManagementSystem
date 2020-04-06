package me.kanmodel.gra.pms.entity;

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

    @Column(name = "option_key", columnDefinition = "varchar(50)", nullable = false)
    private String key;

    @Column(name = "option_value", columnDefinition = "varchar(100)  default ''")
    private String value;

    @Column(name = "option_description", columnDefinition = "varchar(100)  default ''")
    private String description;

    public Option() {
    }

    public Option(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Option(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }
}
