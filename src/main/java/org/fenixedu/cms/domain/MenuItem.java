package org.fenixedu.cms.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.joda.time.DateTime;

import com.google.common.collect.FluentIterable;

import pt.ist.fenixframework.Atomic;

public class MenuItem extends MenuItem_Base {
    
    public MenuItem() {
        super();
        if(Authenticate.getUser() == null){
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
    }
    
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
    
    public void add(MenuItem mi){
        this.putAt(mi, getChildrenSet().size());
    }
    
    public void removeFromParent(){
        if (this.getTop() != null){
            this.getTop().remove(this);
        }else if(this.getParent() != null){            
            this.getParent().remove(this);
        }
    }
    
    public List<MenuItem> getChildrenSorted(){
        
        return FluentIterable.from(getChildrenSet()).toSortedList(new Comparator<MenuItem>() {

            @Override
            public int compare(MenuItem o1, MenuItem o2) {
                return o1.getPosition().compareTo(o2.getPosition());
            }

        });
    }
    
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
