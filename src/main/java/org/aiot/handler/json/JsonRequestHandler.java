package org.aiot.handler.json;

import org.aiot.util.FileUtil;
import org.nutz.http.Http;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Dumps;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 
 * @author taojin
 *
 */
public class JsonRequestHandler extends JsonTypeHandler {

    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return false;
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj instanceof HttpServletRequest;
    }

    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        HttpServletRequest req = (HttpServletRequest) currentObj;
        r.writeRaw("\""+req+"\"");
    }

    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return null;
    }
}
