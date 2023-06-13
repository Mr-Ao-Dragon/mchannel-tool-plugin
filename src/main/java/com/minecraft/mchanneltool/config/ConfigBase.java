package com.minecraft.mchanneltool.config;

public class ConfigBase {
    public static String wsUri = "";

    public static String hint = "";

    private static String separator = null;

    /**
     * 分隔符
     *
     * @return
     */
    static public String SEPARATOR() {
        if (separator == null) {
            String os = System.getProperty("os.name");
            //Windows操作系统
            if (os != null && os.toLowerCase().startsWith("windows")) {
                separator = "\\";
            } else if (os != null && os.toLowerCase().startsWith("linux")) {//Linux操作系统
                separator = "/";
            } else { //其它操作系统
                separator = "/";
            }
        }
        return separator;
    }
}
