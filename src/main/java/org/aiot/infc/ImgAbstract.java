package org.aiot.infc;

import org.aiot.util.ImgUtil;
import org.aiot.util.OpenCVUtil;
import org.nutz.castor.Castors;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class ImgAbstract implements ImgInfc {
    protected Object img;
    protected int width;
    protected int height;

    @Override
    public byte[] getImgBytes(){
        if(img instanceof byte[])
            return (byte[])img;
        if(img instanceof BufferedImage)
            return ImgUtil.toBytes((BufferedImage) img);
        if(img instanceof Mat)
            return OpenCVUtil.toBytes((Mat) img);
        if(img instanceof String)
            img = Castors.me().castTo(img,File.class);
        try {
            if(img instanceof File)
                return Files.readAllBytes(((File) img).toPath());
        }catch (IOException ignored){

        }
        return null;
    };

    public Object getImg() {
        return img;
    }

    public void setImg(Object img) {
        this.img = img;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
