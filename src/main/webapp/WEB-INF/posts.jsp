<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<h1>Manage Posts</h1>
<p class="small">Site: <a href="../"><strong>${site.name.content}</strong></a> </p>
<p>
<a href="posts/create" class="btn btn-default btn-primary">Create Post</a>
</p>

<c:choose>
      <c:when test="${posts.size() == 0}">
      <p>There are no posts created for this site.</p>
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
          <c:forEach var="p" items="${posts}">
            <tr>
              <td>
                <h5>${p.getName().getContent()}</h5>
                <div><small>Url:<code>${p.getSlug()}</code></small></div>
              </td>
              <td>${p.createdBy.username}</td>
              <td><joda:format value="${p.getCreationDate()}" pattern="MMM dd, yyyy"/></td>
              <td>
                <div class="btn-group">
                  <a href="posts/${p.slug}/link" class="btn btn-sm btn-default">Edit</a>
                  <a href="${pageContext.request.contextPath}/${p.site.slug}/${p.slug}" class="btn btn-sm btn-default" target="_blank">Link</a>
                </div>

                <a href="posts/${p.slug}/delete" class="btn btn-danger btn-sm">Delete</a>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
