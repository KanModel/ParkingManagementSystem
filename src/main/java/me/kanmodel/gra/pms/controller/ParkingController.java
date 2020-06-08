package me.kanmodel.gra.pms.controller;

import io.swagger.annotations.ApiOperation;
import me.kanmodel.gra.pms.dao.OptionRepository;
import me.kanmodel.gra.pms.dao.RecordRepository;
import me.kanmodel.gra.pms.dao.ScatterRecordRepository;
import me.kanmodel.gra.pms.dao.ScatterRepository;
import me.kanmodel.gra.pms.entity.ParkRecord;
import me.kanmodel.gra.pms.entity.ParkScatter;
import me.kanmodel.gra.pms.entity.ParkScatterRecord;
import me.kanmodel.gra.pms.service.ExcelServiceImp;
import me.kanmodel.gra.pms.service.ParkLogExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/park")
@ApiIgnore
public class ParkingController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private ScatterRecordRepository scatterRecordRepository;
    @Autowired
    private ScatterRepository scatterRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private ParkLogExporter parkLogExporter;
    @Autowired
    private ExcelServiceImp excelService;

    @ModelAttribute
    public void generalModel(Model model) {
        model.addAttribute("site_name", optionRepository.findByKey("site_name").get().getValue());
    }

    @RequestMapping("/iao/record")
    public String listRecord(Model model,
                             @RequestParam(value = "res", required = false) String res,
                             @RequestParam(value = "carID", required = false, defaultValue = "") String carID,
                             @RequestParam(value = "no", defaultValue = "1", required = false) int pageNo,
                             @RequestParam(value = "size", defaultValue = "10", required = false) int pageSize) {
//        System.out.println("PageNo" + pageNo + " PageSize" + pageSize + carID);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, new Sort(Sort.Direction.DESC, "park_record_id"));
        Page<ParkRecord> page = recordRepository.findAllByCarIDAndIsDelete(carID, pageable);
        model.addAttribute("recordList", page.getContent());
        model.addAttribute("res", res);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pageCount", page.getTotalPages());
        model.addAttribute("parkCount", recordRepository.countByExistAndIsDelete(true, false));
        if (carID != null) model.addAttribute("carID", carID);
        return "/park/iao_record_list";
    }

    @RequestMapping("/scatter/record")
    public String listScatterRecord(Model model,
                                    @RequestParam(value = "res", required = false) String res,
                                    @RequestParam(value = "scatterID", required = false, defaultValue = "0") Long scatterID,
                                    @RequestParam(value = "no", defaultValue = "1", required = false) int pageNo,
                                    @RequestParam(value = "size", defaultValue = "10", required = false) int pageSize) {
        logger.info("PageNo" + pageNo + " ScatterID" + scatterID);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, new Sort(Sort.Direction.DESC, "park_scatter_record_id"));
        Page<ParkScatterRecord> page;
        if (scatterID == 0) {
            page = scatterRecordRepository.findAllByIsDeleteFalse(pageable);
        } else {
            page = scatterRecordRepository.findAllByScatterIDAndIsDeleteFalse(scatterID, pageable);
        }
        model.addAttribute("recordList", page.getContent());
        model.addAttribute("res", res);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pageCount", page.getTotalPages());
        if (scatterID != 0) model.addAttribute("scatterID", scatterID);
        return "/park/scatter_record_list";
    }

    /**
     * 获取分布列表
     */
    @RequestMapping("/scatter/list")
    public String scatterList(Model model,
                              @RequestParam(value = "res", required = false) String res,
                              @RequestParam(value = "deviceID", required = false, defaultValue = "") String deviceID,
                              @RequestParam(value = "no", defaultValue = "1", required = false) int pageNo,
                              @RequestParam(value = "size", defaultValue = "10", required = false) int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, new Sort(Sort.Direction.ASC, "park_scatter_id"));
        Page<ParkScatter> page;
        if (deviceID.equals("")) {
            logger.info("无deviceID");
            page = scatterRepository.findAll(pageable);
        } else {
            logger.info("deviceID:" + deviceID);
            page = scatterRepository.findAllByDeviceID(deviceID, pageable);
        }
        model.addAttribute("scatterList", page.getContent());
        model.addAttribute("res", res);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pageCount", page.getTotalPages());
        model.addAttribute("deviceID", deviceID);
        return "park/scatter_list";
    }

    @PostMapping("/edit/scatter")
    public ModelAndView editScatter(Model model, Long id, double x, double y, String deviceID) {
        Optional<ParkScatter> optional = scatterRepository.findById(id);
        if (optional.isPresent()) {
            ParkScatter scatter = optional.get();
            scatter.update(x, y, deviceID);
            scatterRepository.save(scatter);
        }
        ModelAndView modelAndView = new ModelAndView("redirect:/park/scatter/list");
        modelAndView.addObject("res", "编辑" + id + "分布成功!");
//        model.addAttribute("res", "编辑" + id + "分布成功!");
        return modelAndView;
    }

    @PostMapping("/add/scatter")
    public ModelAndView addScatter(Model model, double x, double y, String deviceID) {
        ParkScatter scatter = new ParkScatter(x, y, false, deviceID);
        scatterRepository.save(scatter);
        ModelAndView modelAndView = new ModelAndView("redirect:/park/scatter/list");
        modelAndView.addObject("res", "增加分布成功!");
        return modelAndView;
    }

    @PostMapping("/delete/scatter")
    public ModelAndView deleteScatter(Model model, Long id) {
        scatterRepository.deleteById(id);
        ModelAndView modelAndView = new ModelAndView("redirect:/park/scatter/list");
        modelAndView.addObject("res", "删除" + id + "分布成功!");
        return modelAndView;
    }

    @ApiOperation("导出停车记录Excel")
    @RequestMapping("/export/parkrecord/excel")
    private ResponseEntity<byte[]> exportParkRecordExcel() throws IOException {
        return parkLogExporter.getParkingRecordResult();
    }

    @ApiOperation("导出分布记录Excel")
    @RequestMapping("/export/scatterrecord/excel")
    private ResponseEntity<byte[]> exportParkScatterRecordExcel() throws IOException {
        return parkLogExporter.getScatterRecordResult();
    }

    @ApiOperation("导出分布Excel")
    @RequestMapping("/export/parkscatter/excel")
    private ResponseEntity<byte[]> exportParkScatterExcel() throws IOException {
        return parkLogExporter.getScatterResult();
    }

    @ApiOperation(value = "导入停车记录Excel表", httpMethod = "POST")
    @RequestMapping(path = "/upload/parkrecord/excel", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView uploadIAORecord(@RequestParam("file") MultipartFile file) {
        try {
            String res = excelService.batchImport(file.getOriginalFilename(), file, "park_record");
            logger.info(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/park/iao/record");
    }

    @ApiOperation(value = "导入分布记录Excel表", httpMethod = "POST")
    @RequestMapping(path = "/upload/scatterrecord/excel", method = RequestMethod.POST)
    public ModelAndView uploadScatterRecord(@RequestParam("file") MultipartFile file) {
        try {
            String res = excelService.batchImport(file.getOriginalFilename(), file, "park_scatter_record");
            logger.info(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/park/scatter/record");
    }

    @ApiOperation(value = "导入分布Excel表", httpMethod = "POST")
    @RequestMapping(path = "/upload/parkscatter/excel", method = RequestMethod.POST)
    public ModelAndView uploadScatter(@RequestParam("file") MultipartFile file) {
        try {
            String res = excelService.batchImport(file.getOriginalFilename(), file, "park_scatter");
            logger.info(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/park/scatter/list");
    }

    @RequestMapping("/delete/iaorecord/all")
    public ModelAndView deleteIAORecordAll() {
        recordRepository.deleteAll();
        return new ModelAndView("redirect:/park/iao/record");
    }

    @RequestMapping("/delete/scatterrecord/all")
    public ModelAndView deleteScatterRecordAll() {
        scatterRecordRepository.deleteAll();
        return new ModelAndView("redirect:/park/scatter/record");
    }

    @RequestMapping("/delete/scatter/all")
    public ModelAndView deleteScatterAll() {
        scatterRepository.deleteAll();
//        scatterRepository.truncate();
        return new ModelAndView("redirect:/park/scatter/list");
    }

    @RequestMapping("/chart")
    public String scatter() {
        return "park/scatter";
    }
}
