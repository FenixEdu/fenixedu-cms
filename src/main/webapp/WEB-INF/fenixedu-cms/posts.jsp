<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h2 class="page-header" style="margin-top: 0">
  <spring:message code="post.manage.title" />
  <small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a> </small>
</h2>
<p>
<a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/create" class="btn btn-default btn-primary"><spring:message code="page.manage.label.createPost" /></a>
</p>

<c:choose>
      <c:when test="${posts.size() == 0}">
      <p><spring:message code="page.manage.label.emptyPosts" /></p>
      </c:when>

      <c:otherwise>
        <table class="table table-striped">
          <thead>
            <tr>
              <th><spring:message code="page.manage.label.name" /></th>
              <th><spring:message code="page.manage.label.creationDate" /></th>
              <th><spring:message code="site.manage.label.categories"/></th>
              <th><spring:message code="page.manage.label.operations" /></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="post" items="${posts}">
            <tr>
              <td>
                <h5><a href="${post.address}" target="_blank">${post.name.content}</a></h5>
                <div><small><spring:message code="page.manage.label.url" />:<code>${post.slug}</code></small></div>
              </td>
              <td>${post.creationDate.toString('dd MMMM yyyy, HH:mm', locale)} <small>- ${post.createdBy.name}</small></td>
              <td>
                <c:forEach var="cat" items="${post.categoriesSet}">
                  <a href="${pageContext.request.contextPath}/cms/categories/${site.slug}" class="badge">${cat.name.content}</a>
                </c:forEach></td>
              <td>
                <div class="btn-group">
                  <a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/${post.slug}/edit" class="btn btn-sm btn-default"><spring:message code="action.edit" /></a>
               	  <a href="#" class="btn btn-danger btn-sm" onclick="document.getElementById('deleteForm${post.externalId}').submit();"><spring:message code="action.delete" /></a>
               	  <form id="deleteForm${post.externalId}" action="${pageContext.request.contextPath}/cms/posts/${site.slug}/${post.slug}/delete" method="POST"></form>
                </div>

              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
