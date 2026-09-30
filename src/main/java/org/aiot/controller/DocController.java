package org.aiot.controller;

import org.aiot.handler.protocol.TTextProtocol;
import org.aiot.main.Constants;
import org.aiot.model.enums.DictTypeEnum;
import org.aiot.model.lang.SRes;
import org.aiot.model.table.SysDict;
import org.aiot.model.table.TDoc;
import org.aiot.service.BaseService;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class DocController {

    @At("/docs/*")
    public @Ok("pm:base.docs") void docs(HttpServletRequest req,HttpServletResponse resp) throws Throwable{
        BaseService bs = Constants.ioc.get(BaseService.class);
        // /docs -> /aiot/index
        String path = req.getServletPath().substring(5);
        if(Strings.isBlank(path) || path.equals("/"))
            path = "/aiot/index";

        // aiot/index 没有二级补足
        path = path.substring(1);
        if(!path.contains("/"))
            path += "/index";

        int i = path.indexOf("/");
        String proCode = path.substring(0,i);//项目
        String path1 = path.substring(i);//路径
        List<TDoc> docs = bs.getTCache(TDoc.class, v->Strings.equals(proCode,v.getProCode()));
        Map<String,TDoc> docMap = buildDocPathMap(docs);
        TDoc doc = docMap.get(path.substring(i)); // 按全路径匹配，如 /guide/install
        if(doc == null)
            throw new RuntimeException(proCode+"文档不存在 "+path1+" 内容");

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
        req.setAttribute("docList", docs);
        req.setAttribute("docTree",buildDocTree(docs,proCode));
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
     * 将扁平文档列表转为 Map，键为文档全路径（按 parentId 从祖先到自身以 "/" 拼接各 path 段，如 /guide/install），值为 TDoc。
     * path 仅存单段（如 index）；上级不在当前列表中时视为根；同一全路径重复时以后者为准
     */
    private Map<String,TDoc> buildDocPathMap(List<TDoc> docList){
        Map<Long,TDoc> idMap = new HashMap<>();
        for(TDoc d : docList){
            idMap.put(d.getId(),d);
        }
        Map<String,TDoc> result = new HashMap<>();
        for(TDoc d : docList){
            // 自下而上收集自身及祖先的 path 段，visited 防止循环引用
            List<String> segs = new ArrayList<>();
            Set<Long> visited = new HashSet<>();
            TDoc cur = d;
            while(cur != null && visited.add(cur.getId())){
                String seg = Strings.trim(cur.getPath());
                while(seg.startsWith("/")) // 容错：去掉误带的斜杠
                    seg = seg.substring(1);
                while(seg.endsWith("/"))
                    seg = seg.substring(0,seg.length() - 1);
                if(!seg.isEmpty())
                    segs.add(seg);
                cur = cur.getParentId() == null ? null : idMap.get(cur.getParentId());
            }
            if(segs.isEmpty())
                continue;
            Collections.reverse(segs); // 祖先在前
            result.put("/" + String.join("/",segs),d);
        }
        return result;
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
            // 根 index 是默认落地页，不进侧边栏（嵌套的 guide/index 仍显示）
            if("index".equals(Strings.trim(d.getPath())) && (d.getParentId() == null || !ids.contains(d.getParentId())))
                continue;
            // 根节点：无上级，或上级不在当前文档列表中
            if(d.getParentId() == null || !ids.contains(d.getParentId())){
                appendDocNode(d,childrenMap,0,proCode,"",visited,result);
            }
        }
        return result;
    }

    private void appendDocNode(TDoc node, Map<Long,List<TDoc>> childrenMap, int level, String proCode, String parentPath, Set<Long> visited, List<NutMap> out){
        if(!visited.add(node.getId())){
            return; // 防止循环引用
        }
        NutMap nm = new NutMap();
        nm.put("id",node.getId());
        nm.put("name",node.getName());
        String seg = Strings.trim(node.getPath()); // path 仅存单段
        String fullPath = seg.isEmpty() ? parentPath : parentPath + "/" + seg;
        if(!seg.isEmpty())
            nm.put("url",proCode + fullPath);
        nm.put("level",level);
        out.add(nm);
        List<TDoc> children = childrenMap.get(node.getId());
        if(children != null){
            children.sort(null); // 按 sequence 排序
            for(TDoc c : children){
                appendDocNode(c,childrenMap,level + 1,proCode,fullPath,visited,out);
            }
        }
    }

}
