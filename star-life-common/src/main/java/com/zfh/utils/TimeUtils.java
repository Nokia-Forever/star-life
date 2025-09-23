package com.zfh.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 */
public class TimeUtils {
    public static boolean isInTimeRange(LocalTime time, String timeRange) {
        // 分割开始和结束时间
        String[] parts = timeRange.split("-");
        if (parts.length != 2) throw new IllegalArgumentException("无效时间段格式");

        // 解析时间（支持 HH:mm 和 HH:mm:ss 格式）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H[:][m][:s]");
        LocalTime start = LocalTime.parse(parts[0].trim(), formatter);
        LocalTime end = LocalTime.parse(parts[1].trim(), formatter);

        // 处理跨午夜时段（如 22:00 到次日 09:00）
        if (start.isAfter(end)) {
            return time.isAfter(start) || time.isBefore(end);
        }
        // 处理正常时段
        else {
            return !time.isBefore(start) && !time.isAfter(end);
        }
    }
}
