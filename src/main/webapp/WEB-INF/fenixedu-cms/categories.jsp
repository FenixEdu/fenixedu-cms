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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="page-header">
    <h1><spring:message code="categories.manage.title" /></h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small></h2>
</div>

<a href="${pageContext.request.contextPath}/cms/categories/${site.slug}/create" class="btn btn-default btn-primary">
	<span class="glyphicon glyphicon-plus"></span>&nbsp;New</a>
</p>

<c:choose>
      <c:when test="${categories.size() == 0}">
        <div class="panel panel-default">
          <div class="panel-body">
            <i><spring:message code="categories.manage.emptyCategories"/></i>
          </div>
        </div>
      </c:when>

      <c:otherwise>
        <table class="table table-striped">
          <thead>
            <tr>
              <th><spring:message code="categories.manage.label.name"/></th>
              <th><spring:message code="categories.manage.label.creationDate"/></th>
              <th><spring:message code="site.dashboard.label.posts"/></th>
              <th><spring:message code="categories.manage.label.operations"/></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="category" items="${categories}">
            <tr>
              <td>
                <h5>
                  <c:if test="${category.address != null}">
                    <a href="${category.address}" target="_blank">${category.name.content}</a>
                  </c:if>
                  <c:if test="${category.address == null}">
                    ${category.name.content}
                  </c:if>
                </h5>
                <div><small><spring:message code="categories.manage.label.url"/>:<code>${category.getSlug()}</code></small></div>
              </td>
              <td>${category.creationDate.toString('dd MMMM yyyy, HH:mm', locale)} <small>- ${category.createdBy.name}</small></td>
              <td>${category.postsSet.size()}</td>
              <td>
                  <a href="${category.editUrl}" class="btn btn-sm btn-default">Edit</a>
	               <button class="btn btn-danger btn-sm" onclick="document.getElementById('deleteCategoryForm${category.externalId}').submit();" ${category.postsSet.size() > 0 ? 'disabled' : ''}><spring:message code="action.delete"/></button>
					       <form id="deleteCategoryForm${category.externalId}" action="${pageContext.request.contextPath}/cms/categories/${category.site.slug}/${category.slug}/delete" method="POST">${csrf.field()}</form>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>
