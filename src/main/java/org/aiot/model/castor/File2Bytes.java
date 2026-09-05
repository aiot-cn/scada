package org.aiot.model.castor;

import org.nutz.castor.Castor;
import org.nutz.lang.Files;

import java.io.File;


public class File2Bytes extends Castor<File, byte[]> {

    @Override
    public byte[] cast(File src, Class<?> toType, String... args) {

       return Files.readBytes(src);
    }

}
