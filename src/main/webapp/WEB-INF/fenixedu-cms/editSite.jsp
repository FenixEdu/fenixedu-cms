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

<h2 class="page-header" style="margin-top: 0">
    <spring:message code="site.edit.title"/>
    <small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small>
</h2>
<div class="row">
    <form class="form-horizontal" action="" method="post" role="form">
        <div class="col-sm-8">
            <div class="${emptyName ? "form-group has-error" : "form-group"}">
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.edit.label.slug"/></label>

                <div class="col-sm-4">
                    <div class="input-group">

                        <span class="input-group-addon"><code>${site.folder == null ? '' : site.folder.functionality.fullPath}/</code></span>
                        <input required type="text" name="newSlug" class="form-control" id="inputEmail3"
                               placeholder="<spring:message code="site.edit.label.slug" />" value='${site.slug}' \>
                    </div>
                </div>

            </div>

            <div class="${emptyName ? "form-group has-error" : "form-group"}">
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.edit.label.name"/></label>

                <div class="col-sm-10">
                    <input bennu-localized-string required-any type="text" name="name" class="form-control" id="inputEmail3"
                           placeholder="<spring:message code="site.edit.label.name" />" value='${site.name.json()}' \>
                    <c:if test="${emptyName != null}"><p class="text-danger"><spring:message
                            code="site.edit.error.emptyName"/></p></c:if>
                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message
                        code="site.edit.label.description"/></label>

                <div class="col-sm-10">
                    <textarea bennu-localized-string required-any name="description" class="form-control"
                              rows="3">${site.description.json()}</textarea>
                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.edit.label.theme"/></label>

                <div class="col-sm-10">
                    <select name="theme" id="theme" class="form-control">
                        <c:forEach var="i" items="${themes}">
                            <option value="${i.type}" ${i == site.theme ? 'selected' : ''}>${i.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label for="folder" class="col-sm-2 control-label"><spring:message code="site.edit.label.folder"/></label>

                <div class="col-sm-10">
                    <select name="folder" id="" class="form-control">
                        <option value ${site.folder == null ? 'selected': ''}>--</option>

                        <c:forEach items="${folders}" var="folder">
                            <option value="${folder.externalId}" ${site.folder == folder ? 'selected': ''}>${folder.functionality.description.content}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Can View</label>

                <div class="col-sm-10">
                    <input bennu-group allow="public,users,managers,custom" name="viewGroup" type="text"
                           value="${ site.canViewGroup.expression }"/>
                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Can Post</label>

                <div class="col-sm-10">
                    <input bennu-group allow="managers,custom" name="postGroup" type="text"
                           value="${ site.canPostGroup.expression }"/>
                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Can Admin</label>

                <div class="col-sm-10">
                    <input bennu-group allow="managers,custom" name="adminGroup" type="text"
                           value="${ site.canAdminGroup.expression }"/>
                </div>
            </div>

            <div class="form-group">
                <label for="analyticsCode" class="col-sm-2 control-label">Analytics Code</label>

                <div class="col-sm-10">
                    <input type="text" name="analyticsCode" id="analyticsCode" value="${ site.analyticsCode }"
                           class="form-control"/>
                </div>
            </div>

            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.save"/></button>
                    <a href="${pageContext.request.contextPath}/cms/sites/${site.slug}" class="btn btn-default"><spring:message
                            code="action.cancel"/></a>
                </div>
            </div>

        </div>
        <div class="col-sm-4">
            <div class="panel panel-primary">
                <div class="panel-heading">Information</div>
                <div class="panel-body">
                    <dl>
                        <dt><spring:message
                        code="site.create.label.published"/></dt>
                        <dd> <input name="published" type="checkbox" value="true" ${site.published ? "checked='checked'" : ""}></dd>
                        <dt>Created by</dt>
                        <dd>${site.createdBy.username}</dd>
                        <dt>Created at</dt>
                        <dd>${site.creationDate.toString('dd MMM, yyyy HH:mm:ss')}</dd>
                    </dl>
                </div>
            </div>

            <div class="panel panel-danger">
                <div class="panel-heading"><spring:message code="site.edit.danger.zone"/></div>
                <div class="panel-body">
                    <p class="help-block">Once you delete a site, there is no going back. Please be certain.</p>
                    <a href="#" data-toggle="modal" data-target="#confirmDeleteModal" class="btn btn-danger">Delete this Site </a>
                </div>
            </div>

        </div>
    </form>
</div>

<div class="modal fade" id="confirmDeleteModal" tabindex="-1" role="dialog" aria-hidden="true">
    <form method="post" action="${pageContext.request.contextPath}/cms/sites/${site.slug}/delete">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                            class="sr-only">Close</span></button>
                    <h4><spring:message code="site.edit.delete.title"/></h4>
                </div>
                <div class="modal-body">
                    <spring:message code="site.edit.delete.confirm"/>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-danger"><spring:message code="action.delete"/></button>
                    <button type="button" data-dismiss="modal" class="btn btn-primary"><spring:message
                            code="action.cancel"/></button>
                </div>
            </div>
        </div>
    </form>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
${portal.toolkit()}



