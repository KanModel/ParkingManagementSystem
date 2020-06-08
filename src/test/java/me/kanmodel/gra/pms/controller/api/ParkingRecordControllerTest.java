package me.kanmodel.gra.pms.controller.api;

import me.kanmodel.gra.pms.dao.OptionRepository;
import me.kanmodel.gra.pms.dao.RecordRepository;
import me.kanmodel.gra.pms.entity.ParkRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ParkingRecordControllerTest {
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private OptionRepository optionRepository;

    @Test
    public void feeCal() throws UnsupportedEncodingException {
        String carID = "苏EA13X7";
        Map<String, String> result = new HashMap<>();

        carID = URLDecoder.decode(carID, "UTF-8");

        ParkRecord enterRecord = recordRepository.findByCarIDAndExistAndEnter(carID, true, true).get();
        enterRecord.setExist(false);
        ParkRecord exitRecord = new ParkRecord(carID, false, false);
//        recordRepository.save(enterRecord);
//        recordRepository.save(exitRecord);
        //获取时间差
        long diff = exitRecord.getRecordTime().getTime() - enterRecord.getRecordTime().getTime();
        double freeMinutes = Double.parseDouble(optionRepository.findByKey("free_minutes").get().getValue());
        double feePerHours = Double.parseDouble(optionRepository.findByKey("fee_per_hours").get().getValue());
        int fee = 0;
        if (diff - freeMinutes * 60 * 1000 > 0){
            fee = (int)(Math.ceil((diff - freeMinutes * 60 * 1000) / 1000 / 60 / 60) * feePerHours);
        }
        result.put("diff", String.valueOf(diff));
        result.put("fee", String.valueOf(fee));
        result.put("diff_time", parseMillisecond(diff));
        result.put("status", "exit success");
        for(Map.Entry<String, String> entry : result.entrySet()){
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
    }

    private static String parseMillisecond(long millisecond) {
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
