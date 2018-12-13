package com.example.admin.bdplayer.util;

public class PlayerHelper {
    public static String convertFileSize(long var0) {
        long var3 = 1024L * 1024L;
        long var5 = var3 * 1024L;
        if (var0 >= var5) {
            return String.format("%.1f GB", (float)var0 / (float)var5);
        } else {
            float var2;
            String var7;
            if (var0 >= var3) {
                var2 = (float)var0 / (float)var3;
                if (var2 > 100.0F) {
                    var7 = "%.0f M";
                } else {
                    var7 = "%.1f M";
                }

                return String.format(var7, var2);
            } else if (var0 >= 1024L) {
                var2 = (float)var0 / (float)1024L;
                if (var2 > 100.0F) {
                    var7 = "%.0f K";
                } else {
                    var7 = "%.1f K";
                }

                return String.format(var7, var2);
            } else {
                return String.format("%d B", var0);
            }
        }
    }
}
