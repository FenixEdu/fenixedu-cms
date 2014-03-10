<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<h1>View Theme</h1>
<h2>${theme.name}</h2>
<p>${theme.description}</p>
<div>Type:<code>${theme.type}</code></div>

<h2>Templates</h2>
        <table class="table table-striped table-bordered">
      <thead>
        <tr>
          <th class="col-md-6">Name</th>
          <th>Type</th>
          <th>Path</th>
        </tr>
      </thead>
      <tbody>
<c:forEach var="i" items="${theme.templatesSet}">
  <tr>
    <td>
      <h5>${i.getName()}</h5>
      <div><small>${i.getDescription()}</small></div>
    </td>
    <td><code>${i.type}</code></td>
    <td><code>${i.file.filename}</code></td>
  </tr>
</c:forEach>
          </tbody>
        </table>

<h2>Files</h2>
        <table class="table table-striped table-bordered">
      <thead>
        <tr>
          <th>Path</th>
          <th>Operations</th>
        </tr>
      </thead>
      <tbody>
<c:forEach var="i" items="${theme.filesSet}">
  <tr>
    <td><code>${i.displayName}</code></td>
    <td><a href="editFile/${i.displayName}" class="btn btn-default btn-sm">Edit</a></td>
  </tr>
</c:forEach>
          </tbody>
        </table>