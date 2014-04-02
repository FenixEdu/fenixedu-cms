<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<h1>Manage Categories</h1>
<p class="small">Site: <a href="../"><strong>${site.name.content}</strong></a> </p>
<p>
<a href="categories/create" class="btn btn-default btn-primary">Create Categories</a>
</p>

<c:choose>
      <c:when test="${categories.size() == 0}">
      <p>There are no categories created for this site.</p>
      </c:when>

      <c:otherwise>
        <table class="table table-striped table-bordered">
          <thead>
            <tr>
              <th>Name</th>
              <th>Created By</th>
              <th>Creation Date</th>
              <th>Operations</th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="c" items="${categories}">
            <tr>
              <td>
                <h5><a target="_blank" href="${pageContext.request.contextPath}/${c.site.slug}/${c.slug}">${c.getName().getContent()}</a></h5>
                <div><small>Url:<code>${c.getSlug()}</code></small></div>
              </td>
              <td>${c.createdBy.username}</td>
              <td><joda:format value="${c.getCreationDate()}" pattern="MMM dd, yyyy"/></td>
              <td>
              	<div class="btn-group">
	                <a href="#" class="btn btn-danger btn-sm" onclick="document.getElementById('deleteCategoryForm').submit();">Delete</a>
	                <a href="categories/${c.slug}/stuff" class="btn btn-sm btn-default">Stuff</a>
	                <a href="${pageContext.request.contextPath}/${c.site.slug}/${c.slug}" class="btn btn-sm btn-default" target="_blank">Link</a>
					<form id="deleteCategoryForm" action="categories/${c.slug}/delete" method="POST"></form>
				</div>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
