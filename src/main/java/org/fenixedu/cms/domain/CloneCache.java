package org.fenixedu.cms.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import pt.ist.fenixframework.DomainObject;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Created by borgez-dsi on 17-06-2015.
 */
public class CloneCache {
    private HashMap<String, Cloneable> clonesCache = Maps.newHashMap();

    public <R extends DomainObject> R getOrClone(R object, Function<R, Cloneable> cloneCreator) {
        Preconditions.checkNotNull(object);
        if (!clonesCache.containsKey(object.getExternalId())) {
            clonesCache.put(object.getExternalId(), cloneCreator.apply(object));
        }
        return (R) clonesCache.get(object.getExternalId());
    }

    public <R extends DomainObject> void setClone(R object, Cloneable clone) {
        clonesCache.put(object.getExternalId(), clone);
    }

}
