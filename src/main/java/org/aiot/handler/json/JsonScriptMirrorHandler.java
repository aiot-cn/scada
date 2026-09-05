package org.aiot.handler.json;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author taojin
 *
 */
public class JsonScriptMirrorHandler extends JsonTypeHandler {

    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return false;
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        if(obj instanceof ScriptObjectMirror){
            return ((ScriptObjectMirror)obj).isArray();
        }
        return false;
    }

    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        ScriptObjectMirror s = (ScriptObjectMirror) currentObj;
        List<Object> l = new ArrayList<>();
        s.forEach((k,v)->{
            l.add(v);
        });
        r.writeRaw(Json.toJson(l,jf));
    }

    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return null;
    }
}
