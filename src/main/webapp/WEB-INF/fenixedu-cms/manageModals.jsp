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
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://fenixedu.org/taglib/intersection" prefix="modular" %>

<c:if test="${cmsSettings.canManageSettings()}">
    <modular:intersect location="sites.manage" position="creation.modals">
        <modular:arg key="csrfField" value="${csrf.field()}"></modular:arg>
    </modular:intersect>
    <div class="modal fade" id="sites-settings">
        <div class="modal-dialog">
            <form method="post" class="form-horizontal" action="${pageContext.request.contextPath}/cms/sites/cmsSettings">
                    ${csrf.field()}
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span class="sr-only">Close</span></button>
                        <div class="row">
                            <div class="col-sm-12">
                                <h3 class="modal-title">Settings</h3>
                                <small>Costumize your content managment system</small>
                            </div>
                            <div class="col-sm-12">
                                <div role="tabpanel">
                                    <!-- Nav tabs -->
                                    <ul class="nav nav-tabs" role="tablist">
                                        <li role="presentation" class="active">
                                            <a href="#general" aria-controls="general" role="tab" data-toggle="tab">General</a>
                                        </li>
                                        <c:if test="${cmsSettings.canManageFolders()}">
                                            <li role="presentation">
                                                <a href="#tags" aria-controls="tags" role="tab" data-toggle="tab">Tags</a>
                                            </li>
                                        </c:if>
                                        <c:if test="${cmsSettings.canManageRoles()}">
                                            <li role="presentation">
                                                <a href="#roles" aria-controls="roles" role="tab" data-toggle="tab">Roles</a>
                                            </li>
                                        </c:if>

                                        <c:if test="${cmsSettings.canManageSettings()}">
                                            <li role="presentation">
                                                <a href="#acl" aria-controls="acl" role="tab" data-toggle="tab">Access Control</a>
                                            </li>
                                        </c:if>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-body">
                        <!-- Tab panes -->
                        <div class="tab-content">
                            <div role="tabpanel" class="tab-pane active" id="general">
                                <div class="form-group">
                                    <label class="col-sm-3 control-label">Default Site</label>

                                    <div class="col-sm-9">
                                        <a href="${pageContext.request.contextPath}/cms/sites/${defaultSite.slug}#settings">
                                            ${defaultSite.name.content}
                                        </a>
                                        <p class="help-block">
                                            The Default Site is the site that is used when you visit the root of the server.
                                            To change it, search for the new default site, click the arrow on the right side
                                            and then click "Make Default Site".
                                        </p>
                                    </div>
                                </div>
                            </div>
                            <c:if test="${cmsSettings.canManageFolders()}">
                                <div role="tabpanel" class="tab-pane" id="tags">
                                    <div class="row">
                                        <div class="col-md-12">
                                            <a class="btn btn-primary" href="#" data-toggle="modal" data-target="#newFolderModal">
                                                <span class="glyphicon glyphicon-plus"></span>&nbsp;New</a>
                                            </a>
                                        </div>
                                    </div>
                                    <c:choose>
                                        <c:when test="${folders.size() == 0}">
                                            <div class="panel panel-default">
                                                <div class="panel-body">
                                                    There are no tags.
                                                </div>
                                            </div>
                                        </c:when>

                                        <c:otherwise>
                                            <table class="table">
                                                <thead>
                                                <tr>
                                                    <th>Name</th>
                                                    <th>Path</th>
                                                    <th>Websites</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <c:forEach var="f" items="${folders}">
                                                    <tr>
                                                        <td class="col-md-8">${f.functionality.title.content}</td>
                                                        <td class="col-md-3">/${f.functionality.path}</td>
                                                        <td class="col-md-1">
                                                                ${f.siteSet.size()}
                                                            <div class="dropdown pull-right">
                                                                <a class="dropdown-toggle" href="#" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                                                                    <span class="caret"></span>
                                                                </a>
                                                                <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenu1">
                                                                    <li><a href="${pageContext.request.contextPath}/cms/folders/resolver/${f.externalId}">Edit custom resolver</a></li>
                                                                    <c:if test="${f.siteSet.size() == 0}">
                                                                        <li><a class="delete-tag-link" data-id="${f.externalId}" href="#"><spring:message code="action.delete"/></a></li>
                                                                    </c:if>
                                                                </ul>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>

                            <c:if test="${cmsSettings.canManageRoles()}">
                                <div role="tabpanel" class="tab-pane" id="roles">
                                    <div class="row">
                                        <div class="col-md-12">
                                            <a class="edit-permissions-link btn btn-primary" href="#">
                                                <span class="glyphicon glyphicon-plus"></span>&nbsp;New</a>
                                            </a>
                                        </div>
                                    </div>
                                    <c:choose>
                                        <c:when test="${folders.size() == 0}">
                                            <div class="panel panel-default">
                                                <div class="panel-body">
                                                    There are no roles.
                                                </div>
                                            </div>
                                        </c:when>

                                        <c:otherwise>
                                            <table class="table">
                                                <thead>
                                                <tr>
                                                    <th>Role Name</th>
                                                    <th>Websites</th>
                                                </tr>
                                                </thead>
                                                <tbody>

                                                <c:forEach var="role" items="${roles}">
                                                    <tr>
                                                        <td class="col-md-10">
                                                                ${role.name.content}
                                                            <a href="#" data-id="${role.externalId}" class="edit-permissions-link btn btn-small btn-default pull-right">Edit</a>
                                                        </td>
                                                        <td class="col-md-2">
                                                                ${role.roles.size()}
                                                            <div class="dropdown pull-right">
                                                                <a class="dropdown-toggle" href="#" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                                                                    <span class="caret"></span>
                                                                </a>
                                                                <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenu1">
                                                                    <li><a class="edit-permissions-link" data-id="${role.externalId}" href="#">Edit permissions</a></li>
                                                                    <li><a class="connect-site-link" data-id="${role.externalId}" href="#">Add to website</a></li>
                                                                    <li><a class="delete-role-link" data-id="${role.externalId}" href="#">Delete</a></li>
                                                                </ul>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>

                            <c:if test="${cmsSettings.canManageSettings()}">
                                <div role="tabpanel" class="tab-pane" id="acl">
                                    <div class="form-group">
                                        <label class="control-label col-sm-3">Themes managers:</label>
                                        <div class="col-sm-9">
                                            <input bennu-group allow="public,users,managers,custom" name="themesManagers" type="text" value='${cmsSettings.themesManagers.toGroup().expression}'/>
                                            <p class="help-block">Users that are allowed to manage <a href="${pageContext.request.contextPath}/cms/themes">themes</a>.</p>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Roles managers:</label>
                                        <div class="col-sm-9">
                                            <input bennu-group allow="public,users,managers,custom" name="rolesManagers" type="text" value='${cmsSettings.rolesManagers.toGroup().expression}'/>
                                            <p class="help-block">Users that are allowed to manage <a href="${pageContext.request.contextPath}/cms/permissions">roles</a>.</p>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Folders managers:</label>
                                        <div class="col-sm-9">
                                            <input bennu-group allow="public,users,managers,custom" name="foldersManagers" type="text" value='${cmsSettings.foldersManagers.toGroup().expression}'/>
                                            <p class="help-block">Users that are allowed to manage <a href="${pageContext.request.contextPath}/cms/folders">folders</a>.</p>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Global Settings:</label>
                                        <div class="col-sm-9">
                                            <input bennu-group allow="public,users,managers,custom" name="settingsManagers" type="text" value='${cmsSettings.settingsManagers.toGroup().expression}'/>
                                            <p class="help-block">Users that are allowed to global settings such setting the <a href="${pageContext.request.contextPath}/cms/sites">default site</a> or <a href="#create-site">create new sites</a></p>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Done</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</c:if>

