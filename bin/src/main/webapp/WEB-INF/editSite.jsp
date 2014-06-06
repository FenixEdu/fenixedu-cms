<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<h1><spring:message code="site.edit.title" /></h1>
<form class="form-horizontal" action="" method="post" role="form">
  <div class="${emptyName ? "form-group has-error" : "form-group"}">
    <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.edit.label.name" /></label>
    <div class="col-sm-10">
      <input type="text" name="name" class="form-control" id="inputEmail3" placeholder="<spring:message code="site.edit.label.name" />" value="${site.name.content}" \>
      <c:if test="${emptyName}"><p class="text-danger"><spring:message code="site.edit.error.emptyName" /></p></c:if>
    </div>
  </div>

  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.edit.label.description" /></label>
    <div class="col-sm-10">
      <textarea name="description" class="form-control" rows="3">${site.description.content}</textarea>
    </div>
  </div>

  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.edit.label.theme" /></label>
    <div class="col-sm-10">
      <select name="theme" id="theme">
        <option value="null">-</option>
        <c:forEach var="i" items="${themes}">
          <option value="${i.type}" ${i == site.theme ? 'selected' : ''}>${i.name}</option>
        </c:forEach>
      </select>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.save" /></button>
    </div>
  </div>
</form>