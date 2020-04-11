package me.kanmodel.gra.pms.controller;

import me.kanmodel.gra.pms.dao.RecordRepository;
import me.kanmodel.gra.pms.entity.ParkRecord;
import me.kanmodel.gra.pms.entity.ParkScatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/park")
public class ParkingController {

    @Autowired
    private RecordRepository recordRepository;

    @ModelAttribute
    public void generalModel(Model model) {
        model.addAttribute("site_name", "PMS");
    }

    @RequestMapping("/list")
    public String listUsers(Model model,
                            @RequestParam(value = "res", required = false) String res,
                            @RequestParam(value = "carID", required = false, defaultValue = "") String carID,
                            @RequestParam(value = "no", defaultValue = "1", required = false) int pageNo,
                            @RequestParam(value = "size", defaultValue = "10", required = false) int pageSize) {
        System.out.println("PageNo" + pageNo + " PageSize" + pageSize + carID);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, new Sort(Sort.Direction.DESC, "park_record_id"));
        Page<ParkRecord> page = recordRepository.findAllByCarIDAndIsDelete(carID, pageable);
        model.addAttribute("recordList", page.getContent());
        model.addAttribute("res", res);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pageCount", page.getTotalPages());
        model.addAttribute("parkCount", recordRepository.countByExistAndIsDelete(true, false));
        if (carID != null) model.addAttribute("carID", carID);
        return "/park/record_list";
    }

    @RequestMapping("/chart")
    public String echarts(Model model) {
//        model.addAttribute("data", "");
        return "park/echart";
    }

    @RequestMapping("/scatter")
    @ResponseBody
    public List<ParkScatter> ParkScatter() {
        ArrayList<ParkScatter> list = new ArrayList<>();

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

        return list;
    }
}
