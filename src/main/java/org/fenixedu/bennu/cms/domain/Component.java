package org.fenixedu.bennu.cms.domain;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.cms.rendering.TemplateContext;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

/**
 * Component for a given page with some associated metadata.
 */
public abstract class Component extends Component_Base {
    protected static final HashMap<String, Class<?>> COMPONENTS = new HashMap<>();

    /**
     * Registers a new class has a component.
     * 
     * @param type
     *            the type of the component. It must be unique for the application.
     * @param c
     *            the class being registered as a component.
     */
    public static void register(String type, Class c) {
        COMPONENTS.put(type, c);
    }

    /**
     * Searches for the class of a component with a given type.
     * 
     * @param type
     *            the type of the component.
     * @return
     *         the class of the component with the given type.
     */
    public static Class forType(String type) {
        return COMPONENTS.get(type);
    }

    /**
     * The logged {@link User} creates a new component.
     */
    public Component() {
        super();
        if (Authenticate.getUser() == null) {
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());

    }

    /**
     * 
     * @return
     *         the name of the component.
     */
    public String getName() {
        return this.getClass().getAnnotation(ComponentType.class).name();
    }

    /**
     * 
     * @return
     *         the description of the component.
     */
    public String getDescription() {
        return this.getClass().getAnnotation(ComponentType.class).description();
    }

    /**
     * @return
     *         the type of the component.
     */
    @Override
    public String getType() {
        return this.getClass().getAnnotation(ComponentType.class).type();
    }

    /**
     * Provides the necessary info needed to render the component on a given page and context.
     * 
     * @param page
     *            the page where the component will be rendered.
     * @param req
     *            request to render the page and the component.
     * @param componentContext
     *            local context for the component.
     * @param globalContext
     *            global context where the component is being rendered.
     */
    public abstract void handle(Page page, HttpServletRequest req, TemplateContext componentContext, TemplateContext globalContext);

    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        this.setCreatedBy(null);
        this.setPage(null);
        this.deleteDomainObject();
    }
}
