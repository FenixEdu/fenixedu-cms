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
    <h1><spring:message code="site.manage.label.archivedPages"></spring:message></h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small></h2>
</div>

<p>
<div class="row">
    <div class="col-sm-8">
    </div>
    <div class="col-sm-4 pull-right">
        <input id="search-query" type="text" class="form-control" placeholder="Search for..." value="${query}" autofocus>
    </div>
</div>
</p>

<c:choose>
    <c:when test="${pages.size() == 0}">
        <div class="panel panel-default">
            <div class="panel-body">
                <i><spring:message code="page.manage.label.emptyPages"/></i>
            </div>
        </div>
    </c:when>

    <c:otherwise>
        <table class="table">
            <thead>
            <tr>
                <th><spring:message code="post.manage.label.name"/></th>
                <th><spring:message code="post.manage.label.creationDate"/></th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="page" items="${pages}">
                <tr>
                    <td class="col-md-6"><h5>${page.name.content}</h5></td>
                    <td class="col-md-5">${cms.prettyDate(page.creationDate)}</td>
                    <td class="col-md-1">
                        <button data-page="${page.slug}" class="btn btn-primary">
                            <spring:message code="site.manage.label.recover"/>
                        </button>
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

<div class="modal fade" id="recover-page" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4><spring:message code="page.manage.label.recover.page"/></h4>
            </div>
            <div class="modal-body">
                <p><spring:message code="page.manage.label.recover.page.message"/></p>
            </div>
            <div class="modal-footer">
                <form id="recover-form" method="POST">
                    ${csrf.field()}
                    <button type="submit" class="btn btn-primary"><spring:message code="site.manage.label.recover"/></button>
                    <a class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></a>
                </form>
            </div>
        </div>
    </div>
</div>

<script type="application/javascript">

    $(document).ready(function(){
        $("[data-page]").on('click', function (el) {
            var pageSlug = el.target.getAttribute('data-page');
            $('#recover-form').attr('action', '${pageContext.request.contextPath}/cms/pages/${site.slug}/'+pageSlug+'/recover');
            $('#recover-page').modal('show');
        });
    })

    function getParameterByName(name) {
        var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
        return match && decodeURIComponent(match[1].replace(/\+/g, ' ')) || "";
    }

    function goToPage(pageNumber) {
        searchPosts({currentPage: pageNumber});
    }

    function searchPosts(options) {
        var searchQueryObj = {
            currentPage: options.currentPage || getParameterByName('currentPage'),
            query: typeof(options.query) === "string" ? options.query : getParameterByName('query'),
            archived: true
        };
        window.location.search = $.param(searchQueryObj);
    }

    (function () {
        $('#search-query').keypress(function (e) {
            if (e.which == 13) {
                searchPosts({ query: $('#search-query').val(), currentPage: 1});
            }
        });
    })();

</script>