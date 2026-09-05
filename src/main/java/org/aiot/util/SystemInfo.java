package org.aiot.util;

public class SystemInfo {
    /**
     * windows 10
     */
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    /**
     * amd64
     */
    private static final String OS_ARCH = System.getProperty("os.arch").toLowerCase();

    /**
     * 获取操作系统类型
     * windows、linux、osx
     */
    public static String getOsType() {
        if(isLinux())
            return "linux";
        if(isMac())
            return "osx";
        return "windows";
    }

    /**
     * 获取CPU架构
     * x86_32,x86_64,arm32,arm64
     */
    public static String getOsArch() {
        if(isArm64())
            return "arm64";
        if(isX64())
            return "x86_64";
        if(isArm32())
            return "arm32";
        return "x86_32";
    }

    public static boolean isWindows() {
        return OS_NAME.contains("win");
    }

    public static boolean isLinux() {
        return OS_NAME.contains("nix") || OS_NAME.contains("nux") || OS_NAME.contains("aix");
    }

    public static boolean isMac() {
        return OS_NAME.contains("mac") || OS_NAME.contains("darwin");
    }

    public static boolean isX64() {
        return "x86_64".equals(OS_ARCH) || "amd64".equals(OS_ARCH);
    }

    public static boolean isX86() {
        return !isX64() && OS_ARCH.contains("x86");
    }

    public static boolean isArm64() {
        return "aarch64".equals(OS_ARCH) || "arm64".equals(OS_ARCH);
    }

    public static boolean isArm32() {
        return !isArm64() && OS_ARCH.contains("arm");
    }
}
