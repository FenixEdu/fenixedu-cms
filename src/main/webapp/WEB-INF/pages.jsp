<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<h1>Manage Pages</h1>
<p class="small">Site: <a href="../"><strong>${site.name.content}</strong></a> </p>
<p>
<a href="pages/create" class="btn btn-default btn-primary">Create Page</a>
</p>

<c:choose>
      <c:when test="${pages.size() == 0}">
      <p>There are no pages created for this site.</p>
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
          <c:forEach var="p" items="${pages}">
            <tr>
              <td>
                <h5><a  target="_blank" href="${pageContext.request.contextPath}/${p.site.slug}/${p.slug}">${p.getName().getContent()}</a></h5>
                <div><small>Url:<code>${p.getSlug()}</code></small></div>
              </td>
              <td>${p.createdBy.username}</td>
              <td><joda:format value="${p.getCreationDate()}" pattern="MMM dd, yyyy"/></td>
              <td>
                <div class="btn-group">
                  <a href="pages/${p.slug}/edit" class="btn btn-sm btn-default">Edit</a>
                  <a href="${pageContext.request.contextPath}/${p.site.slug}/${p.slug}" class="btn btn-sm btn-default" target="_blank">Link</a>
				  <a href="#" class="btn btn-danger btn-sm" onclick="document.getElementById('deletePageForm').submit();">Delete</a>
               	  <form id="deletePageForm" action="pages/${p.slug}/delete" method="POST"></form>
                </div>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
