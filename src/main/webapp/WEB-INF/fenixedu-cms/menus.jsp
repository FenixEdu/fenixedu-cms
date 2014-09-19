<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h2 class="page-header" style="margin-top: 0">
  <spring:message code="menu.manage.title" />
  <small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a> </small>
</h2>

<p>
<a href="${pageContext.request.contextPath}/cms/menus/${site.slug}/create" class="btn btn-default btn-primary"><spring:message code="menu.manage.label.createMenu" /></a>
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
	                <a href="#" class="btn btn-danger btn-sm" onclick="document.getElementById('deleteMenuForm${menu.externalId}').submit();"><spring:message code="action.delete" /></a>
					<form id="deleteMenuForm${menu.externalId}" action="${pageContext.request.contextPath}/cms/menus/${site.slug}/${menu.oid}/delete" method="POST"></form>
				</div>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
