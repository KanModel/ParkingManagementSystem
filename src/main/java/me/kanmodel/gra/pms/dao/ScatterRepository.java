package me.kanmodel.gra.pms.dao;

import me.kanmodel.gra.pms.entity.ParkScatter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScatterRepository extends JpaRepository<ParkScatter, Long> {
}
