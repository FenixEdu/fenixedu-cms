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
    <h1>Media Library</h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small></h2>
</div>
<p>
    <div class="row">
        <div class="col-md-4 pull-right">
                <input id="search-query" type="text" class="form-control" placeholder="Search for..." value="${query}">                    
        </div>
    </div>
</p>

<c:choose>

    <c:when test="${postFiles.size() == 0}">
        <div class="panel panel-default">
            <div class="panel-body">
                <i>There are no files.</i>
            </div>
        </div>
    </c:when>

      <c:otherwise>
            <div class="row">
                <c:forEach var="postFile" items="${postFiles}">
                    <c:set var="editPostFileUrl" value="${postFile.editUrl}" />

                    <div class="col-sm-6 col-md-4">

                        <a href="${editPostFileUrl}">
                            <div class="thumbnail">
                                <c:choose>
                                    <c:when test="${postFile.files.contentType.startsWith('image/')}">
                                      <img src="${cms.downloadUrl(postFile.files)}" style="height: 200px; width: 100%; display: block;" data-holder-rendered="true" />
                                    </c:when>
                                
                                    <c:otherwise>
                                        <div style="height: 200px; width: 100%; display: block;" >
                                            <div class="empty-preview">
                                                <h5>No thumbnail available</h5>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <div class="caption">
                                    <h3><a href="${editPostFileUrl}"><c:out value="${postFile.files.displayName}" /></a></h3>
                                    <p><samp>${postFile.files.contentType}</samp></p>
                                </div>
                            </div>
                        </div>
                    </a>
                </c:forEach>
            </div>

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

<style type="text/css">
    .empty-preview {
        font-size: 60px; 
        color: #999; 
        display: table;
        margin: 0 auto;
        padding-top: 110px;
    }
</style>

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
  })();
</script>
