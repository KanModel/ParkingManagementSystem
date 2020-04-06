package me.kanmodel.gra.pms.dao;

import me.kanmodel.gra.pms.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 角色的持久化方法
 */
public interface RoleRepository extends JpaRepository<SysRole, Long> {
    SysRole findByName(String name);
}
