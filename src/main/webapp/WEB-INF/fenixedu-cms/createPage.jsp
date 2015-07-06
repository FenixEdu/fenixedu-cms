<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of FenixEdu CMS.

    FenixEdu CMS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu CMS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
${portal.toolkit()}

<div class="page-header">
    <h1><spring:message code="page.create.title" /></h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small></h2>
</div>

<form class="form-horizontal" action="" method="post" role="form">
  ${csrf.field()}
  <div class="${emptyName ? "form-group has-error" : "form-group"}">
    <label class="col-sm-1 control-label"><spring:message code="menu.create.label.name" /></label>
    <div class="col-sm-11">
      <input type="text" name="name" bennu-localized-string required-any class="form-control" placeholder="<spring:message code="menu.create.label.name"/>">
      <c:if test="${emptyName != null}"><p class="text-danger"><spring:message code="menu.create.error.emptyName" /></p></c:if>
    </div>
  </div>
  <div class="form-group">
    <div class="btn-group">
      <button type="submit" class="btn btn-primary"><spring:message code="action.create" /></button>
    </div>
  </div>
</form>