package org.aiot.handler.json;

import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Mirror;

import java.io.IOException;

/**
 * 
 * @author taojin
 *
 */
public class JsonBytesHandler extends JsonTypeHandler {

    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return false;
    }

    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return obj instanceof byte[];
    }

    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        byte[] bytes = (byte[]) currentObj;
        StringBuilder s = new StringBuilder();
        if(bytes.length > 200){
            for(int i=0;i<100;i++){
                s.append(String.format("%02x ", bytes[i]));
            }
            s.append("...").append(s.length()).append("...");
            for(int i=bytes.length-100;i<bytes.length;i++){
                s.append(String.format("%02x ", bytes[i]));
            }
        }else{
            for(byte b : bytes){
                s.append(String.format("%02x ", b));
            }
        }
        r.writeRaw("\"["+ s.substring(0,s.length()-1).toUpperCase() +"]\"");
    }

    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return null;
    }
}
