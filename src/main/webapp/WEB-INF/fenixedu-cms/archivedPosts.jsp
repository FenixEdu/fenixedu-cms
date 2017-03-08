<%--

    Copyright © 2017 Instituto Superior Técnico

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
    <h1><spring:message code="site.manage.label.archivedPosts"></spring:message>
        <small>
            <ol class="breadcrumb">
                <li><a href="${pageContext.request.contextPath}/cms/sites">Content Management</a></li>
                <li><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></li>
            </ol>
        </small>
    </h1>
</div>


<div class="row">
    <div class="col-sm-5"></div>
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
                    <input id="search-query" type="text" class="form-control" placeholder="Search for..." value="${query}" autofocus>
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
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="post" items="${posts}">
                <tr>
                    <td class="col-md-6"><h5>${post.name.content}</h5></td>
                    <td class="col-md-2">${cms.prettyDate(post.creationDate)}</td>
                    <td class="col-md-3">
                        <c:forEach var="cat" items="${post.categoriesSet}" end="3">
                            <a href="${cat.getEditUrl()}" class="badge">${cat.name.content}</a>
                        </c:forEach>
                    </td>
                    <td class="col-md-1">
                            <button class="btn btn-primary" data-post="${post.slug}"><spring:message code="site.manage.label.recover"/></button>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <!-- Pagination -->
        <c:if test="${partition.getNumPartitions() > 1}">
            <nav class="pull-right">
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

<div class="modal fade" id="recover-post" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4><spring:message code="post.manage.label.recover.post"/></h4>
            </div>
            <div class="modal-body">
                <p><spring:message code="post.manage.label.recover.post.message"/></p>
            </div>
            <div class="modal-footer">
                <form id="recover-form" method="POST">
                    <button type="submit" class="btn btn-primary"><spring:message code="site.manage.label.recover"/></button>
                    <a class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></a>
                </form>
            </div>
        </div>
    </div>
</div>

<script type="application/javascript">
    $(document).ready(function(){
        $("[data-post]").on('click', function (el) {
            var postSlug = el.target.getAttribute('data-post');
            $('#recover-form').attr('action', '${pageContext.request.contextPath}/cms/posts/${site.slug}/'+postSlug+'/recover');
            $('#recover-post').modal('show');
        });
    })

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
            query: typeof(options.query) === "string" ? options.query : getParameterByName('query'),
            archived: true
        };
        window.location.search = $.param(searchQueryObj);
    }

    $(document).ready(function(){
        $('#search-query').keypress(function (e) {
            if (e.which == 13) {
                searchPosts({ query: $('#search-query').val(), page: 1});
            }
        });

        $('.category-item').on('click', function (e) {
            e.preventDefault();
            searchPosts({ categorySlug: $(e.target).data('category-slug') });
        });
    });
</script>