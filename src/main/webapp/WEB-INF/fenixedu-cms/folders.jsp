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
${portal.toolkit()}

<div class="page-header">
  <h1>Sites</h1>
  <h2><a href="sites"><small><spring:message code="folder.manage.label.folders"/></small></a></h2>
</div>

<p>
    <a href="${pageContext.request.contextPath}/cms" class="btn btn-default">« <spring:message code="action.back"/></a>
    <button class="btn btn-primary" data-toggle="modal" data-target="#newFolderModal">
        <span class="glyphicon glyphicon-plus"></span>&nbsp;New</a>
    </button>
</p>

<ul class="list-group">
    <c:forEach var="folder" items="${folders}">
        <li class="list-group-item">
            <h3>
                <a href="${pageContext.request.contextPath}/cms/folders/resolver/${folder.externalId}">${folder.functionality.description.content}</a>
            </h3>
            
            <div><code>${folder.functionality.fullPath}</code></div>

            <c:if test="${folder.resolver == null}">
                <span class="label label-default">Static Resolver</span>
            </c:if>
            <c:if test="${folder.resolver != null}">
                <span class="label label-primary">Custom Resolver</span>
            </c:if>
            
            <div class="btn-group pull-right">
                <a href="${pageContext.request.contextPath}/cms/folders/resolver/${folder.externalId}" class="btn btn-icon btn-primary pull-right">
                    <i class="glyphicon glyphicon-cog"></i>
                </a>
            </div>
        </li>
    </c:forEach>
</ul>

<div class="modal fade" id="newFolderModal" tabindex="-1" role="dialog" aria-hidden="true">
    <form class="form-horizontal" method="post">
        ${csrf.field()}
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                            class="sr-only">Close</span></button>
                    <h4><spring:message code="folder.manage.label.new.folder"/></h4>
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