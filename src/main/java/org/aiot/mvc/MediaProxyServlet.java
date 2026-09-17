package org.aiot.mvc;

import org.aiot.model.enums.PathEnum;
import org.aiot.util.IniParser;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * 类似nginx的反向代理 把挂载路径(默认/media/*)之后的路径原样转发到流媒体服务(ZLMediaKit)的http端口
 * 如 /media/live/1.live.flv -> http://127.0.0.1:8079/live/1.live.flv
 *    /media/ffmpeg/2/hls.m3u8 -> http://127.0.0.1:8079/ffmpeg/2/hls.m3u8
 * 挂载路径在web.xml的servlet-mapping里改 这里不感知
 * FLV直播是持续输出的无限流 必须边读边写并flush 不能先缓冲完整响应
 * 每个观看中的直播连接会占用一个tomcat工作线程 播放器断开后自动释放
 *
 * web.xml里可用init-param覆盖默认目标:
 * host 默认127.0.0.1
 * port 默认读lib/ZLMediaKit/config.ini的[http]port 都没有则8079
 */
public class MediaProxyServlet extends HttpServlet {

	private static final Log log = Logs.get();

	/** 请求方向的逐跳头和压缩头不转发 Host由HttpURLConnection按目标地址生成 */
	private static final Set<String> SKIP_REQUEST_HEADERS = new HashSet<>(Arrays.asList(
			"host", "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
			"te", "trailer", "transfer-encoding", "upgrade", "content-length", "accept-encoding"));

	/** 响应方向只跳过逐跳头 Content-Length原样透传(ts/m3u8等有限响应) 无长度的由容器自动chunked */
	private static final Set<String> SKIP_RESPONSE_HEADERS = new HashSet<>(Arrays.asList(
			"connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
			"te", "trailer", "transfer-encoding", "upgrade"));

	private String host = "127.0.0.1";
	private int port = 8079;

	@Override
	public void init() throws ServletException {
		String h = getInitParameter("host");
		if (Strings.isNotBlank(h)) {
			host = h.trim();
		}
		String p = getInitParameter("port");
		if (Strings.isNotBlank(p)) {
			port = Integer.parseInt(p.trim());
		} else {
			//与ZLMediaKit.init()保持一致 从config.ini读取http端口
			File conf = new File(PathEnum.lib.getFile("ZLMediaKit"), "config.ini");
			if (conf.isFile()) {
				port = new IniParser(conf).getInteger("http", "port", port);
			}
		}
		log.infof("media proxy: %s -> http://%s:%s/{path}", getServletName() + "/*", host, port);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//挂载点之后的路径 如/live/1.live.flv 直接访问挂载根时为/
		String path = req.getPathInfo();
		if (path == null || path.isEmpty()) {
			path = "/";
		}
		if (req.getQueryString() != null) {
			path += "?" + req.getQueryString();
		}

		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL("http", host, port, path).openConnection();
			conn.setRequestMethod(req.getMethod());
			conn.setConnectTimeout(3000);
			//不设readTimeout 直播流空闲时可能长时间无数据
			copyRequestHeaders(req, conn);

			//请求体透传(取流都是GET 保险起见支持POST)
			if (req.getContentLengthLong() > 0) {
				conn.setDoOutput(true);
				copy(req.getInputStream(), conn.getOutputStream(), false);
			}

			int status = conn.getResponseCode();
			resp.setStatus(status);
			copyResponseHeaders(conn, resp);

			InputStream in = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
			if (in != null) {
				copy(in, resp.getOutputStream(), true);
			}
		} catch (IOException e) {
			if (conn != null) {
				conn.disconnect(); //断开上游 防止流媒体服务继续推流
			}
			//播放器关闭页面会主动断开连接 属正常情况
			log.debugf("media proxy %s : %s", path, e.getMessage());
			if (!resp.isCommitted()) {
				resp.sendError(HttpServletResponse.SC_BAD_GATEWAY);
			}
		}
	}

	private void copyRequestHeaders(HttpServletRequest req, HttpURLConnection conn) {
		Enumeration<String> names = req.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			if (SKIP_REQUEST_HEADERS.contains(name.toLowerCase())) {
				continue;
			}
			Enumeration<String> values = req.getHeaders(name);
			while (values.hasMoreElements()) {
				conn.addRequestProperty(name, values.nextElement());
			}
		}
	}

	private void copyResponseHeaders(HttpURLConnection conn, HttpServletResponse resp) {
		for (int i = 1; ; i++) {
			String name = conn.getHeaderFieldKey(i);
			String value = conn.getHeaderField(i);
			if (name == null && value == null) {
				break;
			}
			if (name == null || SKIP_RESPONSE_HEADERS.contains(name.toLowerCase())) {
				continue;
			}
			resp.addHeader(name, value);
		}
	}

	private void copy(InputStream in, OutputStream out, boolean flush) throws IOException {
		byte[] buf = new byte[16 * 1024];
		int len;
		try {
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
				if (flush) {
					out.flush(); //直播流必须及时推给播放器
				}
			}
		} finally {
			in.close();
			if (!flush) {
				out.close(); //请求体写出后close触发发送
			}
		}
	}
}
