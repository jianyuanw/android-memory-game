package com.example.memorygame;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BestRecordReader {
    private List<Record> sortedBest5;
    File mTargetRecords;

    public BestRecordReader(File mTargetRecords) {
        sortedBest5 = new ArrayList<>();
        List<Record> allRecords = new ArrayList<>();
        this.mTargetRecords = mTargetRecords;
        try {
            FileInputStream fis = new FileInputStream(mTargetRecords);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));
            String strLine;
            while((strLine = br.readLine())!=null){
                String[] splits = strLine.split("\\+");
                allRecords.add(new Record(splits[0], Integer.parseInt(splits[1])));
            }
            dis.close();
            List<Record> sortedRecords = allRecords.stream().sorted(Comparator.comparing(Record::getTime)).collect(Collectors.toList());

            for(int i=0;i<5;i++) {
                sortedBest5.add(sortedRecords.get(i));
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public List<Record> getSortedBest5() {
        return sortedBest5;
    }

}

class Record {
    private String name;
    private int time;

    public Record() {
    }

    public Record(String name, int time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
