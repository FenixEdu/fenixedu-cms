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
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2 class="page-header" style="margin-top: 0">
    <spring:message code="post.create.title"/>
    <small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small>
</h2>

<form class="form-horizontal" action="" method="post" role="form">
    ${csrf.field()}
    <div class="${emptyName ? "form-group has-error" : "form-group"}">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="post.create.label.name"/></label>

        <div class="col-sm-10">
            <input bennu-localized-string required-any name="name" id="inputEmail3"
                   placeholder="<spring:message code="post.create.label.name" />">
            <c:if test="${emptyName != null}"><p class="text-danger"><spring:message code="post.create.error.emptyName"/></p>
            </c:if>
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.create"/></button>
        </div>
    </div>
</form>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
${portal.toolkit()}

