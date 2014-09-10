package org.fenixedu.bennu.cms.domain.wraps;

import org.fenixedu.bennu.cms.domain.wraps.Wrap;
import org.fenixedu.bennu.core.domain.User;

/**
 * Created by nurv on 09/09/14.
 */
public class UserWrap extends Wrap {
    private final User user;

    public UserWrap(User user) {
        this.user = user;
    }

    public String getUsername(){
        return this.user.getUsername();
    }
}
