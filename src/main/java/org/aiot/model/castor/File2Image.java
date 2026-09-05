package org.aiot.model.castor;

import org.nutz.castor.Castor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class File2Image extends Castor<File, BufferedImage> {

    @Override
    public BufferedImage cast(File src, Class<?> toType, String... args) {
        try {
            return ImageIO.read(src);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
