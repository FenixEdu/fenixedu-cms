package org.fenixedu.cms.portal;

import java.text.DecimalFormat;

import org.fenixedu.bennu.io.domain.GenericFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;

public class CMSBean {

    public String prettySize(long size) {
        if (size == 0) {
            return "0 B";
        }
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public String downloadUrl(GenericFile file) {
        return FileDownloadServlet.getDownloadUrl(file);
    }

}
