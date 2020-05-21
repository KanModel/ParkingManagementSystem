package me.kanmodel.gra.pms.dao;

import me.kanmodel.gra.pms.entity.ParkScatterRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScatterRecordRepository extends JpaRepository<ParkScatterRecord, Long> {
    @Query(nativeQuery = true, value = "select * from park_scatter_record where scatter_id = :scatterID AND is_delete = 0")
    Page<ParkScatterRecord> findAllByScatterIDAndIsDeleteFalse(Long scatterID, Pageable pageable);
    @Query(nativeQuery = true, value = "select * from park_scatter_record where is_delete = 0")
    Page<ParkScatterRecord> findAllByIsDeleteFalse(Pageable pageable);
}
