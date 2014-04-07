package org.fenixedu.cms.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.FluentIterable;

/**
 * Model of a Menu for a given {@link Page}
 */
public class Menu extends Menu_Base {

    /**
     * Logged {@link User} creates a new Menu.
     */
    public Menu() {
        super();
        if (Authenticate.getUser() == null) {
            throw new RuntimeException("Needs Login");
        }
        this.setCreatedBy(Authenticate.getUser());
        this.setCreationDate(new DateTime());
    }

    @Atomic
    public void delete() {
        for(Component c : this.getComponentSet()){
            c.delete();
        }
        
        for (MenuItem menuItem : getItemsSet()) {
            menuItem.delete();
        }
        
        this.setCreatedBy(null);
        this.setSite(null);
        this.deleteDomainObject();
    }

    /**
     * Puts a {@link MenuItem} at a given position, shifting the existing ones to the right.
     * 
     * @param item
     *            The {@link MenuItem} to be added.
     * @param position
     *            the position to save the item.
     */
    public void putAt(MenuItem item, int position) {
        if (position < 0){
            position = 0;
        }
        
        if (position >= this.getToplevelItemsSet().size()){
            item.removeFromParent();
            item.setPosition(this.getToplevelItemsSorted().size());
            this.addToplevelItems(item);
            this.addItems(item);
            return;
        }
        
        if (item.getPosition() != null){
            item.removeFromParent();
        }
        
        List<MenuItem> list = getToplevelItemsSorted();
        
        for (int i = position; i < list.size(); i++) {
            MenuItem menuItem = list.get(i);
            menuItem.setPosition(menuItem.getPosition() + 1);
        }
        
        item.setPosition(position);
        getToplevelItemsSet().add(item);
        getItemsSet().add(item);
    }
    
    /**
     * Removes a given {@link MenuItem} from the Menu.
     * 
     * @param mi
     *            the {@link MenuItem} to be removed.
     */
    public void remove(MenuItem mi){
        int found = 0;
        for(MenuItem item : new ArrayList<>(getToplevelItemsSorted())){
            if (item == mi){
                found++;
                getToplevelItemsSet().remove(mi);
                getItemsSet().remove(mi);
            }else{
                item.setPosition(item.getPosition() - found);
            }
        }
    }
    
    /**
     * Adds a given {@link MenuItem} as the last item.
     * 
     * @param mi
     *            the {@link MenuItem} to be added.
     */
    public void add(MenuItem mi){
        this.putAt(mi, getToplevelItemsSet().size());
    }
    
    /**
     * @return the menu items sorted by position.
     */
    public List<MenuItem> getChildrenSorted(){
        return getToplevelItemsSorted();
    }
    
    public List<MenuItem> getToplevelItemsSorted(){
        
        return FluentIterable.from(getToplevelItemsSet()).toSortedList(new Comparator<MenuItem>() {

            @Override
            public int compare(MenuItem o1, MenuItem o2) {
                return o1.getPosition().compareTo(o2.getPosition());
            }

        });
    }
}
