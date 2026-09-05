package org.aiot.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.aiot.main.Constants;
import org.aiot.model.table.TFile;
import org.aiot.service.BaseService;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.nutz.dao.Cnd;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Logs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

/**
 * 文件读取工具类
 */
public class FileUtil {

    public static List<File> getFiles(File dir,Predicate<File> predicate){
        List<File> fileList = new ArrayList<>();

        // 获取目录中的所有文件和子目录
        File[] files = dir.listFiles();
        if (files == null) {
            return fileList; // 如果目录为空或不可读，返回空列表
        }
        for (File file : files) {
            if (file.isDirectory()) {
                //默认不添加目录
                if(predicate != null && predicate.test(file))
                    fileList.add(file);
                // 如果是子目录，递归调用
                fileList.addAll(getFiles(file,predicate));
            } else if (predicate == null || predicate.test(file)) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    public static List<File> getFilesBySuffix(File dir,String suffix){
        return getFiles(dir,v->v.getName().endsWith(suffix));
    }

    public static String toPath(File file){
        // 替换为 \ 需要使用 Matcher.quoteReplacement(File.separator)
        String path = file.getAbsolutePath();
        if(path.startsWith(Constants.HOME_PATH))
            path = path.substring(Constants.HOME_PATH.length());
        return path.replaceAll("\\\\","/");
    }

    public static File toFile(String pathName){
        return new File(Constants.HOME_PATH,pathName);
    }

    public static File toFile(String path,String name){
        return new File(Constants.HOME_PATH + "/" + Strings.sBlank(path,""),name);
    }

    public static TFile fileInfo(String path, String name){
        return fileInfo(toFile(path,name));
    }

    public static TFile fileInfo(File file){

        String pathName = toPath(file);
        BaseService bs = Constants.ioc.get(BaseService.class);
        TFile tFile = bs.daoFetch(TFile.class, Cnd.where("pathName","=",pathName));
        String md5 = null;
        if(tFile == null){
            md5 = Lang.md5(file);
            tFile = bs.daoFetch(TFile.class, Cnd.where("md5","=",md5));
        }
        if(tFile == null)
            tFile = new TFile();
        if(Strings.isBlank(tFile.getMd5()))
            tFile.setMd5(md5);
        tFile.setPathName(pathName);
        tFile.setSize(file.length()/1024f);
        tFile.setCreateDate(new Date(file.lastModified()));

        int i = file.getName().lastIndexOf(".");
        if(i > -1)
            tFile.setType(file.getName().substring(i+1).toLowerCase());

        return tFile;
    }

    /**
     * @param path 		文件路径
     * @param suffix 	后缀名, 为空则表示所有文件
     * @param isdepth 	是否遍历子目录
     * @return list
     */
    public static List<String> getListFiles(String path, String suffix, boolean isdepth) {
        List<String> lstFileNames = new ArrayList<String>();
        File file = new File(path);
        return listFile(lstFileNames, file, suffix, isdepth);
    }

    private static List<String> listFile(List<String> lstFileNames, File f, String suffix, boolean isdepth) {
        // 若是目录, 采用递归的方法遍历子目录
        if (f.isDirectory()) {
            File[] t = f.listFiles();

            for (int i = 0; i < t.length; i++) {
                if (isdepth || t[i].isFile()) {
                    listFile(lstFileNames, t[i], suffix, isdepth);
                }
            }
        } else {
            String filePath = f.getAbsolutePath();
            if (!suffix.equals("")) {
                int begIndex = filePath.lastIndexOf("."); // 最后一个.(即后缀名前面的.)的索引
                String tempsuffix = "";

                if (begIndex != -1) {
                    tempsuffix = filePath.substring(begIndex + 1, filePath.length());
                    if (tempsuffix.equals(suffix)) {
                        lstFileNames.add(filePath);
                    }
                }
            } else {
                lstFileNames.add(filePath);
            }
        }
        return lstFileNames;
    }

    public static String bmp2Jpeg(String filePath, String outPath) {
        try {
            long start = System.currentTimeMillis();
            File file = new File(filePath);
            Image img = ImageIO.read(file);
            BufferedImage tag = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(img.getScaledInstance(img.getWidth(null), img.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);
            FileOutputStream out = new FileOutputStream(outPath);
            // JPEGImageEncoder可适用于其他图片类型的转换
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag);
            out.close();
            Logs.get().info("bmp 转 JPEG，共耗时：  " + (System.currentTimeMillis() - start) + " 毫秒");
            return outPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outPath;
    }

    public static void setStationProp(String key,String value){
        try {
            //还是原生的 Properties 好用，只是 =# 等字符会加斜杠转义符
            Constants.propStation.put(key,value);
            Writer writer = Streams.utf8w(new FileOutputStream(Constants.propPath));
            Constants.propStation.store(writer, "station");
            Streams.safeClose(writer);
            /*MultiLineProperties prop = new MultiLineProperties(Streams.utf8r(new FileInputStream(Constants.propPath)));
            prop.put(key,value);
            prop.print(Streams.utf8w(new FileOutputStream(Constants.propPath)));*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //文件路径转存
    public static String fileMove(String file,String targetPath){
        File f = new File(file);
        if(!f.isFile())
            return null;
        String f2 = targetPath + "/" + System.currentTimeMillis() + file.substring(file.lastIndexOf("."));
        File f3 = new File(Constants.HOME_PATH+"/"+f2);
        try {
            Files.move(f,f3);
        }catch (IOException e){
            e.printStackTrace();
        }
        return f2;
    }

    public static boolean isSuffix(String name,String suffix){
        int i = name.lastIndexOf(".");
        if(i == -1)
            return false;
        return name.substring(i+1).equals(suffix);
    }

    /**
     * Excel文件转Array
     * @param file
     * @param sheetNumber
     * @return
     */
    public static String[][] excelToArr(File file,int sheetNumber) {
        String[][] rows = null;
        try(Workbook workbook = WorkbookFactory.create(file)) {
            // 工作表
            Sheet sheet = workbook.getSheetAt(sheetNumber);

            // 行数
            int rowNumbers = sheet.getLastRowNum() + 1;

            rows = new String[rowNumbers][];
            int cellNum = 0;
            // 读数据
            for (int row = 0; row < rowNumbers; row++){
                Row r = sheet.getRow(row);
                cellNum = Math.max(r.getLastCellNum(),cellNum) ;
                String[] cells = new String[cellNum];
                for (int i = 0; i < cellNum; i++) {
                    Cell cell = r.getCell(i);
                    if(cell != null){
                        try {
                            cells[i] = cell.getStringCellValue();
                        }catch (Exception e){
                            cells[i] = cell + "";
                        }
                    }
                    //System.out.println(cells[i]);
                }
                rows[row] = cells;
            }

            return rows;

        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
        }

        return rows;


    }

    
}
