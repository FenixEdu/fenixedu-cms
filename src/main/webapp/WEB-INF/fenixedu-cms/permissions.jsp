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
    <h1>Permissions</h1>
    <h2><a href="${pageContext.request.contextPath}/cms"><small>Manage global permissions</small></a></h2>
</div>

<p>
    <button class="btn btn-default btn-primary" data-target="#create-modal" data-toggle="modal">
        <span class="glyphicon glyphicon-plus"></span>&nbsp;Create
    </button>
</p>

<c:choose>
    <c:when test="${templates.size() == 0}">
        <div class="panel panel-default">
            <div class="panel-body">
                <i>There are no templates for roles.</i>
            </div>
        </div>
    </c:when>

    <c:otherwise>
        <ul class="list-group">
            <c:forEach var="template" items="${templates}">
                <li class="list-group-item">
                    <h3><a href="${pageContext.request.contextPath}/cms/permissions/${template.externalId}/edit">${template.description.content}</a></h3>

                    <span class="label label-primary">${template.numSites} Sites</span>

                    <div class="btn-group pull-right">
                        <a href="${pageContext.request.contextPath}/cms/permissions/${template.externalId}/edit" class="btn btn-icon btn-primary">
                            <i class="glyphicon glyphicon-cog"></i>
                        </a>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>

<div class="modal fade" id="create-modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form method="POST" action="${pageContext.request.contextPath}/cms/permissions/create">
                ${csrf.field()}
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><spanclass="sr-only"></span></button>
                    <h4>Create</h4>
                    <small>Create a new Role template that can be used by other sites</small>
                </div>  


                <div class="modal-body">
                    <div class="form-group" id="role-description">
                        <label class="col-sm-2 control-label">Description</label>
                        <div class="col-sm-10">
                            <input bennu-localized-string required-any name="description" placeholder="Enter a description for this role template.">
                        </div>
                    </div>

                    <c:forEach var="permission" items="${allPermissions}">
                        <div class="form-group permissions-inputs">
                            <label class="col-sm-8 control-label">${permission.localizedName.content}</label>
                            <div class="col-sm-4">
                                <div class="checkbox">
                                    <input type="checkbox" data-permission-name="${permission.name()}"></div>
                                </div>
                            </div>
                    </c:forEach>

                    <input type="text" name="permissions" id="permissions-json" class="hidden">

                </div>


                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><spring:message code="action.create"/></button>
                    <button type="reset" class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></button>
                </div>
            </form>

        </div>

    </div>
</div>

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