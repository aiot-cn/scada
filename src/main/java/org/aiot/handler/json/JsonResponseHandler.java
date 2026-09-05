package org.aiot.handler.json;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 
 * @author taojin
 *
 */
public class JsonResponseHandler extends JsonTypeHandler {

    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return false;
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj instanceof HttpServletResponse;
    }

    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        HttpServletResponse response = (HttpServletResponse) currentObj;
        r.writeRaw("\""+response+"\"");
    }

    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return null;
    }
}
