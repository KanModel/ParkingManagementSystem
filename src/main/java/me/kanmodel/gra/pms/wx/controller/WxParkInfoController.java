package me.kanmodel.gra.pms.wx.controller;

import io.swagger.annotations.ApiOperation;
import me.kanmodel.gra.pms.dao.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/park")
public class WxParkInfoController {
    @Autowired
    private OptionRepository optionRepository;


    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "获取停车场信息")
    private ResponseEntity<Map<String, String >> getParkInfo(){
        Map<String, String> result = new HashMap<>();

        int freeMinutes = Integer.parseInt(optionRepository.findByKey("free_minutes").get().getValue());
        int feePerHours = Integer.parseInt(optionRepository.findByKey("fee_per_hours").get().getValue());
        result.put("free_minutes", String.valueOf(freeMinutes));
        result.put("fee_per_hours", String.valueOf(feePerHours));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
