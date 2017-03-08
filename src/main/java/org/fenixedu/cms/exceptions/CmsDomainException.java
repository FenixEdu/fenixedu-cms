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
package org.fenixedu.cms.exceptions;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Created by nurv on 22/08/14.
 */
public class CmsDomainException extends DomainException {

    private static final long serialVersionUID = -7452149666134820945L;

    protected static final String BUNDLE = "CmsExceptionResources";

    protected CmsDomainException(Response.Status status, String bundle, String key, String... args) {
        super(status, bundle, key, args);
    }

    public static CmsDomainException forbiden() {
        return new CmsDomainException(Response.Status.FORBIDDEN, BUNDLE, "error.not.authorized");
    }

    public static CmsDomainException notFound() {
        return new CmsDomainException(Response.Status.NOT_FOUND, BUNDLE, "error.not.found");
    }

    public static CmsDomainException badRequest(String key) {
        return new CmsDomainException(Status.BAD_REQUEST, BUNDLE, key);
    }
}
