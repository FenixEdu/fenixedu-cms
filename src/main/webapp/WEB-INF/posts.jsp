<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1><spring:message code="post.manage.title" /></h1>
<p class="small"><spring:message code="page.manage.label.site" />: <a href="../"><strong>${site.name.content}</strong></a> </p>
<p>
<a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/create" class="btn btn-default btn-primary"><spring:message code="page.manage.label.createPost" /></a>
</p>

<c:choose>
      <c:when test="${posts.size() == 0}">
      <p><spring:message code="page.manage.label.emptyPosts" /></p>
      </c:when>

      <c:otherwise>
        <table class="table table-striped table-bordered">
          <thead>
            <tr>
              <th><spring:message code="page.manage.label.name" /></th>
              <th><spring:message code="page.manage.label.createdBy" /></th>
              <th><spring:message code="page.manage.label.creationDate" /></th>
              <th><spring:message code="page.manage.label.operations" /></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="p" items="${posts}">
            <tr>
              <td>
                <h5>${p.getName().getContent()}</h5>
                <div><small><spring:message code="page.manage.label.url" />:<code>${p.getSlug()}</code></small></div>
              </td>
              <td>${p.createdBy.username}</td>
              <td><joda:format value="${p.getCreationDate()}" pattern="MMM dd, yyyy"/></td>
              <td>
                <div class="btn-group">
                  <a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/${p.slug}/link" class="btn btn-sm btn-default"><spring:message code="action.edit" /></a>
                  <a href="${pageContext.request.contextPath}/${p.site.slug}/${p.slug}" class="btn btn-sm btn-default" target="_blank"><spring:message code="action.link" /></a>
               	  <a href="#" class="btn btn-danger btn-sm" onclick="document.getElementById('deleteForm').submit();"><spring:message code="action.delete" /></a>
               	  <form id="deleteForm" action="${pageContext.request.contextPath}/cms/posts/${site.slug}/${p.slug}/delete" method="POST"></form>
                </div>

              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
