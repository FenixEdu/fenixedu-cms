<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2 class="page-header" style="margin-top: 0">
  <spring:message code="categories.create.title" />
  <small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a> </small>
</h2>

<form class="form-horizontal" action="" method="post" role="form">
  <div class="${emptyName ? "form-group has-error" : "form-group"}">
    <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="categories.create.label.name"/></label>
    <div class="col-sm-10">
      <input type="text" name="name" class="form-control" id="inputEmail3" placeholder="<spring:message code="categories.create.label.name"/>">
      <c:if test="${emptyName != null}"><p class="text-danger"><spring:message code="categories.create.error.emptyName"/></p></c:if>
    </div>
  </div>
  
  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.create"/></button>
    </div>
  </div>
</form>