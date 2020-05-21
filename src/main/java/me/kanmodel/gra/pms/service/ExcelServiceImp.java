package me.kanmodel.gra.pms.service;

import me.kanmodel.gra.pms.dao.RecordRepository;
import me.kanmodel.gra.pms.dao.ScatterRecordRepository;
import me.kanmodel.gra.pms.entity.ParkRecord;
import me.kanmodel.gra.pms.entity.ParkScatterRecord;
import me.kanmodel.gra.pms.util.ExcelImportUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExcelServiceImp {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private ScatterRecordRepository scatterRecordRepository;

    private static final String TGA = "ExcelService : ";

    private String result = "";//返回的结果

    private String errorMsg = "";//记录错误信息


    private int getTotalRows(Workbook wb) {
        //获取第一个sheet的所有行数
        return wb.getSheetAt(0).getPhysicalNumberOfRows();
    }

    private Sheet getSheet(Workbook wb) {
        //将第一个sheet返回回去
        return wb.getSheetAt(0);
    }

    private int getTotalCell(Workbook wb) {
        Sheet sheet = getSheet(wb);

        int tempTotalRows = getTotalRows(wb);

        //得到Excel的列数(前提是有行数)，从第二行算起
        //这个可以自定义，从真实数据的一行开始读取
        if (tempTotalRows >= 2 && sheet.getRow(1) != null) {
            return sheet.getRow(1).getPhysicalNumberOfCells();
        }
        return -1;
    }

    /**
     * 分批处理导入
     *
     * @param fileName
     * @param mfile
     * @param tableName 要导入得数据库表名
     * @return
     */
    public String batchImport(String fileName, MultipartFile mfile, String tableName) {

        File uploadDir = new File("E:\\fileupload");
        //创建一个目录 （它的路径名由当前 File 对象指定，包括任一必须的父路径。）
        if (!uploadDir.exists()) {
            boolean result = uploadDir.mkdirs();
            System.out.println("创建路径：" + uploadDir.getAbsolutePath() + result);
        }
        //新建一个文件
        File tempFile = new File("E:\\fileupload\\" + new Date().getTime() + ".xlsx");
        //初始化输入流
        InputStream is = null;
        try {
            //将上传的文件写入新建的文件中
            mfile.transferTo(tempFile);

            //根据新建的文件实例化输入流
            is = new FileInputStream(tempFile);

            //根据版本选择创建Workbook的方式
            Workbook wb = null;
            //根据文件名判断文件是2003版本还是2007版本
            if (ExcelImportUtils.isExcel2007(fileName)) {
//                wb = new XSSFWorkbook(is);
            } else {
                wb = new HSSFWorkbook(is);
            }
            //根据excel里面的内容读取信息
            return readExcel(tableName, wb, tempFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    is = null;
                    e.printStackTrace();
                }
            }
        }
        return "导入出错！请检查数据格式！";


    }//batchImport

    /**
     * 返回对应表的读取实列
     * <p>
     * 根据表名来确定要上传到得数据库
     *
     * @param tableName 要上传到数据库的表名
     * @param wb
     * @param file
     * @return
     */
    private String readExcel(String tableName, Workbook wb, File file) {

        switch (tableName) {
            case "park_record":
                logger.info("开始导入 park_record 数据");
                return readParkRecordExcelValue(wb, file);
            case "park_scatter_record":
                logger.info("开始导入 park_scatter_record 数据");
                return readParkScatterRecordExcelValue(wb, file);
        }
        return null;
    }

    /**
     * @param wb
     * @param tempFile 临时文件，在文件读入msql后执行删除操作
     * @return
     */
    private String readParkRecordExcelValue(Workbook wb, File tempFile) {

        //得到第一个sheet
        Sheet sheet = getSheet(wb);
        //得到Excel的行数
        int totalRows = getTotalRows(wb);
        //总列数
        int totalCells = getTotalCell(wb);
        //得到Excel的列数，第二行算起

        List<ParkRecord> records = new ArrayList<>();
        ParkRecord record;

        String br = "<br/>";
        //循环excel中的每一行数据
        for (int r = 1; r < totalRows; r++) {
            String rowMessage = "";
            Row row = sheet.getRow(r);
            if (row == null) {
                errorMsg += br + "第" + (r + 1) + "行数据有问题，请仔细检查！";
                continue;
            }

//            String[] tableHeaders = {"park_record_id", "car_id", "enter", "exist", "is_delete", "record_time"};

            record = new ParkRecord(
                    Long.valueOf(row.getCell(0).getStringCellValue()),
                    row.getCell(1).getStringCellValue(),
                    Boolean.valueOf(row.getCell(2).getStringCellValue()),
                    Boolean.valueOf(row.getCell(3).getStringCellValue()),
                    Boolean.valueOf(row.getCell(4).getStringCellValue()),
                    new Timestamp(Long.parseLong(row.getCell(5).getStringCellValue()))
            );

           /* //循环excel中的每一列数据
            for (int c = 0; c < totalCells; c++) {
                //获取每一列的一个单元格
                Cell cell = row.getCell(c);//获取每一个单元格cell

                if (cell != null) {
                    switch (c) {
                        case 0:
                            //设置读取的字段为字符类型
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            record.setUsername(cell.getStringCellValue());//读取并设置到实体类中
                            break;
                        case 1:
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            record.setPassword(cell.getStringCellValue());
                            break;
                        case 2:
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            record.setPhone(cell.getStringCellValue());
                            break;
                        case 3:
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            record.setSex(cell.getStringCellValue());
                            break;
                    }//end switch
                }//end if

            }//end for遍历完每一列数据*/

            //拼接每行的错误提示
            if (!StringUtils.isEmpty(rowMessage)) {

                errorMsg += br + "第" + (r + 1) + "行，" + rowMessage;
                System.out.println(TGA + errorMsg);

            } else {
                //数据正常将其添加到集合中
                records.add(record);
            }//end if

        }//遍历完每一行数据

        //删除上传的临时文件
        if (tempFile.exists()) {
            tempFile.delete();
        }

        //将数据导入到Mysql中
        for (ParkRecord t : records) {
            recordRepository.save(t);
        }

        result = "导入成功，共" + records.size() + "条数据！";
        logger.info(result);

        return result;
    }

    private String readParkScatterRecordExcelValue(Workbook wb, File tempFile) {

        //得到第一个sheet
        Sheet sheet = getSheet(wb);
        //得到Excel的行数
        int totalRows = getTotalRows(wb);
        //总列数
        int totalCells = getTotalCell(wb);
        //得到Excel的列数，第二行算起

        List<ParkScatterRecord> records = new ArrayList<>();
        ParkScatterRecord record;

        String br = "<br/>";
        //循环excel中的每一行数据
        for (int r = 1; r < totalRows; r++) {
            String rowMessage = "";
            Row row = sheet.getRow(r);
            if (row == null) {
                errorMsg += br + "第" + (r + 1) + "行数据有问题，请仔细检查！";
                continue;
            }

//            String[] tableHeaders = {"park_scatter_record_id", "record_time", "scatter_id", "is_use", "is_delete"};

            record = new ParkScatterRecord(
                    Long.valueOf(row.getCell(0).getStringCellValue()),
                    new Timestamp(Long.parseLong(row.getCell(1).getStringCellValue())),
                    Long.valueOf(row.getCell(2).getStringCellValue()),
                    Boolean.valueOf(row.getCell(3).getStringCellValue()),
                    Boolean.valueOf(row.getCell(4).getStringCellValue())
            );

            //拼接每行的错误提示
            if (!StringUtils.isEmpty(rowMessage)) {

                errorMsg += br + "第" + (r + 1) + "行，" + rowMessage;
                System.out.println(TGA + errorMsg);

            } else {
                //数据正常将其添加到集合中
                records.add(record);
            }//end if

        }//遍历完每一行数据

        //删除上传的临时文件
        if (tempFile.exists()) {
            tempFile.delete();
        }

        //将数据导入到Mysql中
        for (ParkScatterRecord t : records) {
            scatterRecordRepository.save(t);
        }

        result = "导入成功，共" + records.size() + "条数据！";
        logger.info(result);

        return result;
    }

}