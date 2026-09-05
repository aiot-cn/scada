package org.aiot.handler.json;

import org.aiot.util.FileUtil;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 
 * @author taojin
 *
 */
public class JsonFileHandler extends JsonTypeHandler {

    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return false;
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj instanceof File;
    }

    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        File file = (File) currentObj;
        NutMap nm = new NutMap("path", FileUtil.toPath(file));
        if(file.isFile()){
            double kb = file.length()/102.4d;
            nm.put("size",Math.round(kb)/10.0);
            nm.put("date",new Date(file.lastModified()));
        }
        r.writeRaw(Json.toJson(nm,jf));
    }

    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return null;
    }
}
