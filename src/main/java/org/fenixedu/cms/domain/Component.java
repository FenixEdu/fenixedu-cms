package org.fenixedu.cms.domain;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.cms.rendering.TemplateContext;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public abstract class Component extends Component_Base {
    protected static final HashMap<String, Class<?>> COMPONENTS = new HashMap<>();

    public static void register(String type, Class c) {
        COMPONENTS.put(type, c);
    }

    public static Class forType(String type) {
        return COMPONENTS.get(type);
    }

    public Component() {
        super();
        if (Authenticate.getUser() == null) {
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());

    }

    public String getName() {
        return this.getClass().getAnnotation(ComponentType.class).name();
    }

    public String getDescription() {
        return this.getClass().getAnnotation(ComponentType.class).description();
    }

    public String getType() {
        return this.getClass().getAnnotation(ComponentType.class).type();
    }

    public abstract void handle(Page page, HttpServletRequest req, TemplateContext componentContext, TemplateContext globalContext);

    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        this.setCreatedBy(null);
        this.setPage(null);
        this.deleteDomainObject();
    }
}
