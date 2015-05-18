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
  <spring:message code="menu.manage.title" />
  <small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a> </small>
</h2>

<p>
    <a href="${pageContext.request.contextPath}/cms/menus/${site.slug}/create" class="btn btn-default btn-primary">
        <span class="glyphicon glyphicon-plus"></span>&nbsp;<spring:message code="menu.manage.label.createMenu" />
    </a>
</p>

<c:choose>
      <c:when test="${menus.size() == 0}">
      <i><spring:message code="menu.manage.title.label.emptyMenus" /></i>
      </c:when>

      <c:otherwise>
        <table class="table table-striped">
          <thead>
            <tr>
              <th><spring:message code="menu.manage.label.name" /></th>
              <th><spring:message code="menu.manage.label.creationDate" /></th>
              <th><spring:message code="menu.manage.label.operations" /></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="menu" items="${menus}">
            <tr>
              <td>
                <h5>${menu.name.content}</h5>
              </td>
              <td>${menu.creationDate.toString('dd MMMM yyyy, HH:mm', locale)} <small>- ${menu.createdBy.name}</small></td>
              <td>
              	<div class="btn-group">
	                <a href="${pageContext.request.contextPath}/cms/menus/${site.slug}/${menu.oid}/change" class="btn btn-sm btn-default"><spring:message code="action.manage" /></a>
	                <a href="#" class="btn btn-danger btn-sm" data-menu="${menu.oid}"><spring:message code="action.delete" /></a>
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
                <h4><spring:message code="menu.manage.label.delete.menu"/></h4>
            </div>
            <div class="modal-body">
                <p><spring:message code="menu.manage.label.delete.menu.message"/></p>
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

<script>
(function () {
    $("a[data-menu]").on('click', function(el) {
        var menu = el.target.getAttribute('data-menu');
        $('#deleteForm').attr('action', '${pageContext.request.contextPath}/cms/menus/${site.slug}/' + menu + '/delete');
        $('#deleteModal').modal('show');
    });
})();
</script>