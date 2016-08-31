/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu CMS.
 *
 * FenixEdu CMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu CMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.cms.domain;

import java.util.HashMap;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import pt.ist.fenixframework.DomainObject;

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
