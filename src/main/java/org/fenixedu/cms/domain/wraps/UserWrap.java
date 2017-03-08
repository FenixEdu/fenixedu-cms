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
package org.fenixedu.cms.domain.wraps;

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

    public String getUsername() {
        return user.map(x -> x.getUsername()).orElse("");
    }

    public String getDisplayName() {
        return user.map(x -> x.getProfile().getDisplayName()).orElse("");
    }

    public String getAvatar() {
        return user.map(x -> x.getProfile().getAvatarUrl()).orElse("");
    }

    public boolean isAuthenticated() {
        return user.isPresent();
    }

}
