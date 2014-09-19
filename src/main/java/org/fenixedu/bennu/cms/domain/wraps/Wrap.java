package org.fenixedu.bennu.cms.domain.wraps;

/**
 * Created by nurv on 05/09/14.
 */
public abstract class Wrap {
    public static Wrap make(Object o) {
        if (o instanceof Wrappable) {
            return ((Wrappable) o).makeWrap();
        } else {
            throw new RuntimeException("Object of type " + o.getClass().getCanonicalName() + " is not wrappable");
        }
    }
}
