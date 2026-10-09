package org.aiot.util;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.file.Files;

public class SystemInfo {
    private static final OperatingSystemMXBean OS_BEAN = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
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

    /**
     * 系统整体 CPU 使用率，范围 0.0 ~ 1.0，不可用时返回 -1
     */
    public static double getSystemCpuUsage() {
        return OS_BEAN.getSystemCpuLoad();
    }

    /**
     * 当前 JVM 进程的 CPU 使用率，范围 0.0 ~ 1.0，不可用时返回 -1
     */
    public static double getProcessCpuUsage() {
        return OS_BEAN.getProcessCpuLoad();
    }

    /**
     * 物理内存使用率，范围 0.0 ~ 1.0
     */
    public static double getSystemMemoryUsage() {
        long total = OS_BEAN.getTotalPhysicalMemorySize();
        long free = OS_BEAN.getFreePhysicalMemorySize();
        if (total <= 0) {
            return -1;
        }
        return (double) (total - free) / total;
    }

    /**
     * JVM 堆内存使用率，范围 0.0 ~ 1.0
     */
    public static double getHeapMemoryUsage() {
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        long max = heap.getMax();
        if (max <= 0) {
            return -1;
        }
        return (double) heap.getUsed() / max;
    }

    public static long getHeapMemoryUsed() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    }

    public static long getHeapMemoryMax() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
    }

    /**
     * JVM 启动时间，毫秒时间戳
     */
    public static long getStartTime() {
        return ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    public static long getTotalPhysicalMemory() {
        return OS_BEAN.getTotalPhysicalMemorySize();
    }

    public static long getFreePhysicalMemory() {
        return OS_BEAN.getFreePhysicalMemorySize();
    }

    /**
     * 当前进程占用的物理内存（工作集），约等于任务管理器中显示的进程内存，
     * 除堆外还包含 Metaspace、线程栈、直接内存及原生库（SDK/OpenCV 等）的分配
     * @return 字节数，不可用时返回 -1
     */
    public static long getProcessMemoryUsed() {
        try{
            if(isWindows()){
                PsapiEx.PROCESS_MEMORY_COUNTERS pmc = new PsapiEx.PROCESS_MEMORY_COUNTERS();
                if(PsapiEx.INSTANCE.GetProcessMemoryInfo(Kernel32.INSTANCE.GetCurrentProcess(), pmc, pmc.size()))
                    return pmc.WorkingSetSize.longValue();
            }else if(isLinux()){
                ///proc/self/status 的 VmRSS 行，如 "VmRSS:\t  123456 kB"
                for(String line : Files.readAllLines(new File("/proc/self/status").toPath())){
                    if(line.startsWith("VmRSS:"))
                        return Long.parseLong(line.replaceAll("\\D", "")) * 1024;
                }
            }
        }catch(Throwable e){
            //任何异常均按不可用处理
        }
        return -1;
    }

    /**
     * 所有磁盘分区（Windows 下为 C:\、D:\ 等，Linux 下为 /）
     */
    public static File[] getDiskRoots() {
        return File.listRoots();
    }

    /**
     * 指定分区磁盘使用率，范围 0.0 ~ 1.0，分区不可用（如空光驱）返回 -1
     */
    public static double getDiskUsage(File root) {
        long total = root.getTotalSpace();
        if (total <= 0) {
            return -1;
        }
        return (double) (total - root.getFreeSpace()) / total;
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

    public static void main(String[] args) throws InterruptedException {
        // 关键：CPU 使用率是"区间统计值"，
        // 需要两次调用之间有采样间隔，第一次调用往往返回 0 或 -1
        getSystemCpuUsage();
        getProcessCpuUsage();
        Thread.sleep(1000);

        System.out.printf("系统CPU  : %.1f%%%n", getSystemCpuUsage() * 100);
        System.out.printf("进程CPU  : %.1f%%%n", getProcessCpuUsage() * 100);
        System.out.printf("物理内存  : %.1f%%  (已用 %.1f GB / 共 %.1f GB)%n",
                getSystemMemoryUsage() * 100,
                (getTotalPhysicalMemory() - getFreePhysicalMemory()) / 1024.0 / 1024 / 1024,
                getTotalPhysicalMemory() / 1024.0 / 1024 / 1024);
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        System.out.printf("JVM 堆内存: %.1f%%  (已用 %.1f GB / 共 %.1f GB)%n",
                getHeapMemoryUsage() * 100,
                heap.getUsed() / 1024.0 / 1024 / 1024,
                heap.getMax() / 1024.0 / 1024 / 1024);

        for (File root : getDiskRoots()) {
            long total = root.getTotalSpace();
            long free = root.getFreeSpace();
            if (total <= 0) {
                continue;
            }
            System.out.printf("磁盘 %s  : %.1f%%  (已用 %.1f GB / 共 %.1f GB)%n",
                    root.getPath(),
                    getDiskUsage(root) * 100,
                    (total - free) / 1024.0 / 1024 / 1024,
                    total / 1024.0 / 1024 / 1024);
        }
    }

    /**
     * Windows 下取本进程内存的 psapi 映射，JNA 5.6.0 未内置 GetProcessMemoryInfo
     */
    public interface PsapiEx extends StdCallLibrary {
        PsapiEx INSTANCE = Native.load("psapi", PsapiEx.class, W32APIOptions.DEFAULT_OPTIONS);

        boolean GetProcessMemoryInfo(WinNT.HANDLE process, PROCESS_MEMORY_COUNTERS counters, int cb);

        @Structure.FieldOrder({"cb", "PageFaultCount", "PeakWorkingSetSize", "WorkingSetSize",
                "QuotaPeakPagedPoolUsage", "QuotaPagedPoolUsage", "QuotaPeakNonPagedPoolUsage",
                "QuotaNonPagedPoolUsage", "PagefileUsage", "PeakPagefileUsage"})
        class PROCESS_MEMORY_COUNTERS extends Structure {
            public int cb;
            public int PageFaultCount;
            public BaseTSD.SIZE_T PeakWorkingSetSize;
            public BaseTSD.SIZE_T WorkingSetSize;
            public BaseTSD.SIZE_T QuotaPeakPagedPoolUsage;
            public BaseTSD.SIZE_T QuotaPagedPoolUsage;
            public BaseTSD.SIZE_T QuotaPeakNonPagedPoolUsage;
            public BaseTSD.SIZE_T QuotaNonPagedPoolUsage;
            public BaseTSD.SIZE_T PagefileUsage;
            public BaseTSD.SIZE_T PeakPagefileUsage;
        }
    }
}
