package com.sucker.demo.excel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestEasyExcel {

    public static void main(String[] args) {
        //实现excel写的操作
        //1. 设置写入文件夹地址和名称
//        String filename = "D:\\123.xlsx";

        //2. 调用easyExcel里面的方法实现写操作
        //write 方法两个参数分别为文件路径名称和实体类class
//        EasyExcel.write(filename,DemoData.class).sheet("学生列表").doWrite(getData());


        //实现excel读操作
        String filename = "D:\\123.xlsx";

        EasyExcel.read(filename,DemoData.class,new ExcelListener()).sheet().doRead();

    }

    //创建方法返回list 集合
    private static List<DemoData> getData(){
        List<DemoData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoData demoData = new DemoData();
            demoData.setSno(i);
            demoData.setSname("lucy"+i);
            list.add(demoData);
        }
        return list;
    }

}
