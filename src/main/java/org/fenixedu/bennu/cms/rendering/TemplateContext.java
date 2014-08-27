package org.fenixedu.bennu.cms.rendering;

import java.util.HashMap;

public class TemplateContext extends HashMap<String, Object> {

    private static final String REQUEST_CONTEXT_ATTR = "__request_ctx__";
    private static final long serialVersionUID = -2684602340841158526L;

    public void setRequestContext(String[] ctx) {
        put(REQUEST_CONTEXT_ATTR, ctx);
    }

    public String[] getRequestContext() {
        return (String[]) get(REQUEST_CONTEXT_ATTR);
    }

    public String getParameter(String name) {
        return (String) get(name);
    }

}
