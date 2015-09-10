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
  <h2><small>Manage your content</small></h2>
</div>

<c:if test="${isManager}">
<p>
<div class="row">
  <div class="col-sm-8">
    <a href="${pageContext.request.contextPath}/cms/sites/new" class="btn btn-primary"><i class="glyphicon glyphicon-plus"></i> New</a>
    <a href="${pageContext.request.contextPath}/cms/folders" class="btn btn-default"><i class="glyphicon glyphicon-folder-close"></i> Folders</a>
    <a href="${pageContext.request.contextPath}/cms/themes" class="btn btn-default"><i class="icon icon-brush"></i> Themes</a>
    <button type="button" data-target="#sites-settings" data-toggle="modal" class="btn btn-default"><i class="glyphicon glyphicon-cog"></i> Settings</button>
    <button type="button" class="btn btn-default" onclick="$('#import-button').click();"><i class="glyphicon-cloud-upload"></i> Import</button>
    <form id="import-form" method="post" action="${pageContext.request.contextPath}/cms/sites/import" enctype='multipart/form-data'>
      ${csrf.field()}
      <input id="import-button" class="hidden" type="file" name="attachment" onchange="$('#import-form').submit();" />
    </form>
  </div>
  <div class="col-sm-4">
      <input id="search-query" type="text" class="form-control" placeholder="Search for..." value="${query}" autofocus>
  </div>
</div>
</p>

</c:if>
<c:choose>
    <c:when test="${sites.size() == 0}">
        <div class="panel panel-default">
          <div class="panel-body">
            <spring:message code="site.manage.label.emptySites"/>
          </div>
        </div>
    </c:when>

  <c:otherwise>
    <ul class="list-group">
      <c:forEach var="i" items="${sites}">
        <li class="list-group-item">
          <h3><a href="${pageContext.request.contextPath}/cms/sites/${i.slug}">${i.getName().getContent()}</a></h3>
          <div>${i.getDescription().getContent()}</div>
          <c:choose>
              <c:when test="${ i.published }">
                  <span class="label label-primary">Public</span>
              </c:when>
              <c:otherwise>
                  <span class="label label-default">Draft</span>
              </c:otherwise>
          </c:choose>

          <c:if test="${i.getEmbedded()}">
            <p><span class="label label-info">Embedded</span></p>
          </c:if>

          <c:if test="${i.isDefault()}">
            <span class="label label-success"><spring:message code="site.manage.label.default"/></span>
          </c:if>

          <div class="btn-group pull-right">
            <a href="${i.fullUrl}" class="btn btn-icon btn-default ${i.published ? '' : 'disabled'}"><i class="glyphicon glyphicon-link"></i></a>
            <a href="${pageContext.request.contextPath}/cms/permissions/site/${i.slug}" class="btn btn-icon btn-default"><i class="glyphicon glyphicon-eye-close"></i></a>
            <a href="${pageContext.request.contextPath}/cms/sites/${i.slug}/edit" class="btn btn-icon btn-primary"><i class="glyphicon glyphicon-cog"></i></a>
          </div>
        </li>
      </c:forEach>
    </ul>

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

<div class="modal fade" id="sites-settings">
  <div class="modal-dialog modal-lg">
    <form method="post" action="${pageContext.request.contextPath}/cms/sites/cmsSettings">
      ${csrf.field()}
      <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal"><span class="sr-only">Close</span></button>
            <h3 class="modal-title">CMS Settings</h3>
            <small>Global Settings for your sites</small>
        </div>
        <div class="modal-body">
          <div class="form-group">
              <label class="col-sm-3 control-label">Default Site</label>

              <div class="col-sm-9">
                  <select class="form-control" name="slug">
                      <option value="**null**">-</option>
                      <c:forEach var="i" items="${sites}">
                          <c:if test="${i.isDefault()}">
                            <option ${i.isDefault() ? 'selected' : ''}  value="${i.slug}">${i.name.content}</option>
                          </c:if>
                      </c:forEach>
                  </select>
                  <p class="help-block">The Default Site is the site that is used when you visit the root of the server.</p>
              </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="submit" class="btn btn-primary">Save changes</button>
        </div>
      </form>

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
