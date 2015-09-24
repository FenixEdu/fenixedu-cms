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
<%@taglib uri="http://fenixedu.com/cms/permissions" prefix="permissions" %>

${portal.toolkit()}

<div class="page-header">
    <h1>Posts</h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small></h2>
</div>

<div class="row">
    <div class="col-sm-5">
        <c:choose>
            <c:when test="${permissions:canDoThis(site, 'CREATE_POST')}">
                <button type="button" data-toggle="modal" data-target="#create-post" class="btn btn-primary"><i class="icon icon-plus"></i> New</a>
            </c:when>
            <c:otherwise>
                <button type="button" class="btn btn-primary disabled"><i class="icon icon-plus"></i> New</a>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="col-sm-7">
        <div class="pull-right">
            <div class="form-inline">
                <div class="btn-group">
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="true">
                        <c:choose>
                            <c:when test="${category!=null}">${category.name.content}</c:when>
                            <c:otherwise>Category</c:otherwise>
                        </c:choose>
                        <span class="caret"></span>
                    </button>

                    <ul class="dropdown-menu dropdown-menu-right" role="menu">
                        <li><a href="#" class="category-item" data-category-slug="">All</a></li>
                        <c:forEach var="cat" items="${site.categories}">
                            <li><a href="#" class="category-item" data-category-slug="${cat.slug}">${cat.name.content}</a></li>
                        </c:forEach>
                    </ul>
                </div>
                <div class="form-group">
                    <input id="search-query" type="text" class="form-control" placeholder="Search for..." value="${query}">                    
                </div>
            </div>
        </div>
    </div>
</div>

<p></p>

<c:choose>
    <c:when test="${posts.size() == 0}">
    <div class="panel panel-default">
        <div class="panel-body">
           <spring:message code="page.manage.label.emptyPosts"/>
        </div>
    </div>
    </c:when>

    <c:otherwise>
        <table class="table">
            <thead>
            <tr>
                <th><spring:message code="page.manage.label.name"/></th>
                <th><spring:message code="page.manage.label.creationDate"/></th>
                <th><spring:message code="site.manage.label.categories"/></th>
                <th><spring:message code="page.manage.label.operations"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="post" items="${posts}">
                <tr>
                    <td>
                        <h5><a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/${post.slug}/edit">${post.name.content}</a></h5>
                    </td>
                    <td>${post.creationDate.toString('dd MMMM yyyy, HH:mm', locale)}
                        <small>- ${post.createdBy.name}</small>
                    </td>
                    <td>
                        <c:forEach var="cat" items="${post.categoriesSet}">
                            <a href="${cat.getEditUrl()}" class="badge">${cat.name.content}</a>
                        </c:forEach>
                    </td>
                    <td>
                        <div class="btn-group">
                            <a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/${post.slug}/edit"
                               class="btn btn-icon btn-default">
                               <i class="glyphicon glyphicon-edit"></i>
                            </a>
                            <a href="${post.address}"
                               class="btn btn-icon btn-default">
                               <i class="glyphicon glyphicon-link"></i>
                            </a>

                            <div class="btn-group">
                                <button type="button" class="btn btn-default btn-icon dropdown-toggle" data-toggle="dropdown"
                                        aria-expanded="false">
                                    <i class="icon icon-dot-3"></i>
                                </button>

                                <ul class="dropdown-menu dropdown-menu-right" role="menu">
                                    <li><a href="#"><i class="glyphicon glyphicon-bullhorn"></i> Unpublish</a></li>
                                    <c:choose>
                                        <c:when test="${!post.isVisible() && permissions:canDoThis(site, 'DELETE_POSTS')}">
                                            <li><a href="#" data-post="${post.slug}"><i class="glyphicon    glyphicon-trash"></i> Delete</a></li>
                                        </c:when>
                                        <c:when test="${post.isVisible() && permissions:canDoThis(site, 'DELETE_POSTS_PUBLISHED')}">
                                            <li><a href="#" data-post="${post.slug}"><i class="glyphicon glyphicon-trash"></i> Delete</a></li>
                                        </c:when>
                                    </c:choose>
                                </ul>
                            </div>
                        </div>

					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>

        <!-- Pagination -->
        <c:if test="${partition.getNumPartitions() > 1}">
          <nav class="text-center">
            <ul class="pagination">
              <li ${partition.isFirst() ? 'class="disabled"' : ''}>
                <a href="#" onclick="goToPage(${partition.getNumber() - 1})">&laquo;</a>
              </li>
              <li class="disabled"><a>${partition.getNumber()} / ${partition.getNumPartitions()}</a></li>
              <li ${partition.isLast() ? 'class="disabled"' : ''}>
                <a href="#" onclick="goToPage(${partition.getNumber() + 1})">&raquo;</a>
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
                    ${csrf.field()}
					<button type="submit" class="btn btn-danger"><spring:message code="action.delete"/></button>
					<a class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></a>
				</form>
			</div>
		</div>
	</div>
</div>

<script type="application/javascript">
    function getParameterByName(name) {
        var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
        return match && decodeURIComponent(match[1].replace(/\+/g, ' ')) || "";
    }

    function goToPage(pageNumber) {
        searchPosts({page: pageNumber});
    }

    function searchPosts(options) {
        var searchQueryObj = {
            category: typeof(options.categorySlug) === "string" ? options.categorySlug : getParameterByName('category'),
            page: options.page || getParameterByName('page'),
            query: typeof(options.query) === "string" ? options.query : getParameterByName('query')
        };
        window.location.search = $.param(searchQueryObj);
    }

	(function () {
		$('#search-query').keypress(function (e) {
			if (e.which == 13) {
				searchPosts({ query: $('#search-query').val(), page: 1});
			}
		});

		$("a[data-post]").on('click', function (el) {
			var postSlug = el.target.getAttribute('data-post');
			$('#deleteForm').attr('action', '${pageContext.request.contextPath}/cms/posts/${site.slug}/' + postSlug + '/delete');
			$('#deleteModal').modal('show');
		});

		$('.category-item').on('click', function (e) {
			e.preventDefault();
			searchPosts({ categorySlug: $(e.target).data('category-slug') });
		});

	})();
</script>

<c:if test="${permissions:canDoThis(site, 'CREATE_POST')}">
    <div class="modal fade" id="create-post" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
            <form class="form-horizontal" action="${pageContext.request.contextPath}/cms/posts/${site.slug}/create" method="post" role="form">
                ${csrf.field()}
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"> </button>
                    <h3 class="modal-title">New Post</h3>
                    <small>This could be the start of something great!</small>
                </div>
                <div class="modal-body">
                    <div class="${emptyName ? "form-group has-error" : "form-group"}">
                        <label class="col-sm-2 control-label"><spring:message code="post.create.label.name"/></label>

                        <div class="col-sm-10">
                            <input bennu-localized-string required-any name="name" placeholder="<spring:message code="post.create.label.name" />">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button type="Submit" class="btn btn-primary">Make</button>
                </div>
            </form>
        </div>
      </div>
    </div>
</c:if>