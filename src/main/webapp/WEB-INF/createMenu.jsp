<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1><spring:message code="menu.create.title" /></h1>
<p class="small">Site: <strong>${site.name.content}</strong>  </p>
<form class="form-horizontal" action="" method="post" role="form">
  <div class="${emptyName ? "form-group has-error" : "form-group"}">
    <label for="inputEmail3" class="col-sm-2 control-label">Name</label>
    <div class="col-sm-10">
      <input type="text" name="name" class="form-control" id="inputEmail3" placeholder="<spring:message code="menu.create.label.name" />">
      <c:if test="${emptyName}"><p class="text-danger"><spring:message code="site.create.error.emptyName"/></p></c:if>
    </div>
  </div>
  
  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.create" /></button>
    </div>
  </div>
</form>