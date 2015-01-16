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
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h2 class="page-header" style="margin-top: 0">
  <spring:message code="post.manage.title" />
  <small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a> </small>
</h2>
<p>
<a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/create" class="btn btn-default btn-primary"><spring:message code="page.manage.label.createPost" /></a>
</p>

<c:choose>
      <c:when test="${posts.size() == 0}">
      <p><spring:message code="page.manage.label.emptyPosts" /></p>
      </c:when>

      <c:otherwise>
        <table class="table table-striped">
          <thead>
            <tr>
              <th><spring:message code="page.manage.label.name" /></th>
              <th><spring:message code="page.manage.label.creationDate" /></th>
              <th><spring:message code="site.manage.label.categories"/></th>
              <th><spring:message code="page.manage.label.operations" /></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="post" items="${posts}">
            <tr>
              <td>
                <h5><a href="${post.address}" target="_blank">${post.name.content}</a></h5>
                <div><small><spring:message code="page.manage.label.url" />:<code>${post.slug}</code></small></div>
              </td>
              <td>${post.creationDate.toString('dd MMMM yyyy, HH:mm', locale)} <small>- ${post.createdBy.name}</small></td>
              <td>
                <c:forEach var="cat" items="${post.categoriesSet}">
                  <a href="${pageContext.request.contextPath}/cms/categories/${site.slug}" class="badge">${cat.name.content}</a>
                </c:forEach></td>
              <td>
                <div class="btn-group">
                  <a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/${post.slug}/edit" class="btn btn-sm btn-default"><spring:message code="action.edit" /></a>
                  <a href="#" class="btn btn-danger btn-sm" data-post="${post.slug}"><spring:message
                                    code="action.delete"/></a>
                </div>

              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
        <c:if test="${pages > 1}">
            <nav class="text-center">
                <ul class="pagination">
                    <li ${currentPage == 1 ? 'class="disabled"' : ''}>
                        <a href="?page=${currentPage - 1}">&laquo;</a>
                    </li>
                    <li class="disabled"><a>${currentPage} / ${pages}</a></li>
                    <li ${currentPage == pages ? 'class="disabled"' : ''}>
                        <a href="?page=${currentPage + 1}">&raquo;</a>
                    </li>
                </ul>
            </nav>
        </c:if>
      </c:otherwise>
</c:choose>


<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4><spring:message code="post.manage.label.delete.post"/></h4>
            </div>
            <div class="modal-body">
                <p><spring:message code="post.manage.label.delete.post.message"/></p>
            </div>
            <div class="modal-footer">
                <form id="deleteForm" method="POST">
                    <button type="submit" class="btn btn-danger"><spring:message code="action.delete"/></button>
                    <a class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></a>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
(function () {
    $("a[data-post]").on('click', function(el) {
        var postSlug = el.target.getAttribute('data-post');
        $('#deleteForm').attr('action', '${pageContext.request.contextPath}/cms/posts/${site.slug}/' + postSlug + '/delete');
        $('#deleteModal').modal('show');
    });
})();
</script>