<script type="application/javascript">
    function getParameterByName(name) {
        var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
        return match && decodeURIComponent(match[1].replace(/\+/g, ' ')) || "";
    }

    function goToPage(pageNumber) {
        searchPosts({page: pageNumber});
    }

    function searchPosts(options) {
        var searchQueryObj = {page: options.page || getParameterByName('page')};
        var query = typeof(options.query) === "string" ? options.query : getParameterByName('query');
        var tag = typeof(options.tag) === "string" ? options.tag : getParameterByName('tag');
        if(query){
            searchQueryObj.query=query;
        }
        if(tag){
            searchQueryObj.tag=tag;
        }
        window.location.href= "${pageContext.request.contextPath}/cms/sites/search?"+$.param(searchQueryObj)

    }

    (function () {
        $('#search-query').keypress(function (e) {
            if (e.which == 13) {
                searchPosts({ query: $('#search-query').val(), page: 1});
            }
        });
        $('.search-tag').click(function (e) {
            searchPosts({ tag: $(e.target).data('val'), page: 1});
            e.preventDefault();
        });
    })();
</script>

<c:if test="${cmsSettings.canManageFolders()}">
    <div class="modal fade" id="newFolderModal" tabindex="-1" role="dialog" aria-hidden="true">
        <form class="form-horizontal" action="${pageContext.request.contextPath}/cms/folders" method="post">
                ${csrf.field()}
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                                class="sr-only">Close</span></button>
                        <h3><spring:message code="folder.manage.label.new.folder"/></h3>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="path" class="col-sm-2 control-label"><spring:message
                                    code="folder.manage.label.path"/>:</label>

                            <div class="col-sm-10">
                                <input type="text" name="path" id="path" class="form-control">

                                <p class="help-block"><spring:message code="folder.manage.label.path.help"/></p>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="description" class="col-sm-2 control-label"><spring:message
                                    code="folder.manage.label.description"/>:</label>

                            <div class="col-sm-10">
                                <input bennu-localized-string required-any type="text" name="description" class="form-control"
                                       id="description" \>
                            </div>

                        </div>
                    </div>
                    <div class="modal-footer">
                        <form action="deleteTemplate" id="templateDeleteForm" method="POST">
                                ${csrf.field()}
                            <button type="submit" class="btn btn-primary"><spring:message code="action.create"/></button>
                        </form>
                    </div>
                </div>
            </div>
        </form>
    </div>
    <script>
        function deleteFolder(e){
            $("#deleteFolderModal form").attr("action", "${pageContext.request.contextPath}/cms/folders/delete/" + $(e.target).data("id"));

            $("#deleteFolderModal").modal("show");
        }

        $(".delete-tag-link").on("click", deleteFolder);
    </script>
    <div class="modal fade" id="deleteFolderModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h3 class="modal-title">Are you sure?</h3>
                </div>
                <div class="modal-body">
                    <p>You are about to delete this tag. There is no way to rollback this operation. Are you sure? </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
                    <button type="button" onclick="$('#deleteTagForm').submit();" class="btn btn-danger">Yes</button>
                    <form action="${pageContext.request.contextPath}/cms/folders/delete/" method="post" id="deleteTagForm">${csrf.field()}</form>
                </div>
            </div>
        </div>
    </div>

