package org.aiot.model.castor;

import org.aiot.model.project.ArgBean;
import org.nutz.castor.Castor;
import org.nutz.lang.Strings;

/**
 * arg1|参数1|0:否,1:是|url
 */
public class String2ArgBean extends Castor<String, ArgBean> {

    protected boolean _isNull(String str) {
        return Strings.isBlank(str) || "null".equalsIgnoreCase(str);
    }

    @Override
    public ArgBean cast(String src, Class<?> toType, String... args) {
        if (_isNull(src)) {
            return null;
        }
        String[] a2 = src.split("\\|");
        ArgBean ab = new ArgBean(a2[0],Object.class);
        if(a2.length > 1)
            ab.setName(a2[1]);
        if(a2.length > 2)
            ab.setSelect(a2[2]);
        if(a2.length > 3)
            ab.setUrl(a2[3]);
       return ab;
    }

}
