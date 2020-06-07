package me.kanmodel.gra.pms.dao;

import me.kanmodel.gra.pms.entity.ParkScatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScatterRepository extends JpaRepository<ParkScatter, Long> {
    @Query(nativeQuery = true, value = "select * from park_scatter where device_id like %:device%")
    Page<ParkScatter> findAllByDeviceID(@Param("device") String deviceID, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from park_scatter")
    Page<ParkScatter> findAll(Pageable pageable);
}
