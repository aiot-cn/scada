package org.aiot.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.nutz.lang.Strings;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    private ZipUtil(){
    }

    public static void doCompress(String srcFile, String zipFile) throws IOException {
        doCompress(new File(srcFile), new File(zipFile));
    }

    /**
     * 文件压缩
     * @param srcFile 目录或者单个文件
     * @param zipFile 压缩后的ZIP文件
     */
    public static void doCompress(File srcFile, File zipFile) throws IOException {
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(zipFile));
            doCompress(srcFile, out);
        } catch (Exception e) {
            throw e;
        } finally {
            out.close();//记得关闭资源
        }
    }

    public static void doCompress(String filelName, ZipOutputStream out) throws IOException{
        doCompress(new File(filelName), out);
    }

    public static void doCompress(File file, ZipOutputStream out) throws IOException{
        doCompress(file, out, "");
    }

    public static void doCompress(File inFile, ZipOutputStream out, String dir) throws IOException {
        if ( inFile.isDirectory() ) {
            File[] files = inFile.listFiles();
            if (files!=null && files.length>0) {
                for (File file : files) {
                    String name = inFile.getName();
                    if (!"".equals(dir)) {
                        name = dir + "/" + name;
                    }
                    ZipUtil.doCompress(file, out, name);
                }
            }
        } else {
            ZipUtil.doZip(inFile, out, dir);
        }
    }

    public static void doZip(File inFile, ZipOutputStream out, String dir) throws IOException {
        String entryName = null;
        if (!"".equals(dir)) {
            entryName = dir + "/" + inFile.getName();
        } else {
            entryName = inFile.getName();
        }
        ZipEntry entry = new ZipEntry(entryName);
        out.putNextEntry(entry);

        int len = 0 ;
        byte[] buffer = new byte[1024];
        FileInputStream fis = new FileInputStream(inFile);
        while ((len = fis.read(buffer)) > 0) {
            out.write(buffer, 0, len);
            out.flush();
        }
        out.closeEntry();
        fis.close();
    }

    public static void addZip(String pathName,byte[] bytes,ZipOutputStream out) throws IOException {
        ZipEntry entry = new ZipEntry(pathName);
        out.putNextEntry(entry);
        out.write(bytes);
        out.flush();
        out.closeEntry();
    }

    public static int unzip(File zip) throws IOException{
        return unzip(zip,zip.getParentFile());
    }


    public static int unzip(File zip,File destDir) throws IOException {
        System.out.println("开始解压："+zip.getName() +" -> " + destDir.getAbsolutePath());
        int i = 0;
        byte[] buffer = new byte[4096];
        List<ZipArchiveEntry> dirs = new ArrayList<>();
        org.apache.commons.compress.archivers.zip.ZipFile zipFile = new org.apache.commons.compress.archivers.zip.ZipFile(zip);
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();
            File outputFile = new File(destDir,entry.getName());

            if (entry.isDirectory()) {
                outputFile.mkdirs();
                dirs.add(entry);
            } else {
                i++;
                if (!outputFile.getParentFile().exists()) {
                    outputFile.getParentFile().mkdirs();
                }

                try (InputStream inputStream = zipFile.getInputStream(entry);
                     FileOutputStream fos = new FileOutputStream(outputFile)){
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (entry.getLastModifiedDate() != null) {
                    outputFile.setLastModified(entry.getLastModifiedDate().getTime());
                }

                int unixMode = entry.getUnixMode();
                if (unixMode != 0) {
                    outputFile.setExecutable((unixMode & 0111) != 0);
                }
            }
        }

        // 文件写入会覆盖父目录时间，最后再设目录时间
        for (ZipArchiveEntry entry : dirs) {
            if (entry.getLastModifiedDate() != null) {
                new File(destDir, entry.getName()).setLastModified(entry.getLastModifiedDate().getTime());
            }
        }

        return i;
    }


    public static void zipFiles(List<String> filePaths, String zipFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (String filePath : filePaths) {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    addToZipFile(file, file.getName(), zos);
                } else {
                    System.err.println("File not found: " + filePath);
                }
            }

            zos.closeEntry(); // 确保关闭最后一个条目的流
        }
    }

    private static void addToZipFile(File file, String entryName, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);

            byte[] bytesIn = new byte[1024];
            int read = 0;
            while ((read = bis.read(bytesIn)) != -1) {
                zos.write(bytesIn, 0, read);
            }

            zos.closeEntry();
        }
    }


    public static byte[] mergeZipFiles(byte[] zipBytes1, byte[] zipBytes2) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        Set<String> entryNames = new HashSet<>(); // 用于存储已添加的条目名称

        // 创建一个临时的ZIP文件来读取第一个ZIP内容
        File tempZip1 = File.createTempFile("zip1", ".zip");
        Files.write(tempZip1.toPath(), zipBytes1);
        addZipFileToZipOutputStream(zos, tempZip1, entryNames);

        // 创建一个临时的ZIP文件来读取第二个ZIP内容
        File tempZip2 = File.createTempFile("zip2", ".zip");
        Files.write(tempZip2.toPath(), zipBytes2);
        addZipFileToZipOutputStream(zos, tempZip2, entryNames);

        zos.close();
        byte[] mergedZipBytes = baos.toByteArray();

        // 清理临时文件
        tempZip1.delete();
        tempZip2.delete();

        return mergedZipBytes;
    }

    private static void addZipFileToZipOutputStream(ZipOutputStream zos, File zipFile, Set<String> entryNames) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                InputStream is = zip.getInputStream(entry);

                String name = entry.getName();
                // 检查条目名称是否已存在
                if (entryNames.contains(name)) {
                    // 如果已存在，则添加序号
                    int count = 1;
                    String baseName = File.separatorChar == '/' ? name : name.replace('\\', '/'); // 确保跨平台兼容性
                    String extension = org.nutz.lang.Files.getSuffixName(baseName); // 假设你有一个工具类来获取文件扩展名
                    String base = org.nutz.lang.Files.getMajorName(baseName); // 假设你有一个工具类来获取文件名（不含路径）

                    // 查找不重复的条目名称
                    do {
                        name = base + "_" + count + "." + extension;
                        count++;
                    } while (entryNames.contains(name));
                }

                // 将修改后的名称设置为新的ZipEntry
                ZipEntry newEntry = new ZipEntry(name);
                entryNames.add(name);
                zos.putNextEntry(newEntry);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                is.close();
            }
        }
    }

}
