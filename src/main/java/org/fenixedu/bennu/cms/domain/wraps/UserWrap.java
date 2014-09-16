package org.fenixedu.bennu.cms.domain.wraps;

import org.fenixedu.bennu.cms.domain.wraps.Wrap;
import org.fenixedu.bennu.core.domain.User;

import java.util.Optional;

/**
 * Created by nurv on 09/09/14.
 */
public class UserWrap extends Wrap {
    private final Optional<User> user;

    public UserWrap(User user) {
        this.user = Optional.ofNullable(user);
    }

    public String getUsername(){
        return user.map(x -> x.getUsername()).orElse("");
    }

    public String getDisplayName(){
        return user.map(x -> x.getProfile().getDisplayName()).orElse("");
    }

    public String getAvatar(){
        return user.map(x -> x.getProfile().getAvatarUrl()).orElse("");
    }

    public boolean isAuthenticated(){
        return user.isPresent();
    }


}
