package org.aiot.model.castor;

import org.aiot.main.Constants;
import org.nutz.castor.Castor;
import org.nutz.lang.Strings;

import java.io.File;


public class String2File extends Castor<String, File> {

    @Override
    public File cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src)) {
            return null;
        }
        File file = new File(src);
        if(file.isFile() || file.isDirectory())
            return file;

       return new File(Constants.HOME_PATH,src);
    }

}
