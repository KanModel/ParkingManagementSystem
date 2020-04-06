package me.kanmodel.gra.pms.dao;

import me.kanmodel.gra.pms.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * 系统设置的持久化方法
 */
public interface OptionRepository extends JpaRepository<Option, Long> {
    Optional<Option> findByKey(String key);
}
