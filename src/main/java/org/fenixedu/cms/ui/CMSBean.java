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
package org.fenixedu.cms.ui;

import java.text.DecimalFormat;
import java.util.Date;

import org.fenixedu.bennu.io.domain.GenericFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;
import org.fenixedu.commons.i18n.I18N;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.ocpsoft.prettytime.PrettyTime;

public class CMSBean {

    public String prettySize(long size) {
        if (size == 0) {
            return "0 B";
        }
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public String prettyDate(DateTime date){
        long time = date.toDate().getTime();

        Date javaUtilDate = date.toDateTime(DateTimeZone.UTC).toDate();

        return new PrettyTime(I18N.getLocale()).format(javaUtilDate);
    }

    public String downloadUrl(GenericFile file) {
        return FileDownloadServlet.getDownloadUrl(file);
    }

}
