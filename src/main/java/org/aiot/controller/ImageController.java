package org.aiot.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.aiot.infc.ImgAbstract;
import org.aiot.infc.ImgInfc;
import org.aiot.infc.device.DeviceInfc;
import org.aiot.lang.Cache;
import org.aiot.main.Constants;
import org.aiot.model.DataRes;
import org.aiot.model.enums.ConfigEnum;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.model.lang.Target;
import org.aiot.model.table.TDevice;
import org.aiot.service.DeviceService;
import org.aiot.util.*;
import org.nutz.img.Images;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.mvc.view.RawView;
import org.nutz.mvc.view.UTF8JsonView;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

import static org.aiot.main.Constants.ioc;

@At("/image")
public class ImageController {

    @At("/*")
    public void img(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
        String pathName = req.getServletPath().substring(6);
        String width = req.getParameter("width");
        String height = req.getParameter("height");
        String target = req.getParameter("target");
        boolean thumbnail = Boolean.parseBoolean(req.getParameter("thumbnail"));

        if(pathName.endsWith(".workflow") || pathName.endsWith(".cache") ||
           pathName.endsWith(".chain") || pathName.endsWith(".point")){
            Object o = CommonUtil.getUri(pathName);
            if(o instanceof ImgInfc){
                o = ((ImgInfc)o).getImgBytes();
            }
            if(o instanceof Mat){
                o = OpenCVUtil.toBytes((Mat) o);
            }
            new RawView("jpg").render(req,resp,o);
            return;
        }
        File file = new File(Constants.HOME_PATH , pathName);
        if(!file.isFile()){
            file = new File(pathName);
        }

        if(pathName.endsWith(".mp4")){
            File f2 = new File(file.getAbsolutePath()+".jpg");
            if(!f2.isFile()){
                ImgUtil.readVideo(file,(bi, i)->{
                    BufferedImage b2 = Images.zoomScale(bi,-1,128);
                    ImgUtil.write(b2,f2);
                    return true;
                });
            }

            new RawView("jpg").render(req,resp,ImgUtil.read(f2));
            return;
        }

        int w = 0,h = 0;

        if(width != null)
            w = Integer.parseInt(width);

        if(height != null)
            h = Integer.parseInt(height);

        BufferedImage buf;
        String ifNoneMath = req.getHeader("If-None-Match");
        long etag = file.lastModified();
        if(Strings.equals(ifNoneMath,etag+"")){
            new HttpStatusView(304).render(req,resp,null);
            return;
        }

        //resp.setHeader("Cache-Control","max-age="+(90*24*60*60));
        if(thumbnail && file.length() > 200 * 1024){
            File f2 = new File(file.getParent() + "/thumbnail/" + w + "/" + file.getName());
            if(!f2.isFile() || f2.lastModified() != etag){
                buf =  ImgUtil.fileToBufImg(file, w, h);
                Files.createNewFile(f2);
                Images.write(buf,f2);
                f2.setLastModified(etag);
            }else{
                buf = ImageIO.read(f2);
            }
        }else{
            buf =  ImgUtil.fileToBufImg(file, w, h);
        }

        if(Strings.isNotBlank(target)){
            buf = ImgUtil.crop(buf,new Target(target));
        }

        resp.setHeader("Etag",etag+"");

        String suffix = pathName.substring(pathName.lastIndexOf(".")+1);
        new RawView(suffix.toLowerCase()).render(req,resp,buf);

    }

    @At
    public @Ok("raw:jpg") BufferedImage QRCode(String content) throws Exception {
        Hashtable<EncodeHintType,Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    @At
    public @Ok("raw") String crop(String uri,@Param("target")Target target){
        String to = "imgCrop/"+System.currentTimeMillis()+".cache";
        Object img = CommonUtil.getUri(uri);
        Mat mat = OpenCVUtil.crop(img,target);
        Cache.putToUri(to,mat);
        return to;
    }

    //================================================模板匹配==========================================================
    @At

    public @Ok("json")
    RecognitionRes featureTarget(@Param("rec")RecognitionRes rec, String uri2,
                                 @Param("hint") SiftUtil.SiftHint hint){
        RecognitionRes res =  SiftUtil.featureTarget(rec,uri2, hint);
        System.out.println("SIFT:"+res.getRemark());
        return res;
    }

}
