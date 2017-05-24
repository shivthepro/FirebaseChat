package com.full.firebasedatabase.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Shangeeth Sivan on 24/05/17.
 */

public class CommonUtil {

    public static String convertToFormattedTime(String pTimeInMillis){
        Calendar lCalendar = Calendar.getInstance();
        lCalendar.setTimeInMillis(Long.parseLong(pTimeInMillis));
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat("hh:mm a");
        return lSimpleDateFormat.format(lCalendar.getTime());
    }
}
