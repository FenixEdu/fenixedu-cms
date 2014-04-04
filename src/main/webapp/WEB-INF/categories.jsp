<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<h1><spring:message code="categories.manage.title"/></h1>
<p class="small"><spring:message code="categories.manage.site"/>: <a href="../"><strong>${site.name.content}</strong></a> </p>
<p>
<a href="categories/create" class="btn btn-default btn-primary"><spring:message code="categories.manage.createCategories"/></a>
</p>

<c:choose>
      <c:when test="${categories.size() == 0}">
      <p><spring:message code="categories.manage.emptyCategories"/></p>
      </c:when>

      <c:otherwise>
        <table class="table table-striped table-bordered">
          <thead>
            <tr>
              <th><spring:message code="categories.manage.label.name"/></th>
              <th><spring:message code="categories.manage.label.createdBy"/></th>
              <th><spring:message code="categories.manage.label.creationDate"/></th>
              <th><spring:message code="categories.manage.label.operations"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="c" items="${categories}">
            <tr>
              <td>
                <h5><a target="_blank" href="${pageContext.request.contextPath}/${c.site.slug}/${c.slug}">${c.getName().getContent()}</a></h5>
                <div><small><spring:message code="categories.manage.label.url"/>:<code>${c.getSlug()}</code></small></div>
              </td>
              <td>${c.createdBy.username}</td>
              <td><joda:format value="${c.getCreationDate()}" pattern="MMM dd, yyyy"/></td>
              <td>
              	<div class="btn-group">
	                <a href="categories/${c.slug}/stuff" class="btn btn-sm btn-default"><spring:message code="action.stuff"/></a>
	                <a href="${pageContext.request.contextPath}/${c.site.slug}/${c.slug}" class="btn btn-sm btn-default" target="_blank"><spring:message code="action.link"/></a>
	                <a href="#" class="btn btn-danger btn-sm" onclick="document.getElementById('deleteCategoryForm').submit();"><spring:message code="action.delete"/></a>
					<form id="deleteCategoryForm" action="categories/${c.slug}/delete" method="POST"></form>
				</div>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
