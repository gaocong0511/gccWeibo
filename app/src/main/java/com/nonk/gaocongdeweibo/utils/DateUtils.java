package com.nonk.gaocongdeweibo.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by monk on 2018/1/30.
 */

public class DateUtils {
    public static final long ONE_MINUTE_MILLIONS = 60 * 1000;
    public static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
    public static final long ONE_DAY_MILLIONS = 24 * ONE_HOUR_MILLIONS;

    public static String getShortTime(String dateStr) {
        String str = "";

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        try {
            Date date = sdf.parse(dateStr);
            Date curDate = new Date();

            long durTime = curDate.getTime() - date.getTime();
            int dayStatus = calculateDayStatus(date, curDate);
            if (durTime <= 10 * ONE_MINUTE_MILLIONS) {
                str = "刚刚";
            } else if (durTime < ONE_HOUR_MILLIONS) {
                str = durTime / ONE_MINUTE_MILLIONS + "分钟前";
            } else if (dayStatus == 0) {
                str = durTime / ONE_HOUR_MILLIONS + "小时前";
            } else if (dayStatus == -1) {
                String.format(str = "昨天" + "HH:mm", date);
            } else if (isSameYear(date, curDate) && dayStatus < -1) {
                String.format(str = "MM-dd", date);
            }else {
                String.format(str="yyyy-MM",date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 判断两个日期是不是同一年
     *
     * @return 如果两个日期是同一年，返回true
     */
    public static boolean isSameYear(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int targetYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int compareYear = compareCalendar.get(Calendar.YEAR);
        return targetYear == compareYear;
    }

    /**
     * 计算目标日期与待比较日期的相差天数
     *
     * @param targetTime  目标日期
     * @param compareTime 待比较日期
     * @return 目标日期与待比较日期之间相差的天数
     */
    public static int calculateDayStatus(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int compareDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

        return tarDayOfYear - compareDayOfYear;
    }
}
