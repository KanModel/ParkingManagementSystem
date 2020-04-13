package me.kanmodel.gra.pms.controller.api;

import io.swagger.annotations.ApiOperation;
import me.kanmodel.gra.pms.dao.OptionRepository;
import me.kanmodel.gra.pms.dao.RecordRepository;
import me.kanmodel.gra.pms.dao.ScatterRepository;
import me.kanmodel.gra.pms.entity.ParkRecord;
import me.kanmodel.gra.pms.entity.ParkScatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    @Autowired
    private OptionRepository optionRepository;

    @PostMapping("/enter")
    @ApiOperation("入库")
    private ResponseEntity<Map<String, String>> enter(@RequestParam(required = false) String carID) throws UnsupportedEncodingException {
        Map<String, String> result = new HashMap<>();
        if (carID == null) {
            result.put("status", "missing parameters");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        carID = URLDecoder.decode(carID, "UTF-8");
        //判断是否已在库中
        if (recordRepository.findByCarIDAndExistAndEnter(carID, true, true).isPresent()) {
            result.put("status", "exist");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        result.put("status", "enter success");
        recordRepository.save(new ParkRecord(carID));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/exit")
    @ApiOperation("出库")
    private ResponseEntity<Map<String, String>> exit(@RequestParam(required = false) String carID) throws UnsupportedEncodingException {
        Map<String, String> result = new HashMap<>();
        if (carID == null) {
            result.put("status", "missing parameters");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        carID = URLDecoder.decode(carID, "UTF-8");
        //判断是否在库中
        if (!recordRepository.findByCarIDAndExistAndEnter(carID, true, true).isPresent()) {
            result.put("status", "not exist");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        ParkRecord enterRecord = recordRepository.findByCarIDAndExistAndEnter(carID, true, true).get();
        enterRecord.setExist(false);
        ParkRecord exitRecord = new ParkRecord(carID, false, false);
        recordRepository.save(enterRecord);
        recordRepository.save(exitRecord);
        //获取时间差
        long diff = exitRecord.getRecordTime().getTime() - enterRecord.getRecordTime().getTime();
        result.put("diff", parseMillisecone(diff));
        result.put("status", "exit success");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/list")
    private ResponseEntity<List<ParkRecord>> listAll() {
        List<ParkRecord> list = recordRepository.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    private static String parseMillisecone(long millisecond) {
        String time = null;
        try {
            long yushu_day = millisecond % (1000 * 60 * 60 * 24);
            long yushu_hour = (millisecond % (1000 * 60 * 60 * 24))
                    % (1000 * 60 * 60);
            long yushu_minute = millisecond % (1000 * 60 * 60 * 24)
                    % (1000 * 60 * 60) % (1000 * 60);
            @SuppressWarnings("unused")
            long yushu_second = millisecond % (1000 * 60 * 60 * 24)
                    % (1000 * 60 * 60) % (1000 * 60) % 1000;
            if (yushu_day == 0) {
                return (millisecond / (1000 * 60 * 60 * 24)) + "天";
            } else {
                if (yushu_hour == 0) {
                    return (millisecond / (1000 * 60 * 60 * 24)) + "天"
                            + (yushu_day / (1000 * 60 * 60)) + "时";
                } else {
                    if (yushu_minute == 0) {
                        return (millisecond / (1000 * 60 * 60 * 24)) + "天"
                                + (yushu_day / (1000 * 60 * 60)) + "时"
                                + (yushu_hour / (1000 * 60)) + "分";
                    } else {
                        return (millisecond / (1000 * 60 * 60 * 24)) + "天"
                                + (yushu_day / (1000 * 60 * 60)) + "时"
                                + (yushu_hour / (1000 * 60)) + "分"
                                + (yushu_minute / 1000) + "秒";
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }
}
