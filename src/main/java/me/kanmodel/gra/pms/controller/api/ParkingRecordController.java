package me.kanmodel.gra.pms.controller.api;

import io.swagger.annotations.ApiOperation;
import me.kanmodel.gra.pms.dao.RecordRepository;
import me.kanmodel.gra.pms.entity.ParkRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理入库出库
 */
@Controller
@RequestMapping("/api/park")
public class ParkingRecordController {

    @Autowired
    private RecordRepository recordRepository;

    @PostMapping("/enter")
    @ApiOperation("入库")
    private ResponseEntity<Map<String, String>> enter(@RequestParam(required = false) String carID){
        Map<String, String> result = new HashMap<>();
        if (carID == null) {
            result.put("status", "missing parameters");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        //判断是否已在库中
        if(recordRepository.findByCarIDAndExistAndEnter(carID, true, true).isPresent()){
            result.put("status", "exist");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        result.put("status", "enter success");
        recordRepository.save(new ParkRecord(carID));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/exit")
    @ApiOperation("出库")
    private ResponseEntity<Map<String, String>> exit(@RequestParam(required = false) String carID){
        Map<String, String> result = new HashMap<>();
        if (carID == null) {
            result.put("status", "missing parameters");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        //判断是否在库中
        if(!recordRepository.findByCarIDAndExistAndEnter(carID, true, true).isPresent()){
            result.put("status", "not exist");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        ParkRecord enterRecord = recordRepository.findByCarIDAndExistAndEnter(carID, true, true).get();
        enterRecord.setExist(false);
        ParkRecord exitRecord = new ParkRecord(carID, false, false);
        recordRepository.save(enterRecord);
        recordRepository.save(exitRecord);

        result.put("status", "exit success");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/list")
    private ResponseEntity<List<ParkRecord>> listAll(){
        List<ParkRecord> list = recordRepository.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
