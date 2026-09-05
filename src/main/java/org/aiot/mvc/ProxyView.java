package org.aiot.mvc;

import org.aiot.util.SysUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public class ProxyView implements View{

    String url;
    String script;

    public ProxyView(String url) {
        this.url = url;
    }

    public ProxyView setScript(String script){
        this.script = script;
        return this;
    }

    @Override
    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
        String contentType = req.getContentType();
        Connection connection = Jsoup.connect(url).method(Connection.Method.POST).ignoreContentType(true);
        if(contentType == null || contentType.contains("application/x-www-form-urlencoded")){
            //设置参数
            Enumeration<String> paramNames = req.getParameterNames();
            while (paramNames.hasMoreElements()){
                String paramName = paramNames.nextElement();
                connection.data(paramName,req.getParameter(paramName));
            }
        }else if(contentType.contains("application/json")){
            connection.requestBody(Lang.readAll(Streams.utf8r(req.getInputStream())));
        }
        //请求前置操作
        SysUtil.scriptByName(script,req,resp,connection);
        Connection.Response response = connection.execute();
        Object o = SysUtil.scriptByName(script,req,resp,response);
        if(o != null){
            UTF8JsonView.COMPACT.render(req,resp,o);
        }else{
            //text/html;charset=UTF-8
            //application/json;charset=UTF-8
            resp.setContentType(response.contentType());
            resp.getWriter().write(response.body());
        }
    }

}
