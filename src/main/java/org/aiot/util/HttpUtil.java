package org.aiot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.nutz.http.Request;
import org.nutz.http.sender.FilePostSender;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtil {
	/**
	 * 判断ajax请求
	 * @param request
	 * @return
	 */
	public static boolean isAjax(HttpServletRequest request){
		String accept = request.getHeader("Accept");
		if(accept != null && accept.contains("json"))
			return true;
	    return  (request.getHeader("X-Requested-With") != null  && "XMLHttpRequest".equalsIgnoreCase( request.getHeader("X-Requested-With").toString())) ;
	}

	public static boolean isMobile(HttpServletRequest request) {

		String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i" + "|windows (phone|ce)|blackberry"
				+ "|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp" + "|laystation portable)|nokia|fennec|htc[-_]"
				+ "|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
		String tableReg = "\\b(ipad|tablet|(Nexus 7)|up.browser" + "|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";

		Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);
		Pattern tablePat = Pattern.compile(tableReg, Pattern.CASE_INSENSITIVE);

		String userAgent = Strings.sBlank(request.getHeader("User-Agent"),"");

		Matcher matcherPhone = phonePat.matcher(userAgent);
		Matcher matcherTable = tablePat.matcher(userAgent);

		if(matcherPhone.find() || matcherTable.find()){
			try {
				String p = request.getServletPath();
				String s = "WEB-INF/mobile"+("/".equals(p) ? "/index" : p)+".jsp";
				URL url = request.getServletContext().getResource(s);
				if(url != null)
					return true;
				request.setAttribute("isMobile",true);
			} catch (MalformedURLException ignored) {

			}
		}
		return false;
	}
	
	/**
	 * 获取浏览器useragent
	 * @param request
	 * @return
	 */
	public static String getUseragent(HttpServletRequest request){
		return request.getHeader("user-agent");
	}
	
	/**
	 * 获得session中的值
	 * @param sessionKey
	 * @param request
	 * @return
	 */
	public static Object getSessionObject(String sessionKey,HttpServletRequest request){
		return request.getSession().getAttribute(sessionKey);
	}

	public static void addCookie(HttpServletResponse resp, String cookieName, String cookieVlaue, int expiry, String path){
		Cookie cookie = new Cookie(cookieName, cookieVlaue);
		cookie.setMaxAge(expiry);
		cookie.setPath(Strings.isBlank(path) ? Mvcs.getServletContext().getContextPath()+"/" : path);
		resp.addCookie(cookie);
	}

	public static void delCookie(HttpServletResponse resp,String path,String... cookieName){
		for(String name : cookieName){
			Cookie cookie = new Cookie(name, null);
			cookie.setMaxAge(0);
			cookie.setPath(Strings.isBlank(path) ? Mvcs.getServletContext().getContextPath()+"/" : path);
			resp.addCookie(cookie);
		}
	}

	public static String getCookie(HttpServletRequest req,String name){
		Cookie[] cookies = req.getCookies();
		for(Cookie cookie : cookies){
			if(cookie.getName().equals(name)){
				return cookie.getValue();
			}
		}
		return null;
	}

    public static NutMap getParameter(HttpServletRequest req){
		NutMap nm = new NutMap();
		Enumeration<String> paramNames = req.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String paramValue = req.getParameter(paramName);
			nm.put(paramName,paramValue);
		}
       return nm;
    }

	public static Map<String,String> getHeaders(HttpServletRequest req){
		Map<String,String> nm = new HashMap<>();
		Enumeration<String> names = req.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			nm.put(name,req.getHeader(name));
		}
		return nm;
	}

	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (ip != null && ip.length() > 15) {
			if (ip.indexOf(",") > 0) {
				ip = ip.substring(0, ip.indexOf(","));
			}
		}

		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}

		return ip;
	}

	public static NutMap getReqInfo(HttpServletRequest req){
		NutMap nm = new NutMap();
		nm.put("params",HttpUtil.getParameter(req));
		nm.put("headers", HttpUtil.getHeaders(req));
		nm.put("cookies", req.getCookies());
		nm.put("method", req.getMethod());
		nm.put("ip",HttpUtil.getIpAddress(req));
		return nm;
	}


	public static Map<String, Object> reqToMap(HttpServletRequest req){
		Map<String,Object> m = new HashMap<>();
		req.getParameterMap().forEach((k,v)-> m.put(k,v[0]));
		return m;
	}
    
    public static Document requestXml(String url,Map<String,String> m){
    	return Jsoup.parse(request(url,m));
    }
    
    public static JSONObject requestJson(String url,Map<String,String> m){
    	return JSON.parseObject(request(url,m));
    }

	public static String request(String url){
		return 	request(url,null);
	}

    public static String request(String url,Map<String,String> m){
    	Connection c = Jsoup.connect(url).method(Connection.Method.POST);
    	if(m != null)
    		c.data(m);
    	try {
    		c.ignoreContentType(true);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
			Response r = c.execute();
			return r.body();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }

	public static String uploadFile(String url,NutMap nm){
		Request req = Request.create(url, Request.METHOD.POST);
		req.getParams().putAll(nm);
		FilePostSender sender = new FilePostSender(req);
		org.nutz.http.Response resp = sender.send();
		return resp.getContent();
	}

	public static File downloadFile(String downloadUrl,File saveFile, Map<String,String> m) {
		System.out.println("开始下载：" + downloadUrl);

		try {
			// 创建一个URL对象
			URL url = new URL(downloadUrl);
			// 打开一个连接
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpConn.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
			httpConn.setDoOutput(true);

			if(m != null){
				StringBuilder sb = new StringBuilder();
				m.forEach((k,v)-> sb.append("&").append(k).append("=").append(v));
				// 发送POST请求
				try (OutputStream os = httpConn.getOutputStream()) {
					byte[] input = sb.substring(1).getBytes(StandardCharsets.UTF_8);
					os.write(input, 0, input.length);
				}
			}

			int responseCode = httpConn.getResponseCode();

			// 检查响应码是否为HTTP OK状态
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// 获取输入流

				InputStream inputStream = httpConn.getInputStream();
				/*String fileDis = httpConn.getHeaderField("station-file-dis");
				if(fileDis != null)
					tFile.setDescription(URLDecoder.decode(fileDis,"UTF-8"));*/
				if(saveFile.isDirectory()){
					String conDis = httpConn.getHeaderField("Content-Disposition");
					int start = conDis.indexOf("filename=") + 10;

					String filename = conDis.substring(start,conDis.indexOf("\"",start));
					filename = URLDecoder.decode(filename,"UTF-8");
					saveFile = new File(saveFile,filename);
				}
				Files.createDirIfNoExists(saveFile.getParent());
				// 创建文件输出流
				FileOutputStream outputStream = new FileOutputStream(saveFile);

				int bytesRead;
				byte[] buffer = new byte[4096];
				// 从输入流读取数据并写入到输出流中
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				// 关闭流
				outputStream.close();
				inputStream.close();
			} else {
				System.out.println("下载错误，HTTP响应码：" + responseCode + " <- " + url);
			}
			httpConn.disconnect();
			System.out.println("下载完成：" + saveFile.getAbsolutePath() + " " + (saveFile.length()/1024) + "kb");
			return saveFile;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
