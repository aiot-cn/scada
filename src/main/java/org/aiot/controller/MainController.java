package org.aiot.controller;

import org.aiot.handler.protocol.TTemplateProtocol;
import org.aiot.main.Constants;
import org.aiot.main.MainSetup;
import org.aiot.model.enums.PathEnum;
import org.aiot.model.lang.SRes;
import org.aiot.model.table.SysTrigger;
import org.aiot.model.table.SysUrl;
import org.aiot.model.table.TTemplate;
import org.aiot.mvc.PcMobileViewMaker;
import org.aiot.mvc.ProxyView;
import org.aiot.mvc.RoleActionFilter;
import org.aiot.service.BaseService;
import org.aiot.util.*;
import org.nutz.dao.QueryResult;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.view.*;
import org.opencv.core.Mat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Enumeration;
import java.util.List;

@Fail("jsp:pc.common.error")
@Views({PcMobileViewMaker.class})
@SetupBy(MainSetup.class) //应用启动以及关闭时的额外处理
//@Modules({PluginModule.class}) //声明应用的所有子模块 controller
@ChainBy(args = {"ioc/chain.js"}) //动作链
@Chain("default")
@IocBy(
		args={
			"*js", "ioc/",
			"*anno", "org.aiot.service",// 这个package下所有带@IocBean注解的类,都会登记上
			"*quartz",
			"*tx",  // 事务拦截 aop
			"*async"// 异步执行aop
		})

@Filters(@By(type= RoleActionFilter.class))
public class MainController {

	@At("/view/*")
	public View view(HttpServletRequest req,HttpServletResponse resp) throws Throwable {
		String path = req.getServletPath().substring(6);
		SRes sRes = new SRes(path);
		req.setAttribute("SRes",sRes);
		String viewKey = "view."+sRes.getSuffix();
		String view = Constants.prop.get(viewKey,"file");
		//打开方式/模式 preview预览（资源管理器默认） edit编辑
		String openMode = req.getParameter("MODE");
		if(Strings.isNotBlank(openMode)){
			view = Constants.prop.get(viewKey+"."+openMode,view);
		}
		//view 是一个MIME类型
		if(view.contains("/")){
			resp.setContentType(view);
			new RawView("pdf").render(req, resp, sRes.getBytes());
			return null;
		}

		String pm = HttpUtil.isMobile(req) ? "mobile" : "pc";

		if(Strings.isin(new String[]{"workflow","cache","chain","point"},sRes.getSuffix())){
			Object o = CommonUtil.getUri(path);
			if(o instanceof View){
				return (View) o;
			}else if(o instanceof BufferedImage || o instanceof byte[] || o instanceof Mat){
				return new JspView( pm+"/base/view/img");
			}else{
				req.setAttribute("json", Json.toJson(o));
				return new JspView( pm+"/base/view/json");
			}
		}
		return new JspView( pm+"/base/view/"+view);
	}

	@At("/template/*")
	public void toTemplate(HttpServletRequest req,HttpServletResponse resp) throws Throwable {
		String path = req.getServletPath().substring(10);
		BaseService bs = Constants.ioc.get(BaseService.class);
		if(path.endsWith(".pdf")){
			String name = "";
			Enumeration<String>  names = req.getParameterNames();
			boolean download = Lang.parseBoolean(req.getParameter("download"));;
			while (names.hasMoreElements()){
				if(!"download".equals(name))
					name += "_"+req.getParameter(names.nextElement());
			}

			File pdfFile = new File(PathEnum.document.addDir("template/"+path.substring(0,path.length()-4)),
					(name.length() > 0 ? name.substring(1) : "null")+".pdf");
			SysUtil.urlToPdf(req,pdfFile);
			resp.setContentType("application/pdf");
			Object o = pdfFile;
			new RawView("pdf").render(req, resp, download ? pdfFile : Files.readBytes(pdfFile));
			return;
		}
		TTemplate template;
		if(Strings.isNumber(path)){
			Long tid = Long.parseLong(path);
			template = bs.getTCache(TTemplate.class,tid);
		}else{
			template = bs.getTCacheFirst(TTemplate.class,v->Strings.equals("/"+path,v.getPath()));
		}
		SRes sRes = new SRes(new TTemplateProtocol(template));
		req.setAttribute("SRes",sRes);
		new JspView( "pc/base/view/"+(template.getType() == 0 ? "html" : "graph")).render(req,resp,null);
	}

	@At("/*")
	public void api(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
		String path = req.getServletPath();
		String protocol = req.getParameter("PROTOCOL");
		if(Strings.isNotBlank(protocol)){
			req.setAttribute("SRes",new SRes(protocol));
		}
		BaseService bs = Constants.ioc.get(BaseService.class);
		SysTrigger trigger = bs.getTCacheFirst(SysTrigger.class, v-> v.getDeviceId() == -4 && Strings.equals(path,v.getMember()));
		if(trigger != null){
			NutMap param = new NutMap("req",req)
					.setv("reqInfo",HttpUtil.getReqInfo(req))
					.setv("resp",resp);
			Object r = BaseUtils.runWorkflow(trigger,param);
			if(r instanceof View){
				((View) r).render(req,resp,null);
			}else if(CommonUtil.isBasicType(r)){
				new RawView("pdf").render(req,resp,r);
			}else{
				UTF8JsonView.COMPACT.render(req,resp,r);
			}
			return;
		}
		//UserService us = Constants.ioc.get(UserService.class);
		SysUrl url = bs.getTCacheFirst(SysUrl.class, v-> Strings.equals(path,v.getUrl()));
		if(url == null){
			url = new SysUrl();
		}

		/*if(!us.autoLogon(url.getRole(),req.getParameter("token"))){
			new ForwardView("/user/login").render(req, resp, null);
			return;
		}*/

		String param = StrUtil.replace(url.getResParam(),"\\$\\{\\w+}", v->{
			v = v.substring(2,v.length()-1);
			return req.getParameter(v);
		});

		Object data = SysUtil.scriptByName(url.getScript(),req,resp,url);
		switch (url.getType()){
			case 0:
				String p = HttpUtil.isMobile(req) ? "wap":"pc";
				new JspView(p + "/" + Strings.sBlank(param,"/".equals(path) ? "index" : path)).render(req,resp,data);
				break;
			case 1:
				UTF8JsonView.COMPACT.render(req,resp,data);
				break;
			case 2:
				new ForwardView(param).render(req,resp,data);
				break;
			case 3:
				new ServerRedirectView(param).render(req,resp,data);
				break;
			case 4:
				new RawView(param).render(req,resp,data);
				break;
			case 5:
				new ProxyView(param).setScript(url.getScript()).render(req,resp,data);
				break;
			case 6:
				List<NutMap> list = bs.querySql(param, HttpUtil.getParameter(req));
				UTF8JsonView.COMPACT.render(req,resp, new QueryResult(list,null));
				break;

		}
	}

}
