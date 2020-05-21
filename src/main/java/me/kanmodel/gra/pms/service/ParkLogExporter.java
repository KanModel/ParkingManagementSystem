package me.kanmodel.gra.pms.service;

import me.kanmodel.gra.pms.dao.RecordRepository;
import me.kanmodel.gra.pms.dao.ScatterRecordRepository;
import me.kanmodel.gra.pms.entity.ParkRecord;
import me.kanmodel.gra.pms.entity.ParkScatterRecord;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ParkLogExporter {
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private ScatterRecordRepository scatterRecordRepository;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY_MM_dd_HH_mm_ss");

    public ResponseEntity<byte[]> getParkingRecordResult() throws IOException {
        Date now = new Date();
        String[] tableHeaders = {"park_record_id", "car_id", "enter", "exist", "is_delete", "record_time"};

        List<ParkRecord> parkRecordList = recordRepository.findAll();

        //声明工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        //声明表格
        HSSFSheet sheet = workbook.createSheet("park_record");
        //创建行
        HSSFRow hssfRow = sheet.createRow(0);

        //添加表头
        for (int i = 0; i < tableHeaders.length; i++) {
            HSSFCell cell = hssfRow.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(tableHeaders[i]);
            cell.setCellValue(text);
        }

        int index = 1;
        for (ParkRecord record: parkRecordList){
            hssfRow = sheet.createRow(index++);
            hssfRow.createCell(0).setCellValue(String.valueOf(record.getId()));
            hssfRow.createCell(1).setCellValue(String.valueOf(record.getCarID()));
            hssfRow.createCell(2).setCellValue(String.valueOf(record.getEnter()));
            hssfRow.createCell(3).setCellValue(String.valueOf(record.getExist()));
            hssfRow.createCell(4).setCellValue(String.valueOf(record.getIsDelete()));
            hssfRow.createCell(5).setCellValue(String.valueOf(record.getRecordTime().getTime()));
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Cache-Type", "application/msword");
        headers.add("Content-Disposition", "attachment; filename=" + dateFormat.format(now) + "_ParkingRecordResult.xls");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));

        return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }

    public ResponseEntity<byte[]> getScatterRecordResult() throws IOException {
        Date now = new Date();
        String[] tableHeaders = {"park_scatter_record_id", "record_time", "scatter_id", "is_use", "is_delete"};

        List<ParkScatterRecord> parkRecordList = scatterRecordRepository.findAll();

        //声明工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        //声明表格
        HSSFSheet sheet = workbook.createSheet("park_scatter_record");
        //创建行
        HSSFRow hssfRow = sheet.createRow(0);

        //添加表头
        for (int i = 0; i < tableHeaders.length; i++) {
            HSSFCell cell = hssfRow.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(tableHeaders[i]);
            cell.setCellValue(text);
        }

        int index = 1;
        for (ParkScatterRecord record: parkRecordList){
            hssfRow = sheet.createRow(index++);
            hssfRow.createCell(0).setCellValue(String.valueOf(record.getId()));
            hssfRow.createCell(1).setCellValue(String.valueOf(record.getRecordTime().getTime()));
            hssfRow.createCell(2).setCellValue(String.valueOf(record.getScatterID()));
            hssfRow.createCell(3).setCellValue(String.valueOf(record.getUse()));
            hssfRow.createCell(4).setCellValue(String.valueOf(record.getIsDelete()));
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Cache-Type", "application/msword");
        headers.add("Content-Disposition", "attachment; filename=" + dateFormat.format(now) + "_ScatterRecordResult.xls");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));

        return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }
}
