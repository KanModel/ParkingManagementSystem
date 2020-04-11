package me.kanmodel.gra.pms.dao;

import me.kanmodel.gra.pms.entity.ParkRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<ParkRecord, Long> {
    Optional<ParkRecord> findByCarIDAndExistAndEnter(String key, Boolean exist, Boolean enter);
    @Query(nativeQuery = true, value = "select * from park_record where car_id like %:carID% AND is_delete = 0")
    Page<ParkRecord> findAllByCarIDAndIsDelete(@Param("carID") String carID, Pageable pageable);
    int countByExistAndIsDelete(Boolean exist, Boolean isDelete);
}
