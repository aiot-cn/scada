package org.aiot.mvc;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.ViewProcessor;

/**
 * 
 * 自定义动作链
 * 
 */
public class FailProcessor extends ViewProcessor {

    private static final Log log = Logs.get();

    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        view = evalView(config, ai, ai.getFailView());
    }

    public void process(ActionContext ac) throws Throwable {
        if (log.isWarnEnabled()) {
            String uri = Mvcs.getRequestPath(ac.getRequest());
            Throwable throwable = ac.getError();
            ac.getError().printStackTrace();
            /*if(throwable instanceof NullPointerException || !(throwable instanceof RuntimeException))
                ac.getError().printStackTrace();
            else{
                log.errorf("Error@%s :%s",uri,ac.getError().getMessage());
            }*/

        }
        super.process(ac);
    }
}
