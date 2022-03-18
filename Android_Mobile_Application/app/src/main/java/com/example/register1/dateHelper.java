package com.example.register1;

import java.util.Calendar;
import java.util.Date;

public class dateHelper {

    public static Date getDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        month = month - 1; // months are indexed on 0

        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();

        //explanation: https://www.youtube.com/watch?v=SxMElhlhcDI
    }
}
