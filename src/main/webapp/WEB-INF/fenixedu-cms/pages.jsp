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

<h2 class="page-header" style="margin-top: 0">
  <spring:message code="page.manage.title" />
  <small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a> </small>
</h2>

<p>
    <a href="${pageContext.request.contextPath}/cms/pages/${site.slug}/create" class="btn btn-default btn-primary"><spring:message
            code="page.manage.label.createPage"/></a>
    <a href="#" data-toggle="modal" data-target="#defaultModal" class="btn btn-default"><spring:message
            code="page.manage.label.change.default.page"/></a>
</p>

<c:choose>
    <c:when test="${pages.size() == 0}">
        <i><spring:message code="page.manage.label.emptyPages"/></i>
    </c:when>

    <c:otherwise>
        <table class="table table-striped">
            <thead>
            <tr>
                <th><spring:message code="page.manage.label.name"/></th>
                <th><spring:message code="page.manage.label.creationDate"/></th>
                <th><spring:message code="page.manage.label.operations"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="page" items="${pages}">
                <tr>
                    <td>
                        <h5><a target="_blank"
                               href="${page.address}">${page.name.content}</a>
                            <c:if test="${page.site.initialPage == page}">
                                <span class="label label-success"><spring:message code="site.manage.label.default"/></span>
                            </c:if>
                        </h5>

                        <div>
                            <small><spring:message code="page.manage.label.url"/>:
                                <c:choose>
                                    <c:when test="${page.slug != ''}">
                                        <code>${page.slug}</code>
                                    </c:when>
                                    <c:otherwise>
                                        <code>-</code>
                                    </c:otherwise>
                                </c:choose>
                            </small>
                        </div>
                    </td>
                    <td>${page.creationDate.toString('dd MMMM yyyy, HH:mm', locale)} <small>- ${page.createdBy.name}</small></td>
                    <td>
                        <div class="btn-group">
                            <c:choose>
                                <c:when test="${page.slug != ''}">
                                    <a href="${pageContext.request.contextPath}/cms/pages/${page.site.slug}/${page.slug}/edit"
                                       class="btn btn-sm btn-default"><spring:message code="action.edit"/></a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/cms/pages/${page.site.slug}/--**--/edit"
                                       class="btn btn-sm btn-default"><spring:message code="action.edit"/></a>
                                </c:otherwise>
                            </c:choose>
                            <a href="#" class="btn btn-danger btn-sm" data-page="${page.slug}"><spring:message
                                    code="action.delete"/></a>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4><spring:message code="page.manage.label.delete.page"/></h4>
            </div>
            <div class="modal-body">
                <p><spring:message code="page.manage.label.delete.page.message"/></p>
            </div>
            <div class="modal-footer">
                <form id="deleteForm" method="POST">
                    <button type="submit" class="btn btn-danger"><spring:message code="action.delete"/></button>
                    <a class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></a>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="defaultModal" tabindex="-1" role="dialog" aria-hidden="true">
    <form action="${site.slug}/defaultPage" class="form-horizontal" method="post">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                            class="sr-only">Close</span></button>
                    <h4><spring:message code="action.change.default"/></h4>
                </div>
                <div class="modal-body">

                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="label.page"/>:</label>

                        <div class="col-sm-10">
                            <select name="page" class="form-control">
                                <option value="---null---">-</option>
                                <c:forEach var="p" items="${pages}">
                                    <option value="${p.slug}">${p.name.content}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><spring:message code="label.save"/></button>
                </div>
            </div>
        </div>
    </form>
</div>

<script>
(function () {
    $("a[data-page]").on('click', function(el) {
        var pageSlug = el.target.getAttribute('data-page');
        $('#deleteForm').attr('action', '${pageContext.request.contextPath}/cms/pages/${site.slug}/' + pageSlug + '/delete');
        $('#deleteModal').modal('show');
    });
})();
</script>