package org.aiot.controller;

import org.aiot.handler.protocol.TTemplateProtocol;
import org.aiot.handler.protocol.TTextProtocol;
import org.aiot.main.Constants;
import org.aiot.main.MainSetup;
import org.aiot.model.enums.DictTypeEnum;
import org.aiot.model.enums.PathEnum;
import org.aiot.model.lang.SRes;
import org.aiot.model.table.*;
import org.aiot.mvc.CheckLevel;
import org.aiot.mvc.PcMobileViewMaker;
import org.aiot.mvc.ProxyView;
import org.aiot.service.BaseService;
import org.aiot.util.*;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.nutz.dao.QueryResult;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.view.*;
import org.opencv.core.Mat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

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

@Filters(@By(type= CheckLevel.class, args="0"))
public class MainController {

	@Filters
	@At("/view/*")
	public View view(HttpServletRequest req,HttpServletResponse resp) throws Throwable {
		String path = req.getServletPath().substring(6);
		SRes sRes = new SRes(path);
		req.setAttribute("SRes",sRes);
		String view = Constants.prop.get("view."+sRes.getSuffix(),"file");
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

	@At("/docs/*")
	public @Ok("pm:base.docs") void docs(HttpServletRequest req,HttpServletResponse resp) throws Throwable{
		BaseService bs = Constants.ioc.get(BaseService.class);
		String path = req.getServletPath().substring(5); // /aiot/index
		if(Strings.isBlank(path) || path.equals("/"))
			path = "/aiot/index";

		path = path.substring(1);
		if(!path.contains("/"))
			path += "/index";

		int i = path.indexOf("/");
		String proCode = path.substring(0,i);//项目
		String path2 = path.substring(i);	//路径

		TDoc doc = bs.getTCacheFirst(TDoc.class,v->Strings.equals(proCode,v.getProCode()) && Strings.equals(path2,v.getPath()));
		if(doc == null)
			throw new RuntimeException(proCode+"文档不存在 "+path2+" 内容");
		List<TDoc> docList = bs.getTCache(TDoc.class,v->Strings.equals(proCode,v.getProCode()));

		SRes sRes = new SRes(new TTextProtocol("doc-"+doc.getId()));

		List<SysDict> docProjects = DictTypeEnum.docProject.getList();
		SysDict docProject = new SysDict();
		docProject.setCode("aiot");
		docProject.setName("aiot");
		docProjects.add(0,docProject);

		String projectName = proCode;
		for(SysDict dict : docProjects){
			if(Strings.equals(proCode,dict.getCode())){
				projectName = dict.getName();
				break;
			}
		}
		// 将 Markdown 内容渲染为 HTML
		String mdContent = sRes.getContent();
		Parser parser = Parser.builder()
				.extensions(Collections.singletonList(TablesExtension.create()))
				.build();
		Node document = parser.parse(Strings.sBlank(mdContent));
		String contextPath = req.getContextPath();
		HtmlRenderer renderer = HtmlRenderer.builder()
				.extensions(Collections.singletonList(TablesExtension.create()))
				.attributeProviderFactory(ctx -> (node, tagName, attributes) -> {
					if (node instanceof Image) {
						String src = attributes.get("src");
						String fixed = fixDocImage(src, contextPath);
						if (fixed != null) attributes.put("src", fixed);
					}
				})
				.build();
		String htmlContent = renderer.render(document);
		req.setAttribute("doc",doc);
		req.setAttribute("docList",docList);
		req.setAttribute("docTree",buildDocTree(docList,proCode));
		req.setAttribute("docProject",docProjects);
		req.setAttribute("docProjectName",projectName);
		req.setAttribute("docProCode", proCode);
		req.setAttribute("SRes",sRes);
		req.setAttribute("docContentHtml", htmlContent);
	}

	/**
	 * 文档 Markdown 图片路径补全 Tomcat 项目目录（contextPath）
	 */
	private static String fixDocImage(String src, String contextPath){
		if(Strings.isBlank(src))
			return src;
		if(src.startsWith("//") || src.startsWith("#"))
			return src;
		if(src.matches("^[a-zA-Z][a-zA-Z0-9+.-]*:.*")) // http/https/data/其他协议
			return src;
		if(Strings.isNotBlank(contextPath) && (src.equals(contextPath) || src.startsWith(contextPath + "/")))
			return src;
		return (src.startsWith("/") ? contextPath : contextPath + "/") + src;
	}

	/**
	 * 将扁平文档列表按 parentId 组装成树，深度优先遍历拍平，level 表示层级（0 为根）
	 */
	private List<NutMap> buildDocTree(List<TDoc> docList, String proCode){
		Set<Long> ids = new HashSet<>();
		Map<Long,List<TDoc>> childrenMap = new HashMap<>();
		for(TDoc d : docList){
			ids.add(d.getId());
			if(d.getParentId() != null){
				List<TDoc> children = childrenMap.computeIfAbsent(d.getParentId(), k -> new ArrayList<>());
				children.add(d);
			}
		}
		List<NutMap> result = new ArrayList<>();
		Set<Long> visited = new HashSet<>();
		for(TDoc d : docList){
			if("/index".equals(d.getPath()))
				continue;
			// 根节点：无上级，或上级不在当前文档列表中
			if(d.getParentId() == null || !ids.contains(d.getParentId())){
				appendDocNode(d,childrenMap,0,proCode,visited,result);
			}
		}
		return result;
	}

	private void appendDocNode(TDoc node, Map<Long,List<TDoc>> childrenMap, int level, String proCode, Set<Long> visited, List<NutMap> out){
		if(!visited.add(node.getId())){
			return; // 防止循环引用
		}
		NutMap nm = new NutMap();
		nm.put("id",node.getId());
		nm.put("name",node.getName());
		if(Strings.isNotBlank(node.getPath()))
			nm.put("url",proCode + node.getPath());
		nm.put("level",level);
		out.add(nm);
		List<TDoc> children = childrenMap.get(node.getId());
		if(children != null){
			children.sort(null); // 按 sequence 排序
			for(TDoc c : children){
				appendDocNode(c,childrenMap,level + 1,proCode,visited,out);
			}
		}
	}




	@At("/*")
	@Filters
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
			url.setRole(2);
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