</c:if>

<c:if test="${cmsSettings.canManageRoles()}">

    <div class="modal fade" id="delete-role-modal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h3 class="modal-title">Are you sure?</h3>
                </div>
                <div class="modal-body">
                    <p>You are about to delete this role. There is no way to rollback this operation. Are you sure? </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
                    <button type="button" onclick="$('#deleteRoleForm').submit();" class="btn btn-danger">Yes</button>
                    <form action="${pageContext.request.contextPath}/cms/permissions//delete" method="post" id="deleteRoleForm">${csrf.field()}</form>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="connect-site-modal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h3 class="modal-title">Connect with a new site</h3>
                    <small>Using this functionality you are able to make this role available for an existing site.</small>
                </div>
                <form action="${pageContext.request.contextPath}/cms/permissions//addSite" method="post">
                        ${csrf.field()}
                    <div class="modal-body">
                        <div class="form-group">
                            <label class="col-sm-2 control-label">Site</label>
                            <div class="col-sm-10">
                                <input required-any name="siteSlug" placeholder="Enter the site slug" class="form-control">
                                <p class="help-block">Please enter the slug of the site you want to associate with.</p>
                            </div>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="reset" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Connect</button>
                    </div>

                </form>
            </div>
        </div>
    </div>

    <script>
        var roles = [
                <c:forEach var="role" items="${roles}">
                    {
                        description:<c:out value="${role.name.json()}" escapeXml="false"></c:out>,
                        externalId:<c:out value="${role.externalId}"></c:out>,
                        permissions:<c:out  value="${role.permissions.json()}" escapeXml="false"></c:out>
                    },
                </c:forEach>
            ];
        function connectSiteModal(e){
            $("#connect-site-modal form").attr("action", "${pageContext.request.contextPath}/cms/permissions/" + $(e.target).data("id") + "/addSite");

            $("#connect-site-modal").modal("show");
        }

        $(".connect-site-link").on("click", connectSiteModal);

        function deleteRoleModal(e){
            $("#delete-role-modal form").attr("action", "${pageContext.request.contextPath}/cms/permissions/" + $(e.target).data("id") + "/delete");

            $("#delete-role-modal").modal("show");
        }

        $(".delete-role-link").on("click", deleteRoleModal);

        function editPermissionsModal(e){
            var permissions
            if($(e.target).data("id")){
                $("#edit-role-permissions form").attr("action", "${pageContext.request.contextPath}/cms/permissions/" + $(e.target).data("id") + "/edit");
                permissions = roles.filter(function(r){return r.externalId == $(e.target).data("id");})[0];

                $("#permission-description").val(JSON.stringify(permissions.description));
                $("#permission-description").trigger('change')


                permissions= permissions.permissions;
                $('.permissions-inputs input[type="checkbox"]').each(function() {
                    if (permissions.includes($(this).data("permission-name"))) {
                       $(this).prop('checked', true);
                    } else {
                        $(this).prop('checked', false);

                    }
                });
            } else {
                $("#edit-role-permissions form").attr("action", "${pageContext.request.contextPath}/cms/permissions/create");
                $("#permission-description").val('');
                $("#permission-description").trigger('change')


                $('.permissions-inputs input[type="checkbox"]').each(function(){
                    $(this).prop('checked',false);
                })
            }
            $('#permissions-json').val(JSON.stringify(permissions));


            $("#edit-role-permissions").modal("show");
        }

        $(".edit-permissions-link").on("click", editPermissionsModal);
    </script>

    <div class="modal fade" id="create-site" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form class="form-horizontal" action="${pageContext.request.contextPath}/cms/sites/new" method="post" role="form">
                        ${csrf.field()}
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><spanclass="sr-only"></span></button>
                        <h3>Create</h3>
                        <small>Create a new site</small>
                    </div>
                    <div class="modal-body">
                        <div class="${emptyName ? "form-group has-error" : "form-group"}">
                            <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.create.label.name"/></label>

                            <div class="col-sm-10">
                                <spring:message code='site.create.label.name' var="create.label.name"/>
                                <input bennu-localized-string required-any type="text" name="name" class="form-control" id="inputEmail3"
                                       placeholder="${create.label.name}">
                                <c:if test="${emptyName !=null }"><p class="text-danger"><spring:message code='site.create.error.emptyName'/></p>
                                </c:if>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.create.label.description"/></label>
                                <spring:message code="site.create.label.description" var="create.label.description"/>
                            <div class="col-sm-10">
                                <input bennu-localized-string name="description" placeholder="${create.label.description}"
                                       class="form-control" \>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="inputEmail3" class="col-sm-2 control-label">Builder</label>

                            <div class="col-sm-10">
                                <select name="builder" id="builder" class="form-control">
                                    <option value="${null}">&lt; None &gt;</option>

                                    <c:forEach items="${builders}" var="builder">
                                        <option value="${builder.externalId}">${builder.slug}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div id="no-builder-options">
                        <div class="form-group">
                            <label for="inputEmail3" class="col-sm-2 control-label">Theme</label>

                            <div class="col-sm-10">
                                <select name="theme" id="" class="form-control">
                                    <option value="${null}">&lt; None &gt;</option>

                                    <c:forEach var="theme" items="${themes}">
                                        <option value="${theme.type}">${theme.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>



                        <div class="form-group">
                            <label for="folder" class="col-sm-2 control-label">Folder</label>

                            <div class="col-sm-10">
                                <select name="folder" id="" class="form-control">
                                    <option value>--</option>

                                    <c:forEach items="${folders}" var="f">
                                        <option value="${f.externalId}">${f.functionality.description.content}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="embeded" class="col-sm-2 control-label"><spring:message code="label.embedded"/></label>
                            <div class="col-sm-2">
                                <div class="switch switch-success">
                                    <label for="embeded"><input type="checkbox" id="embeded" value="true" \>Embedded</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="roles" class="col-sm-2 control-label"><spring:message code="label.roles"/></label>
                            <div class="col-sm-10">
                                <div class="row">
                                <c:forEach items="${roles}" var="template">
                                <div class="col-sm-3">
                                        <label for="roles-${template.name.content}">
                                            <input type="checkbox" name="roles" value="${template.externalId}">
                                            ${template.name.content}
                                        </label>
                                </div>
                                </c:forEach>
                            </div>
                            </div>

                        </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Make</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        $(document).ready(function(){
            $("#builder").change(function(){
                if($(this).val()!=""){
                    $("#no-builder-options").hide();
                } else {
                    $("#no-builder-options").show();
                }
            })
        })
    </script>
    <script>
        $(document).ready(function() {
            function updatePermissionsJson() {
                var permissions = $('.permissions-inputs input[type="checkbox"]').filter(function(){
                    return $(this).is(":checked");
                }).map(function() {
                    return $(this).data("permission-name");
                });
                $('#permissions-json').val(JSON.stringify(permissions.toArray()));
            }

            updatePermissionsJson();
            $(".permissions-inputs").click(updatePermissionsJson);
        });
    </script>
    <div class="modal fade" id="edit-role-permissions">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <form method="POST">
                        ${csrf.field()}
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><spanclass="sr-only"></span></button>
                        <h4>Update</h4>
                        <small>Change the permissions of this role</small>
                    </div>
                    <div class="modal-body">
                        <div class="form-group" id="role-description">
                            <label class="col-sm-2 control-label">Description</label>
                            <div class="col-sm-10">
                                <input id="permission-description" bennu-localized-string
                                       required-any name="description" placeholder="Enter a description for this role template." >
                            </div>
                        </div>

                        <input type="text" name="permissions" id="permissions-json" class="hidden">
                        <c:forEach var="permission" items="${allPermissions}">
                            <div class="form-group permissions-inputs">
                                <div class="col-sm-12">
                                    <div class="checkbox">
                                        <input type="checkbox" data-permission-name="${permission.name()}" ${roleTemplate.permissions.get().contains(permission) ? 'checked' : ''}/>
                                        <label class="control-label">${permission.localizedName.content}</label>
                                    </div>
                                    <small class="help-block">
                                    ${permission.localizedDescription.content}</small>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="modal-footer">
                        <button type="reset" class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></button>
                        <button type="submit" class="btn btn-primary"><spring:message code="action.save"/></button>
                    </div>
                </form>
            </div>
        </div>allPermissions
    </div>
</c:if>