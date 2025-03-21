package me.kanmodel.gra.pms.controller.api;

import io.swagger.annotations.ApiOperation;
import me.kanmodel.gra.pms.dao.ScatterRecordRepository;
import me.kanmodel.gra.pms.dao.ScatterRepository;
import me.kanmodel.gra.pms.entity.ParkScatter;
import me.kanmodel.gra.pms.entity.ParkScatterRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 管理车位分布及使用情况
 */
@Controller
@RequestMapping("/api/scatter")
public class ParkingScatterController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ScatterRepository scatterRepository;
    @Autowired
    private ScatterRecordRepository scatterRecordRepository;

    @PostMapping("/use")
    @ApiOperation("使用车位")
    private ResponseEntity<Map<String, String>> park(long parkID, boolean use) {
        Map<String, String> result = new HashMap<>();
        Optional<ParkScatter> optionalParkScatter = scatterRepository.findById(parkID);
        if (optionalParkScatter.isPresent()){
            ParkScatter scatter = optionalParkScatter.get();
            if (scatter.getUse() != use) {
                scatter.setUse(use);
                scatterRepository.save(scatter);
                result.put("result", "No." + parkID + " change statue");
                scatterRecordRepository.save(new ParkScatterRecord(parkID, use));
            }else result.put("result", "Nothing change");
        }else {
            result.put("result", "Not exist");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        logger.info(result.get("result"));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/add")
    @ApiOperation("增加分布")
    private ResponseEntity<Map<String, String>> addScatter(double x, double y, String deviceID){
        Map<String, String> result = new HashMap<>();

//        Pattern pattern = Pattern.compile("^\\d+(\\.\\d+)?$");//判断是否为正小数
        if (x < 0 || y < 0){
            result.put("result", "x or y not positive!");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        ParkScatter newScatter = new ParkScatter(x, y, false, deviceID);
        scatterRepository.save(newScatter);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除分布")
    private ResponseEntity<Map<String, String>> deleteScatter(Long id){
        Map<String, String> result = new HashMap<>();

        if (scatterRepository.findById(id).isPresent()){
            scatterRepository.deleteById(id);
        }else {
            result.put("result", id + " ParkScatter not exist!");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/edit")
    @ApiOperation("编辑分布")
    private ResponseEntity<Map<String, String>> editScatter(Long id, double x, double y, String deviceID){
        Map<String, String> result = new HashMap<>();

        Optional<ParkScatter> optionalParkScatter = scatterRepository.findById(id);
        if (optionalParkScatter.isPresent()){
            ParkScatter parkScatter = optionalParkScatter.get();
            parkScatter.update(x, y, deviceID);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("")
    @ResponseBody
    @ApiOperation("获取分布")
    public List<ParkScatter> parkScatter() {
        ArrayList<ParkScatter> list = new ArrayList<>();

        if (scatterRepository.findAll().isEmpty()) {
            list.add(new ParkScatter(2.0, 2.0, true));
            list.add(new ParkScatter(2.0, 4.0, false));
            list.add(new ParkScatter(2.0, 6.0, false));
            list.add(new ParkScatter(2.0, 8.0, false));
            list.add(new ParkScatter(2.0, 10.0, false));
            list.add(new ParkScatter(2.0, 12.0, false));

            list.add(new ParkScatter(6.0, 2.0, true));
            list.add(new ParkScatter(6.0, 4.0, false));
            list.add(new ParkScatter(6.0, 6.0, false));
            list.add(new ParkScatter(6.0, 8.0, false));
            list.add(new ParkScatter(6.0, 10.0, false));
            list.add(new ParkScatter(6.0, 12.0, false));

            list.add(new ParkScatter(14.0, 2.0, true));
            list.add(new ParkScatter(14.0, 4.0, false));
            list.add(new ParkScatter(14.0, 6.0, false));
            list.add(new ParkScatter(14.0, 8.0, false));
            list.add(new ParkScatter(14.0, 10.0, false));
            list.add(new ParkScatter(14.0, 12.0, false));

            list.add(new ParkScatter(18.0, 2.0, true));
            list.add(new ParkScatter(18.0, 4.0, false));
            list.add(new ParkScatter(18.0, 6.0, false));
            list.add(new ParkScatter(18.0, 8.0, false));
            list.add(new ParkScatter(18.0, 10.0, false));
            list.add(new ParkScatter(18.0, 12.0, false));

            for (int i = 1; i <= 9; i++) {
                list.add(new ParkScatter(i * 2, 16.0, false));
            }
            scatterRepository.saveAll(list);
        } else {
            list.addAll(scatterRepository.findAll());
        }

        return list;
    }
    @GetMapping("/count")
    @ResponseBody
    @ApiOperation("可用车位统计")
    public String parkAvailableCount() {
        int availableCount = scatterRepository.countByUseFalse();
        return String.valueOf(availableCount);
    }
}
