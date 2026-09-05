package org.aiot.mvc;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.view.JspView;

public class PcMobileViewMaker implements ViewMaker{
	public PcMobileViewMaker() {}
    public View make(Ioc ioc, String type, String value){
        if("pm".equalsIgnoreCase(type)){
        	JspView pcView = new JspView("pc." + value);
        	JspView mobileView =  new JspView("mobile." + value);
        	return new PcMobileView(pcView,mobileView);
        }
        return null;
    }
}    
