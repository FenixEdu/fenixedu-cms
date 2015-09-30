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
<%@taglib uri="http://fenixedu.com/cms/permissions" prefix="permissions" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
${portal.toolkit()}

<div class="page-header">
    <h1>Settings</h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small></h2>
</div>

<form class="form-horizontal" action="" method="post" role="form">
    ${csrf.field()}
    <div role="tabpanel">
        <p>
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation" class="active"><a href="#general" aria-controls="general" role="tab" data-toggle="tab">General</a></li>
                <li role="presentation"><a href="#permissions" aria-controls="permissions" role="tab" data-toggle="tab">Permissions</a></li>
                <c:if test="${permissions:canDoThis(site, 'MANAGE_ANALYTICS')}">
                    <li role="presentation"><a href="#external" aria-controls="external" role="tab" data-toggle="tab">External Applications</a></li>
                </c:if>
                <li role="presentation"><a href="#io" aria-controls="external" role="tab" data-toggle="tab">Import/Export</a></li>
            </ul>
        </p>
    </div>
    <div class="tab-content">
        <div role="tabpanel" class="tab-pane active form-horizontal" id="general">
            <div class="row">

                    <div class="col-sm-8">
                        <div class="${emptyName ? "form-group has-error" : "form-group"}">
                            <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.edit.label.slug"/></label>

                            <div class="col-sm-4">
                                <div class="input-group">
                                    <span class="input-group-addon"><code>${site.folder == null ? '' : site.folder.functionality.fullPath}/</code></span>
                                    <input required type="text" name="newSlug" class="form-control" id="inputEmail3" placeholder="<spring:message code="site.edit.label.slug" />" value='${site.slug}' ${permissions:canDoThis(site, 'CHOOSE_PATH_AND_FOLDER') ? '' : 'disabled'} \>
                                </div>
                            </div>

                        </div>

                        <div class="${emptyName ? "form-group has-error" : "form-group"}">
                            <label for="site-name" class="col-sm-2 control-label"><spring:message code="site.edit.label.name"/></label>

                            <div class="col-sm-10">
                                <input bennu-localized-string required-any type="text" name="name" class="form-control"
                                    id="site-name" placeholder="<spring:message code="site.edit.label.name" />" value='${site.name.json()}' \>
                                <c:if test="${emptyName != null}">
                                    <p class="text-danger"><spring:message code="site.edit.error.emptyName"/></p>
                                </c:if>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="site-description" class="col-sm-2 control-label"><spring:message code="site.edit.label.description"/></label>

                            <div class="col-sm-10">
                                <textarea id="site-description" bennu-localized-string required-any name="description" class="form-control" rows="3">${site.description.json()}</textarea>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="theme" class="col-sm-2 control-label"><spring:message code="site.edit.label.theme"/></label>

                            <div class="col-sm-10">
                                <select name="theme" id="theme" class="form-control" ${permissions:canDoThis(site, 'CHANGE_THEME') ? '' : 'disabled'}>
                                    <c:forEach var="theme" items="${themes}">
                                        <option value="${theme.type}" ${theme == site.theme ? 'selected' : ''}>${theme.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="folder" class="col-sm-2 control-label"><spring:message code="site.edit.label.folder"/></label>

                            <div class="col-sm-10">
                                <select name="folder" id="" class="form-control" ${permissions:canDoThis(site, 'CHOOSE_PATH_AND_FOLDER') ? '' : 'disabled'}>
                                    <option value ${site.folder == null ? 'selected': ''}>--</option>

                                    <c:forEach items="${folders}" var="folder">
                                        <option value="${folder.externalId}" ${site.folder == folder ? 'selected': ''}>${folder.functionality.description.content}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-sm-2 control-label">Homepage:</label>

                            <div class="col-sm-10">
                                <select name="initialPageSlug" class="form-control" ${permissions:canDoThis(site, 'CHOOSE_DEFAULT_PAGE') ? '' : 'disabled'}>
                                    <option value="---null---">-</option>
                                    <c:forEach var="p" items="${site.pages}">
                                        <option ${p == site.initialPage ? 'selected' : ''} value="${p.slug}">${p.name.content}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-sm-2 control-label">Who can view this site:</label>

                            <div class="col-sm-10">
                                <input bennu-group name="viewGroup" type="text" value='${site.getCanViewGroup().getExpression()}'/>
                            </div>
                        </div>

                    </div>
                    <div class="col-sm-4">
                        <div class="panel panel-primary">
                            <div class="panel-heading">Information</div>
                            <div class="panel-body">
                                <dl>
                                    <dt><spring:message code="site.create.label.published"/></dt>
                                    <c:choose>
                                        <c:when test="${permissions:canDoThis(site, 'PUBLISH_SITE')}">
                                            <dd> <input name="published" type="checkbox" value="true" ${site.published ? "checked='checked'" : ""}></dd>
                                        </c:when>
                                        <c:when test="${site.published}">
                                            <dd><span class="label label-success">Published</span></dd>
                                        </c:when>
                                        <c:otherwise>
                                            <dd><span class="label label-default">NOT Published</span></dd>
                                        </c:otherwise>
                                    </c:choose>
                                    <dt>Created by</dt>
                                    <dd>${site.createdBy.username}</dd>
                                    <dt>Created at</dt>
                                    <dd>${site.creationDate.toString('dd MMM, yyyy HH:mm:ss')}</dd>
                                </dl>
                            </div>
                        </div>

                        <div class="panel panel-danger">
                            <div class="panel-heading"><spring:message code="site.edit.danger.zone" /></div>
                            <div class="panel-body">
                                <p class="help-block">Once you delete a site, there is no going back. Please be certain.</p>
                                <a href="#" data-toggle="modal" data-target="#confirmDeleteModal" class="btn btn-danger">Delete this Site </a>
                            </div>
                        </div>

                    </div>

            </div>
        </div>
        <div role="tabpanel" class="tab-pane form-horizontal" id="permissions">
            <ul class="list-group">
                <c:forEach var="role" items="${site.roles}">
                    <li class="list-group-item">
                        <h3><a href="${pageContext.request.contextPath}/cms/permissions/${role.roleTemplate.externalId}/${role.externalId}/edit" target="_blank">${role.name.content}</a></h3>
                        <c:if test="${not role.name.equals(role.roleTemplate.description)}">
                            <p><small>${role.roleTemplate.description.content}</small></p>
                        </c:if>
                        <p><span class="label label-primary">${role.group.toGroup().members.count()} Users</span></p>
                        <a href='${pageContext.request.contextPath}/cms/permissions/${role.roleTemplate.externalId}/${role.externalId}/edit' target="_blank" class="btn btn-sm btn-default"><span class="glyphicon glyphicon-edit"></span>&nbsp;Edit</a>
                    </li>
                </c:forEach>

            </ul>
        </div>

        <c:if test="${permissions:canDoThis(site, 'MANAGE_ANALYTICS')}">
            <div role="tabpanel" class="tab-pane form-horizontal" id="external">
                <c:if test="${google.isConfigured()}">
                    <h3>Google</h3>

                    <c:if test="${googleUser == null}">
                        <p class="help-block">To use <a href="http://www.google.com/analytics/">Analytics</a>, <a href="https://plus.google.com">Plus</a>, <a href="https://maps.google.com">Maps</a> and other integrationss from Google you need to connect your site with your Google account.</p>
                        <a class="btn btn-primary" href="${google.getAuthenticationUrlForUser(user)}">Connect with Google</a>
                    </c:if>

                    <c:if test="${googleUser != null}">
                        <div class="form-group">
                            <label for="inputEmail3" class="col-sm-2 control-label">Analytics Property</label>
                            <div class="col-sm-10">
                                <span class="property"></span> <a class="btn btn-xs btn-default" data-toggle="modal" href='#select-property'>Select property</a>
                                <p class="help-block">Integrate this site with Google Analytics so you can receive information about the number of visits and correlate them with your posting history. </p>
                            </div>
                        </div>

                        <div class="modal fade" id="select-property">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal"><span class="sr-only">Close</span></button>
                                        <h3 class="modal-title">Select Property</h3>
                                        <small>Choose one of your properties</small>
                                    </div>
                                    <div class="modal-body">

                                        <div class="form-group">
                                                <label class="col-sm-2 control-label">Property</label>
                                                <div class="col-sm-10">
                                                    <div class="row">
                                                        <div class="col-sm-6">
                                                            <select name="accountId" class="form-control" value="${site.analyticsAccountId}">
                                                                <option class="form-control" value="">---</option>
                                                                <c:forEach var="account" items="${accounts}">
                                                                    <option value="${account.id}" class="account" ${account.getId().equals(site.analyticsAccountId) ? 'selected' : ''} class="form-control">${account.name}</option>
                                                                </c:forEach>
                                                            </select>
                                                        </div>
                                                        <div class="col-sm-6">
                                                            <select name="analyticsCode" class="form-control" value="${site.getAnalyticsCode()}">
                                                                <option class="form-control" value="">---</option>
                                                                <c:forEach var="account" items="${accounts}">
                                                                    <c:forEach var="property" items="${account.properties}">
                                                                        <option value="${property.id}" data-account="${account.id}" class="google-property" ${property.getId().equals(site.getAnalyticsCode()) ? 'selected' : ''} class="form-control">${property.name}</option>
                                                                    </c:forEach>
                                                                </c:forEach>
                                                            </select>
                                                        </div>

                                                    </div>
                                                    <p class="help-block">
                                                        If you need to create a new property for this site, go to <a href="https://www.google.com/analytics">Google Analytics</a> create it there and then selected it here.
                                                    </p>
                                                </div>

                                        </div>

                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                                        <button type="submit" class="save btn btn-primary">Save changes</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <script src="https://apis.google.com/js/client.js?onload=authorize"></script>
                    </c:if>
                </c:if>
            </div>
        </c:if>


        <div role="tabpanel" class="tab-pane form-horizontal" id="io">
            <div class="form-group">
                <label class="col-sm-12 control-label">Create a clone of your site <button type="button" onclick="$('#clone-form').submit();" class="btn btn-default">Clone</button></label>
                <label class="col-sm-12 control-label">Export your site<a href="export" class="btn btn-default">Export</a></label>
            </div>
        </div>

    </div>

    <div class="form-group">
        <div class="col-sm-12">
            <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.save"/></button>
            <a href="${pageContext.request.contextPath}/cms/sites/${site.slug}" class="btn btn-default"><spring:message
                    code="action.cancel"/></a>
        </div>
    </div>

</form>
<form id="clone-form" method="post" action="clone">${csrf.field()}</form>

<div class="modal fade" id="confirmDeleteModal" role="dialog" aria-hidden="true">
    <form method="post" action="${pageContext.request.contextPath}/cms/sites/${site.slug}/delete">
        ${csrf.field()}
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span class="sr-only">Close</span></button>
                    <h3 class="modal-title">Delete Site</h3>
                    <small>Are you sure?</small>
                </div>
                <div class="modal-body">
                    You are about to delete the site '<c:out value="${site.name.content}" />'. You will also be deleting all content, including ${site.postSet.size() } posts. There is no way to rollback this opeartion. Are you sure?
                </div>
                <div class="modal-footer">

                    <button type="button" data-dismiss="modal" class="btn btn-default"><spring:message
                            code="action.cancel"/></button>
                    <button type="submit" class="btn btn-danger"><spring:message code="action.delete"/></button>
                </div>
            </div>
        </div>
    </form>
</div>


<script type="application/javascript">
    $(document).ready(function(){
        if(window.location.hash) $("a[href='" + window.location.hash +"']").tab('show');

        $('[name="accountId"]').change(function(){
            var accountId = $('[name="accountId"]').val();
            $('[name="analyticsCode"]').prop("disabled", !accountId)
            $('[name="analyticsCode"]').val("");
            $('.google-property')
                .each(function() {
                    if($(this).data('account') == accountId) {
                        $(this).show();
                    } else {
                        $(this).hide();
                    }
                });
        });

        function initGoogleAccounts() {
            var accountId = $('[name="accountId"]').val();
            $('[name="analyticsCode"]').prop("disabled", !accountId)
            $('.google-property')
                .each(function() {
                    if($(this).data('account') == accountId) {
                        $(this).show();
                    } else {
                        $(this).hide();
                    }
                });
        }
        
        initGoogleAccounts();

    });
</script>