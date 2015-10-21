package com.example.hiranakalab02.sunnote.common;

/**
 * Created by HiranakaLab02 on 2015/09/26.
 */
public class Preservation {
    private static int index;
    private static int recordCount = -1;

    public void setIndex(int index){
        this.index = index;
    }

    public static int getIndex(){
        return index;
    }

    public static int sunRecordCount(int i){
        recordCount += i;
        return recordCount;
    }

    public static int moonRecordCount(int i){
        recordCount += i;
        return recordCount;
    }
}
