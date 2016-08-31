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

import static org.fenixedu.cms.ui.SearchUtils.searchFiles;

import java.util.Collection;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.cms.domain.PostFile;
import org.fenixedu.cms.domain.Site;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.base.Strings;

import pt.ist.fenixframework.FenixFramework;

@BennuSpringController(AdminSites.class)
@RequestMapping("/cms/media")
public class AdminMediaLibrary {

    @RequestMapping(value = "{siteSlug}", method = RequestMethod.GET)
    public String media(Model model, @PathVariable String siteSlug, @RequestParam(required = false) String query,
                    @RequestParam(required = false, defaultValue = "1") int page) {
        Site site = Site.fromSlug(siteSlug);
        AdminSites.canEdit(site);
        Collection<PostFile> allFiles = Strings.isNullOrEmpty(query) ? site.getFilesSet() : searchFiles(site.getFilesSet(), query);
        SearchUtils.Partition<PostFile> partition = new SearchUtils.Partition<>(allFiles, PostFile.NAME_COMPARATOR, 10, page);

        model.addAttribute("site", site);
        model.addAttribute("query", query);
        model.addAttribute("partition", partition);
        model.addAttribute("postFiles", partition.getItems());
        return "fenixedu-cms/media";
    }

    @RequestMapping(value = "{siteSlug}/{postFileId}/edit", method = RequestMethod.GET)
    public String postFile(Model model, @PathVariable String siteSlug, @PathVariable String postFileId) {
        Site site = Site.fromSlug(siteSlug);
        AdminSites.canEdit(site);
        PostFile postFile = FenixFramework.getDomainObject(postFileId);
        if(site.equals(postFile.getSite())) {
            model.addAttribute("site", site);
            model.addAttribute("postFile", postFile);
        }
        return "fenixedu-cms/editMedia";
    }

    @RequestMapping(value = "{siteSlug}/{postFileId}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable String siteSlug, @PathVariable String postFileId) {
        Site site = Site.fromSlug(siteSlug);
        AdminSites.canEdit(site);
        PostFile postFile = FenixFramework.getDomainObject(postFileId);
        if(site.equals(postFile.getSite())) {
            FenixFramework.atomic(()->postFile.delete());
        }
        return mediaLibraryRedirect(site);
    }

    @RequestMapping(value = "{siteSlug}/{postFileId}/edit", method = RequestMethod.POST)
    public RedirectView editPostFile(@PathVariable String siteSlug, @PathVariable String postFileId,
                                     @RequestParam String filename, @RequestParam String displayName,
                                     @RequestParam(required = false) String accessGroup) {
        Site site = Site.fromSlug(siteSlug);
        AdminSites.canEdit(site);
        PostFile postFile = FenixFramework.getDomainObject(postFileId);
        FenixFramework.atomic(()->{
            if(site.equals(postFile.getSite())) {
                postFile.getFiles().setDisplayName(displayName);
                postFile.getFiles().setFilename(filename);
                if(!postFile.getIsEmbedded() && !Strings.isNullOrEmpty(accessGroup)) {
                    postFile.getFiles().setAccessGroup(Group.parse(accessGroup));
                }
            }
        });
        return editMediaLibraryRedirect(postFile);
    }

    private RedirectView editMediaLibraryRedirect(PostFile postFile) {
        return new RedirectView("/cms/media/" + postFile.getSite().getSlug() + "/" + postFile.getExternalId() + "/edit", true);
    }

    private RedirectView mediaLibraryRedirect(Site site) {
        return new RedirectView("/cms/media/" + site.getSlug(), true);
    }

    private Collection<PostFile> getFiles(Site site) {
        return site.getFilesSet().stream().sorted(PostFile.NAME_COMPARATOR).collect(Collectors.toList());
    }

}
