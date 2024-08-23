package com.hui.app.utils;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static String fillInNumberWithDigits(int digits, String oldStr) {
        // Check the length of the original string
        int oldLength = oldStr.length();

        // If the length is already satisfied or exceeds the specified digits, return the original string
        if (oldLength >= digits) {
            return oldStr;
        }

        // Calculate the number of padding characters needed
        int paddingCount = digits - oldLength;

        // Create a padding string with zeros
        String paddingString = new String(new char[paddingCount]).replace('\0', '0');

        // Concatenate the padding string and the original string
        String newString = paddingString + oldStr;

        return newString;
    }

    // 获取时分秒
    public static String getHourMinutes() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String currentTime = formatter.format(now);
        System.out.println("当前时间：" + currentTime);
        return currentTime;
    }

    // 获取当前年月日
    public static String getDay() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateStr = formatter.format(now);
        System.out.println("当前年月日：" + currentDateStr);
        return currentDateStr;
    }
}
