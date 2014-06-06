package org.fenixedu.bennu.cms.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

/**
 * Models the items of a {@link Menu}
 */
public class MenuItem extends MenuItem_Base {
    
    /**
     * The logged {@link User} creates a new MenuItem.
     */
    public MenuItem() {
        super();
        if(Authenticate.getUser() == null){
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
    }
    
    /**
     * Adds a children at a given position and shifts the existing items.
     * 
     * @param item
     *            the {@link MenuItem} to be added.
     * @param position
     *            the position where the item should be added.
     */
    public void putAt(MenuItem item, int position) {
        if (position < 0){
            position = 0;
        }
        
        if (position >= this.getChildrenSet().size()){
            item.removeFromParent();
            item.setPosition(this.getChildrenSet().size());
            this.addChildren(item);
            return;
        }
        
        if (item.getPosition() != null){
            item.removeFromParent();
        }
        
        List<MenuItem> list = getChildrenSorted();
        
        for (int i = position; i < list.size(); i++) {
            MenuItem menuItem = list.get(i);
            menuItem.setPosition(menuItem.getPosition() + 1);
        }
        
        item.setPosition(position);
        getChildrenSet().add(item);
    }

    /**
     * Removes a given {@link MenuItem}
     * 
     * @param mi
     *            the children to be removed
     */
    public void remove(MenuItem mi){
        int found = 0;
        for(MenuItem item : new ArrayList<>(getChildrenSorted())){
            if (item == mi){
                found++;
                getChildrenSet().remove(mi);
            }else{
                item.setPosition(item.getPosition() - found);
            }
        }
    }
    
    /**
     * Adds a new {@link MenuItem} has the last item.
     * 
     * @param mi
     *            the {@link MenuItem} to be added.
     */
    public void add(MenuItem mi){
        this.putAt(mi, getChildrenSet().size());
    }

    /**
     * Removes the {@link MenuItem} from its parent.
     * <p>
     * The Parent can be a {@link Menu} or a {@link MenuItem}
     * </p>
     */
    public void removeFromParent(){
        if (this.getTop() != null){
            this.getTop().remove(this);
        }else if(this.getParent() != null){            
            this.getParent().remove(this);
        }
    }

    /**
     * @return the childrens sorted by position
     */
    public List<MenuItem> getChildrenSorted(){
        return getChildrenSet().stream().sorted(Comparator.comparing(MenuItem::getPosition)).collect(Collectors.toList());
    }
    
    /**
     * @return the URL address to visit the item.
     */
    public String getAddress(){
        if (getUrl() != null){
            return getUrl();
        }else{
            String path = CoreConfiguration.getConfiguration().applicationUrl();
            if (path.charAt(path.length()-1) != '/') {
                path += "/";
            }
            path += this.getMenu().getSite().getSlug() + "/" + this.getPage().getSlug();
            return path;
        }
    }
    
    @Atomic
    public void delete(){
        this.removeFromParent();
        for (MenuItem menuItem : getChildrenSet()) {
            menuItem.delete();
        }
        this.setParent(null);
        this.setCreatedBy(null);
        this.setMenu(null);
        this.setPage(null);
        this.setTop(null);
        this.deleteDomainObject();
    }
}
