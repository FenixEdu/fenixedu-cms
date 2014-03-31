<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<h1>Manage Sites</h1>
<p>
  <a href="${pageContext.request.contextPath}/cms/manage/themes/create" class="btn btn-primary">Add Theme</a>
</p>

<c:choose>
      <c:when test="${themes.size() == 0}">
      There are no sites defined
      </c:when>

      <c:otherwise>
        <table class="table table-striped table-bordered">
      <thead>
        <tr>
          <th class="col-md-6">Name</th>
          <th>Created By</th>
          <th>Creation Date</th>
          <th>#Templates</th>
          <th>Operations</th>
        </tr>
      </thead>
      <tbody>
      <c:forEach var="i" items="${themes}">
        <tr>
          <td>
            <h5>${i.getName()} <c:if test="${true}">
          <span class="label label-success">Default</span>
      </c:if></h5>
            <div><small>Type:<code>${i.type}</code></small></div>
            <div><small>${i.getDescription()}</small></div>
          </td>
          <td>${i.createdBy.username}</td>
          <td><joda:format value="${i.creationDate}" pattern="MMM dd, yyyy"/></td>
          <td>${i.templatesSet.size()}</td>
          <td>
            <a class="btn btn-default" href="themes/${i.type}/see">More</a>
            <a class="btn btn-default btn-danger" href="themes/${i.type}/delete">Delete</a>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
        </table>
      </c:otherwise>
</c:choose>
