package org.fenixedu.cms.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.i18n.LocalizedString;

public class SiteBuilder extends SiteBuilder_Base implements Sluggable {
    
    public SiteBuilder(String slug) {
        this();
        this.setSlug(SlugUtils.makeSlug(this,slug));
    }
    
    public SiteBuilder(){
        super();
        Bennu.getInstance().getSiteBuildersSet().add(this);
        this.setCanViewGroup(Group.nobody());
    }
    
    
    
    public Site create(LocalizedString name, LocalizedString description){
        Site site = new Site(name,description);
        site.setBuilder(this);
        
        site.setTheme(this.getTheme());
        site.setCanViewGroup(this.getCanViewGroup());
        site.setPublished(this.getPublished());
        site.setEmbedded(this.getEmbedded());

        for (RoleTemplate roleTemplate : this.getRoleTemplateSet()) {
            new Role(roleTemplate,site);
        }
    
        site.setFolder(getFolder());
        getCategoriesSet().stream().forEach(category -> site.categoryForSlug(category.getSlug()));
        site.updateMenuFunctionality();
        
        SiteActivity.createdSite(site, Authenticate.getUser());
        return site;
    }
    
    public boolean isSystemBuilder(){return false;}
    
    public Group getCanViewGroup(){
        return getViewGroup().toGroup();
    }
    
    public void setCanViewGroup(Group group){
        setViewGroup(group.toPersistentGroup());
    }
    
    public final static SiteBuilder forSlug(String builderSlug) {
        return Bennu.getInstance().getSiteBuildersSet().stream()
                .filter(sb->sb.getSlug().equals(builderSlug))
                .findAny().orElseGet(()->null);
    }
    
    @Override
    public void setSlug(String slug) {
        if(isValidSlug(slug)) {
            super.setSlug(slug);
        }
    }
    
    @Override
    public final boolean isValidSlug(String slug) {
        return slug!=null && !slug.equals("")
                && Bennu.getInstance().getSiteBuildersSet().stream().noneMatch(sb->sb!=this && sb.getSlug().equals(slug));
    }
    
    public void delete() {
        this.setBennu(null);
        deleteDomainObject();
    }
}

