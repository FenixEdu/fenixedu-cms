<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of FenixEdu CMS.

    FenixEdu CMS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu CMS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
${portal.toolkit()}

<div class="page-header">
    <h1>Settings</h1>
    <h2><a href="${pageContext.request.contextPath}/cms"><small>CMS</small></a></h2>
</div>

<form action="" class="form" role="form" method="post">
    ${csrf.field()}

    <p>
        <button type="submit" class="btn btn-primary">
            <i class="glyphicon glyphicon-edit"></i> Update
        </button>
    </p>

    <div class="form-group">
        <label class="control-label">Themes managers:</label>
        <input bennu-group allow="public,users,managers,custom" name="themesManagers" type="text" value='${cmsSettings.themesManagers.toGroup().expression}'/>
        <p class="help-block">Users that are allowed to manage <a href="${pageContext.request.contextPath}/cms/themes">themes</a>.</p>
    </div>

    <div class="form-group">
        <label class="control-label">Roles managers:</label>
        <input bennu-group allow="public,users,managers,custom" name="rolesManagers" type="text" value='${cmsSettings.rolesManagers.toGroup().expression}'/>
        <p class="help-block">Users that are allowed to manage <a href="${pageContext.request.contextPath}/cms/permissions">roles</a>.</p>
    </div>

    <div class="form-group">
        <label class="control-label">Folders managers:</label>
        <input bennu-group allow="public,users,managers,custom" name="foldersManagers" type="text" value='${cmsSettings.foldersManagers.toGroup().expression}'/>
        <p class="help-block">Users that are allowed to manage <a href="${pageContext.request.contextPath}/cms/folders">folders</a>.</p>
    </div>

    <div class="form-group">
        <label class="control-label">Global Settings:</label>
        <input bennu-group allow="public,users,managers,custom" name="settingsManagers" type="text" value='${cmsSettings.settingsManagers.toGroup().expression}'/>
        <p class="help-block">Users that are allowed to global settings such as the <a href="${pageContext.request.contextPath}/cms/sites">default site</a>.</p>
    </div>
    
</form>

