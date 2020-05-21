package me.kanmodel.gra.pms.controller;

import io.swagger.annotations.ApiOperation;
import me.kanmodel.gra.pms.dao.OptionRepository;
import me.kanmodel.gra.pms.dao.RecordRepository;
import me.kanmodel.gra.pms.dao.ScatterRecordRepository;
import me.kanmodel.gra.pms.entity.ParkRecord;
import me.kanmodel.gra.pms.entity.ParkScatterRecord;
import me.kanmodel.gra.pms.service.ExcelServiceImp;
import me.kanmodel.gra.pms.service.ParkLogExporter;
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

@Controller
@RequestMapping("/park")
@ApiIgnore
public class ParkingController {

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private ScatterRecordRepository scatterRecordRepository;
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
        System.out.println("PageNo" + pageNo + " ScatterID" + scatterID);
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

    @RequestMapping("/export/parkrecord/excel")
    private ResponseEntity<byte[]> regParkRecordExcel() throws IOException {
        return parkLogExporter.getParkingRecordResult();
    }

    @RequestMapping("/export/parkscatter/excel")
    private ResponseEntity<byte[]> regParkScatterRecordExcel() throws IOException {
        return parkLogExporter.getScatterRecordResult();
    }

    @ApiOperation(value = "导入Excel表", httpMethod = "POST")
    @RequestMapping(path = "/upload/parkrecord/excel",method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView uploadIAORecord(@RequestParam("file") MultipartFile file){
        try{
            excelService.batchImport(file.getOriginalFilename(), file, "park_record");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/park/iao/record");
    }

    @ApiOperation(value = "导入Excel表", httpMethod = "POST")
    @RequestMapping(path = "/upload/parkscatter/excel",method = RequestMethod.POST)
    public ModelAndView uploadScatterRecord(@RequestParam("file") MultipartFile file){
        try{
            excelService.batchImport(file.getOriginalFilename(), file, "park_scatter_record");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/park/scatter/record");
    }

    @RequestMapping("/delete/iaorecord/all")
    public ModelAndView deleteIAORecordAll(){
        recordRepository.deleteAll();
        return new ModelAndView("redirect:/park/iao/record");
    }

    @RequestMapping("/delete/scatterrecord/all")
    public ModelAndView deleteScatterRecordAll(){
        scatterRecordRepository.deleteAll();
        return new ModelAndView("redirect:/park/scatter/record");
    }

    @RequestMapping("/chart")
    public String echarts(Model model) {
        return "park/echart";
    }
}
