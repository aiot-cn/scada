package org.aiot.mvc;

import org.aiot.util.HttpUtil;
import org.nutz.mvc.View;
import org.nutz.mvc.view.JspView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PcMobileView implements View{
	private JspView pcView;
	private JspView mobileView;
	public PcMobileView(JspView pcView,JspView mobileView) {
		this.pcView =pcView;
		this.mobileView = mobileView;
	}
	
	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)  {
		try {
			if(HttpUtil.isMobile(req)){
				mobileView.render(req, resp, obj);
			}else{
				pcView.render(req, resp, obj);
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}    
