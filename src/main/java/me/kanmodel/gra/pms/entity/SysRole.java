package me.kanmodel.gra.pms.entity;/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: KanModel
 * Date: 2019-04-07-11:01
 */

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @description: todo
 * @author: KanModel
 * @create: 2019-04-07 11:01
 */
@Entity
@Data
public class SysRole {
    @Id
    @GeneratedValue
    @Column(name = "role_id")
    private Long id;
    @Column(name = "role_name", columnDefinition = "varchar(20)", nullable = false)
    private String name;

    public SysRole(){}

    public SysRole(Long id , String name) {
        this.id = id;
        this.name = name;
    }

    public SysRole(Role role){
        this.id = role.getId();
        this.name = role.getName();
    }
}
