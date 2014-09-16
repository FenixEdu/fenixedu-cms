<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<h2 class="page-header" style="margin-top: 0">
  <spring:message code="categories.manage.title" />
  <small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a> </small>
</h2>

<a href="${pageContext.request.contextPath}/cms/categories/${site.slug}/create" class="btn btn-default btn-primary"><spring:message code="categories.manage.createCategories"/></a>
</p>

<c:choose>
      <c:when test="${categories.size() == 0}">
      <i><spring:message code="categories.manage.emptyCategories"/></i>
      </c:when>

      <c:otherwise>
        <table class="table table-striped">
          <thead>
            <tr>
              <th><spring:message code="categories.manage.label.name"/></th>
              <th><spring:message code="categories.manage.label.creationDate"/></th>
              <th>Posts</th>
              <th><spring:message code="categories.manage.label.operations"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="category" items="${categories}">
            <tr>
              <td>
                <h5>
                  <c:if test="${category.address != null}">
                    <a href="${category.address}" target="_blank">${category.name.content}</a>
                  </c:if>
                  <c:if test="${category.address == null}">
                    ${category.name.content}
                  </c:if>
                </h5>
                <div><small><spring:message code="categories.manage.label.url"/>:<code>${category.getSlug()}</code></small></div>
              </td>
              <td>${category.creationDate.toString('dd MMMM yyyy, HH:mm', locale)} <small>- ${category.createdBy.name}</small></td>
              <td>${category.postsSet.size()}</td>
              <td>
	               <button class="btn btn-danger btn-sm" onclick="document.getElementById('deleteCategoryForm${category.externalId}').submit();" ${category.postsSet.size() > 0 ? 'disabled' : ''}><spring:message code="action.delete"/></button>
					       <form id="deleteCategoryForm${category.externalId}" action="${pageContext.request.contextPath}/cms/categories/${c.site.slug}/${c.slug}/delete" method="POST"></form>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
