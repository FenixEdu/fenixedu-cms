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
<%@taglib uri="http://fenixedu.com/cms/permissions" prefix="permissions" %>
<%@ taglib uri="http://fenixedu.org/taglib/intersection" prefix="modular" %>
${portal.toolkit()}

<div class="page-header">
    <h1>Site Builders
        <small>
            <ol class="breadcrumb">
                <a href="${pageContext.request.contextPath}/cms/sites">Content Manager</a>
            </ol>
        </small>
    </h1>
</div>

<div class="row">
    <div class="col-sm-12">
        <p class="well">Site builders are used to initialize new sites with default themes, roles and folders.
            System builders can also create default content for your site.</p>
         </div>
    </div>
<div class="col-sm-12">
    <p>
        <c:if test="${cmsSettings.canManageSettings()}">
                <button type="button" class="btn btn-primary"
            data-toggle="modal" data-target="#create-builder"><i class="glyphicon glyphicon-plus"></i> New</button>
        </c:if>
    </p>
</div>
</div>


<table class="table">
    <c:forEach items="${siteBuilders}" var="builder">
        <tr>
            <td >
                ${builder.slug}
            </td>
            <td >
                <a href="${pageContext.request.contextPath}/cms/builders/${builder.slug}" class="btn btn-default">
                    <span class="glyphicon glyphicon-pencil"></span> Edit</a>
                <c:if test="${builder.systemBuilder}">
                <span class="badge"><span class="glyphicon glyphicon-wrench"></span> <small>System Builder</small></span>
                </c:if>
            </td>
        </tr>
    </c:forEach>
</table>

<div class="modal fade" id="create-builder" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form class="form-horizontal" action="${pageContext.request.contextPath}/cms/builders/new" method="post" role="form">
                ${csrf.field()}
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only"></span></button>
                    <h3>Create</h3>
                    <small>Create a new site slug</small>
                </div>
                <div class="modal-body">
                    <div class="${emptyName ? "form-group has-error" : "form-group"}">
                        <div class="col-sm-12">
                            <label for="slug"><spring:message code='site.create.label.name' var="create.label.name"/>Slug</label>
                            <input type="text" name="builderSlug" class="form-control" id="slug"
                                   placeholder="${create.label.slug}">
                        </div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Create</button>
                </div>
            </form>
        </div>
    </div>
</div>
