<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1><spring:message code="page.manage.title" /></h1>
<p class="small"><spring:message code="page.manage.label.site" />: <a href="../"><strong>${site.name.content}</strong></a> </p>
<p>
<a href="pages/create" class="btn btn-default btn-primary"><spring:message code="page.manage.label.createPage" /></a>
</p>

<c:choose>
      <c:when test="${pages.size() == 0}">
      <p><spring:message code="page.manage.label.emptyPages" /></p>
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
          <c:forEach var="p" items="${pages}">
            <tr>
              <td>
                <h5><a  target="_blank" href="${pageContext.request.contextPath}/${p.site.slug}/${p.slug}">${p.getName().getContent()}</a></h5>
                <div><small><spring:message code="page.manage.label.url" />:<code>${p.getSlug()}</code></small></div>
              </td>
              <td>${p.createdBy.username}</td>
              <td><joda:format value="${p.getCreationDate()}" pattern="MMM dd, yyyy"/></td>
              <td>
                <div class="btn-group">
                  <a href="pages/${p.slug}/edit" class="btn btn-sm btn-default"><spring:message code="action.edit" /></a>
                  <a href="${pageContext.request.contextPath}/${p.site.slug}/${p.slug}" class="btn btn-sm btn-default" target="_blank"><spring:message code="action.link" /></a>
				  <a href="#" class="btn btn-danger btn-sm" onclick="document.getElementById('deletePageForm').submit();"><spring:message code="action.delete" /></a>
               	  <form id="deletePageForm" action="pages/${p.slug}/delete" method="POST"></form>
                </div>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
