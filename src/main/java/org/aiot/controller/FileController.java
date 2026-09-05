package org.aiot.controller;

import org.aiot.main.Constants;
import org.aiot.model.DataRes;
import org.aiot.model.enums.VarRuntimeEnum;
import org.aiot.model.table.TFile;
import org.aiot.service.BaseService;
import org.aiot.util.FileUtil;
import org.aiot.util.ZipUtil;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipOutputStream;

@At("/file")
public class FileController {

    //下载文件
    @At("/download/*")
    public @Ok("raw:pdf") Object download(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        String pathName = req.getServletPath().substring(15);
        File file = new File(Constants.HOME_PATH,pathName);
        String fname = file.getName();
        if(!file.exists())
            throw Lang.makeThrow(fname + "资源不存在");
        if(file.isFile()){
            if(fname.endsWith(".pt") || fname.endsWith(".onnx")){
                String n = pathName.replace("/weights/best","");
                fname = n.substring(n.lastIndexOf("/")+1);
                TFile tFile = FileUtil.fileInfo(file);
                if(tFile.getDescription() != null)
                    resp.setHeader("station-file-dis", URLEncoder.encode(tFile.getDescription(),"UTF-8"));
            }
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fname,"UTF-8") + "\"");
            return file;
        }

        fname = URLEncoder.encode(file.getName(), "UTF-8")+".zip";
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fname + "\"");
        try (ZipOutputStream out = new ZipOutputStream(resp.getOutputStream())) {
            ZipUtil.doCompress(file, out);
        }
        return null;
    }

    //下载多个文件
    @At
    public @Ok("raw:pdf") Object downloads(String[] names,HttpServletResponse resp) throws IOException{
        if(names.length == 1){
            return new File(Constants.HOME_PATH,names[0]);
        }
        try (ZipOutputStream out = new ZipOutputStream(resp.getOutputStream())) {
            for(String n : names){
                File file = new File(Constants.HOME_PATH,n);
                if(file.isFile())
                    ZipUtil.doCompress(file, out);
            }
        }
        return null;
    }

    @At
    public @Ok("raw:pdf") File downDb(){
        String a = VarRuntimeEnum.dbUrl.val().toString().replace("jdbc:sqlite:","");
        String b = Constants.HOME_PATH+"/aiot_backup.db";
        File fb = new File(b);
        if(fb.isFile())
            Files.deleteFile(new File(b));
        Files.copy(new File(a),fb);
        return new File(b);
    }

    @At
    public @Ok("json") String[] getList(String path){
        if(path == null)
            path = "";
        String p = Constants.HOME_PATH + "/" +path;
        String[] s =  new File(p).list();
        return s;
    }

    @At
    @AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
    public @Ok("json") DataRes upload(File file, String path, String name) throws IOException {
        String pathName = path + "/" +name;
        File targetFile = new File( Constants.HOME_PATH + "/" + pathName);
        java.nio.file.Files.move(file.toPath(),targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Logs.get().infof("上传文件[%s]:%s <- %s",targetFile.isFile()?"成功":"失败",targetFile.getAbsolutePath(),file.getAbsolutePath());
        return DataRes.success(pathName);
    }

    @At
    public @Ok("json") void del(String name){
        File f = new File(Constants.HOME_PATH,name);
        if(f.isFile())
            Files.deleteFile(f);
        if(f.isDirectory())
            Files.deleteDir(f);
    }

    @At
    public @Ok("json") void rename(String name,String newName){
        String pathName = Constants.HOME_PATH +  "/" +name;
        Files.rename(new File(pathName),newName);
    }

    @At
    public @Ok("json") File create(String path,String name) throws IOException {
        File file = FileUtil.toFile(path,name);
        if(file.getName().contains(".")){
            Files.createNewFile(file);
        }else{
            Files.makeDir(file);
        }
        return file;
    }

    @At
    public @Ok("json") DataRes unzip(String name) throws IOException {
        int i = ZipUtil.unzip(FileUtil.toFile(name),null);
        return new DataRes(i);
    }

    @At
    public @Ok("json") TFile getInfo(String name){
        return FileUtil.fileInfo("",name);
    }
    @At
    public @Ok("json") void saveInfo(TFile tFile){
        BaseService bs = Constants.ioc.get(BaseService.class);
        bs.daoSave(tFile);
    }

}
