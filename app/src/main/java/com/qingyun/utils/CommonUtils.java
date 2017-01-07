package com.qingyun.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者： qingyun on 17/1/7.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class CommonUtils {

    public static String dateDifferent(long dateTime) {
        String result = "";
        Date d1 = new Date(dateTime);
        Date d2 = new Date();
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            result = format.format(d1);
            long diff = d2.getTime() - d1.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            if (diffDays < 1) {
                if (diffHours < 1) {
                    if (diffMinutes < 1) {
                        result = "刚刚";
                    } else {
                        result = diffMinutes + "分钟前";
                    }
                } else {
                    result = diffHours + "小时前";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
