<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
${portal.toolkit()}

<div class="page-header">
    <h1>Permissions</h1>
    <h2><a href="${pageContext.request.contextPath}/cms"><small>Manage global permissions</small></a></h2>
</div>

<p>
    <button class="btn btn-default btn-primary" data-target="#create-modal" data-toggle="modal">
        <span class="glyphicon glyphicon-plus"></span>&nbsp;Create
    </button>
</p>