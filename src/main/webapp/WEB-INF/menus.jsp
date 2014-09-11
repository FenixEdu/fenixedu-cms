<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1><spring:message code="menu.manage.title" /></h1>
<p class="small"><spring:message code="menu.manage.label.site" />: <strong><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></strong> </p>
<p>
<a href="${pageContext.request.contextPath}/cms/menus/${site.slug}/create" class="btn btn-default btn-primary"><spring:message code="menu.manage.label.createMenu" /></a>
</p>

<c:choose>
      <c:when test="${menus.size() == 0}">
      <p><spring:message code="menu.manage.title.label.emptyMenus" /></p>
      </c:when>

      <c:otherwise>
        <table class="table table-striped table-bordered">
          <thead>
            <tr>
              <th><spring:message code="menu.manage.label.name" /></th>
              <th><spring:message code="menu.manage.label.createdBy" /></th>
              <th><spring:message code="menu.manage.label.creationDate" /></th>
              <th><spring:message code="menu.manage.label.operations" /></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="m" items="${menus}">
            <tr>
              <td>
                <h5>${m.name.content}</h5>
              </td>
              <td>${m.createdBy.username}</td>
              <td><joda:format value="${m.getCreationDate()}" pattern="MMM dd, yyyy"/></td>
              <td>
              	<div class="btn-group">
	                <a href="${pageContext.request.contextPath}/cms/menus/${site.slug}/${m.oid}/change" class="btn btn-sm btn-default"><spring:message code="action.change" /></a>
	                <a href="#" class="btn btn-danger btn-sm" onclick="document.getElementById('deleteMenuForm').submit();"><spring:message code="action.delete" /></a>
					<form id="deleteMenuForm" action="${pageContext.request.contextPath}/cms/menus/${site.slug}/${m.oid}/delete" method="POST"></form>
				</div>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
