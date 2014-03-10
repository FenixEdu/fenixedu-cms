<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<h1>Manage Menu</h1>
<p class="small">Site: <a href="../"><strong>${site.name.content}</strong></a> </p>
<p>
<a href="menus/create" class="btn btn-default btn-primary">Create Menu</a>
</p>

<c:choose>
      <c:when test="${menus.size() == 0}">
      <p>There are no menus created for this site.</p>
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
          <c:forEach var="m" items="${menus}">
            <tr>
              <td>
                <h5>${m.name.content}</h5>
              </td>
              <td>${m.createdBy.username}</td>
              <td><joda:format value="${m.getCreationDate()}" pattern="MMM dd, yyyy"/></td>
              <td>
                <a href="menus/${m.oid}/delete" class="btn btn-danger btn-sm">Delete</a>
                <a href="menus/${m.oid}/change" class="btn btn-sm btn-default">Change</a>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
