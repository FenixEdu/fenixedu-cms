package org.fenixedu.cms.domain;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.fenixedu.bennu.core.annotation.GroupArgument;
import org.fenixedu.bennu.core.annotation.GroupArgumentParser;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.ArgumentParser;
import org.fenixedu.bennu.core.groups.CustomGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.joda.time.DateTime;

@GroupOperator("siteViewers")
public class SiteViewersGroup extends CustomGroup {

    private static final long serialVersionUID = -2690871317679895561L;

    @GroupArgumentParser
    public static final class SiteArgumentParser implements ArgumentParser<Site> {

        @Override
        public Site parse(String argument) {
            return Site.fromSlug(argument);
        }

        @Override
        public String serialize(Site argument) {
            return argument.getSlug();
        }

        @Override
        public Class<Site> type() {
            return Site.class;
        }

    }

    @GroupArgument("")
    private final Site site;

    private SiteViewersGroup() {
        this.site = null;
    }

    private SiteViewersGroup(Site site) {
        this.site = site;
    }

    public static Group get(Site site) {
        return new SiteViewersGroup(Objects.requireNonNull(site));
    }

    @Override
    public String getPresentationName() {
        return "Site Viewers for site: " + site;
    }

    @Override
    public PersistentGroup toPersistentGroup() {
        return site.getViewerGroup();
    }

    @Override
    public Set<User> getMembers() {
        if (!site.getPublished()) {
            return Collections.emptySet();
        }
        return site.getCanViewGroup().getMembers();
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user) {
        if (site.getPublished()) {
            return site.getCanViewGroup().isMember(user);
        }
        return false;
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof SiteViewersGroup) {
            SiteViewersGroup other = (SiteViewersGroup) object;
            return site.equals(other.site);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return site.hashCode();
    }
}
