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

<div class="page-header">
    <h1>Sites</h1>
    <h2><small>${site.name.content}</small></h2>
</div>

<div class="row">
    <div class="col-sm-8"><a href="" class="btn btn-primary"><i class="icon icon-plus"></i> New</a></div>
    <div class="col-sm-4">
        <div class="row">
        <div class="col-sm-4">
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                    aria-expanded="false">
                <c:if test="${category!=null}">
                    ${category.name.content}
                </c:if>
                <c:if test="${category==null}">
                    Category
                </c:if>
                <span class="caret"></span>
            </button>

            <ul class="dropdown-menu dropdown-menu-right" role="menu">
                <li><a href="#" onclick="searchPosts(null)">All</a></li>
                <c:forEach var="cat" items="${site.categories}">
                    <li><a href="#" onclick='searchPosts("${cat.slug}")'>${cat.name.content}</a></li>
                </c:forEach>
            </ul>            
        </div>  
        </div>
            <div class="col-sm-8">
                <input id="search-query" type="text" class="form-control" placeholder="Search for..." value="${query}">
            </div>
        </div>
    </div>
    <!-- /.col-lg-6 -->
</div>
<!-- /.row -->

<p></p>
<script type="application/javascript">

    function searchPosts(categorySlug) {
        var query = $('#search-query').val();
        var searchQuery = "";
        searchQuery += categorySlug ? "category=" + categorySlug : "";
        searchQuery += query ? "query=" + query : "";
        window.location.search = searchQuery;
    }

    $("#search-query").keyup(function(e){
        if(e.keyCode == 13){
            searchPosts();
        }
    });

</script>

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
                                    <li><a href="#"><i class="glyphicon glyphicon-trash"></i> Delete</a></li>
                                </ul>            
                            </div>  
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
        $("a[data-post]").on('click', function (el) {
            var postSlug = el.target.getAttribute('data-post');
            $('#deleteForm').attr('action', '${pageContext.request.contextPath}/cms/posts/${site.slug}/' + postSlug + '/delete');
            $('#deleteModal').modal('show');
        });
    })();
</script>