package cn.ccut.ylp;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;

import java.io.File;
import java.util.ArrayList;

public class Read {
    public ArrayList<ArrayList<String>> readRows(String fileName) throws IOException {
        File excelFile = new File(fileName);
        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(excelFile));
        HSSFSheet sheet = wb.getSheetAt(0);
        //存放每一个sheet
        // 存放每一单元格数据
        // 存放每一行数据
        ArrayList<ArrayList<ArrayList<String>>> results = new ArrayList<ArrayList<ArrayList<String>>>();
        ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
        for (Row row : sheet) {
            ArrayList<String> cells = new ArrayList<String>();
            for (Cell cell : row) {

                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING://字符串
                        cells.add(cell.getRichStringCellValue().getString());
//                        System.out.print(cell.getRichStringCellValue().getString());
//                        System.out.print("|");
                        break;
                    case Cell.CELL_TYPE_NUMERIC://数值与日期
                        if (DateUtil.isCellDateFormatted(cell)) {
                            cells.add(String.valueOf(cell.getDateCellValue()));
//                            System.out.print(String.valueOf(cell.getDateCellValue()));
                        } else {
                            cells.add(String.valueOf(cell.getNumericCellValue()));
//                            System.out.print(cell.getNumericCellValue());
                        }
//                        System.out.print("|");
                        break;
                    case Cell.CELL_TYPE_BOOLEAN://boolean类型
                        cells.add(String.valueOf(cell.getBooleanCellValue()));
//                        System.out.print(cell.getBooleanCellValue());
//                        System.out.print("|");
                        break;
                    default:
                }
            }
//            System.out.println();
            rows.add(cells);
        }
        return rows;
    }

    public static void main(String[] args) throws IOException {
        Read read = new Read();
        String fileName = Global.basePath+"农行2018年.xls";
        ArrayList<ArrayList<String >> rows = read.readRows(fileName);
        System.out.printf("读取成功！");
    }
}
