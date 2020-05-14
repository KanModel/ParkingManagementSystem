package me.kanmodel.gra.pms.dao;

import me.kanmodel.gra.pms.entity.ParkScatterRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScatterRecordRepository extends JpaRepository<ParkScatterRecord, Long> {
}
