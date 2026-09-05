package org.aiot.model.castor;

import org.nutz.castor.Castor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;


public class Bytes2Image extends Castor<byte[], BufferedImage> {

    @Override
    public BufferedImage cast(byte[] src, Class<?> toType, String... args) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(src);
            return ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
