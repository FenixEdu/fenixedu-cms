<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<h1>Manage Sites</h1>
<p>
  <a href="${pageContext.request.contextPath}/cms/manage/create" class="btn btn-primary">Create Site</a>
  <a href="${pageContext.request.contextPath}/cms/manage/themes" class="btn btn-default">Manage Themes</a>
</p>

<c:if test="">

</c:if>

<c:choose>
      <c:when test="${sites.size() == 0}">
      There are no sites defined
      </c:when>

      <c:otherwise>
        <table class="table table-striped table-bordered">
      <thead>
        <tr>
          <th class="col-md-6">Name</th>
          <th>Created By</th>
          <th>Creation Date</th>
          <th>Operations</th>
        </tr>
      </thead>
      <tbody>
      <c:forEach var="i" items="${sites}">
        <tr>
          <td>
            <h5>${i.getName().getContent()}</h5>
            <div><small>Url: <code>${i.slug}</code></small></div>
            <div><small>${i.getDescription().getContent()}</small></div>
          </td>
          <td>${i.createdBy.username}</td>
          <td><joda:format value="${i.creationDate}" pattern="MMM dd, yyyy"/></td>
          <td>
            <div class="btn-group">
              <a href="${pageContext.request.contextPath}/cms/manage/${i.slug}/posts" class="btn btn-sm btn-default">Posts</a>

              <a href="${pageContext.request.contextPath}/cms/manage/${i.slug}/pages" class="btn btn-sm btn-default">Pages</a>

              <a href="${pageContext.request.contextPath}/cms/manage/${i.slug}/categories" class="btn btn-sm btn-default">Categories</a>

              <a href="${pageContext.request.contextPath}/cms/manage/${i.slug}/menus" class="btn btn-sm btn-default">Menus</a>
            </div>

            <a href="${pageContext.request.contextPath}/cms/manage/${i.slug}/edit" class="btn btn-sm btn-default">Edit</a>

            <a href="${pageContext.request.contextPath}/cms/manage/${i.slug}/delete" class="btn btn-danger btn-sm">Delete</a>

          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
        </table>
      </c:otherwise>
</c:choose>
