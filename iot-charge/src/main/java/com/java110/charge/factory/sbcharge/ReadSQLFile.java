package com.java110.charge.factory.sbcharge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadSQLFile {

    public static void main(String[] args) {
        String filePath = "G:\\客户数据库备份\\binlog9.sql";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int i = 1;
            while((line = reader.readLine()) != null) {
                if (line.startsWith("insert into sys_file values")){
                    i ++;
                    System.out.println(line+";");
                }
            }
            System.out.println(i);
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

}
