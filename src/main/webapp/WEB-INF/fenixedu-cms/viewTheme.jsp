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
    <h1><c:out value="${theme.name}" />
          <small>

              <ol class="breadcrumb">
                    <li><a href="${pageContext.request.contextPath}/cms/sites">Sites</a></li>
                    <li><a href="${pageContext.request.contextPath}/cms/themes">Themes</a></li>
              </ol>
          </small>
    </h1>
</div>

<p>
    <a href="${pageContext.request.contextPath}/cms/themes/${theme.type}/edit" class="btn btn-primary"><i class="glyphicon glyphicon-edit"></i> Edit</a>

    <a href="${pageContext.request.contextPath}/cms/themes/${theme.type}/export" target="_blank" download="${theme.type}.zip" class="btn btn-default"><i class="glyphicon glyphicon-cloud-download"></i> Export</a>
</p>

<div class="row">
    <div class="col-md-8 col-sm-12">
        <c:choose>
            <c:when test="${theme.previewImage == null}">
               <div class="thumbnail" style="min-height:500px; background:#efefef;">
                    <h5 style="display:table;margin: 0 auto; margin-top:220px;">No thumbnail available</h5>
               </div>
            </c:when>
            <c:otherwise>
                <div class="thumbnail">
                    <a href="${pageContext.request.contextPath}/cms/themes/${theme.type}/edit">
                        <img src="${cms.downloadUrl(theme.previewImage)}">
                    </a>
                </div>
            </c:otherwise>
        </c:choose>

        <c:if test="${not empty sites}">
          <h4>Some sites using this theme</h4>
          <div class="row">
              <c:forEach var="site" items="${sites}">
                <div class="col-sm-6 col-md-6">
                  <div class="thumbnail">
                    <div class="caption">
                      <h5><a href="${site.editUrl}">${site.name.content}</a></h3>
                      <p>${site.description.content}</p>
                    </div>
                  </div>
                </div>
              </c:forEach>
          </div>
          <p>
              <a href="${pageContext.request.contextPath}/cms/sites" class="btn btn-xs btn-default">View all</a>
          </p>
        </c:if>
    </div>
    <div class="col-md-4 col-sm-12">
        <div class="panel panel-primary">
          <div class="panel-heading">Details</div>
          <div class="panel-body">
            <dl class="dl-horizontal">
                <dt>Type</dt>
                <dd><samp><c:out value="${theme.type}" /></samp></dd>
                
                <dt>Description</dt>
                <dd><samp><c:out value="${theme.description}" /></samp></dd>

                <c:if test="${theme.extended != null}">
                  <dt>Extends</dt>
                  <dd><a href="../${theme.extended.type}/see">${theme.extended.name}</a></dd>
                </c:if>
                
                <dt>Size</dt>
                <dd>${cms.prettySize(theme.files.totalSize)}</dd>

                <c:if test="${theme.isDefault()}">
                  <dt>Default</dt>
                  <dd><span class="label label-success">Yes</span></dd>
                </c:if>

            </dl>
          </div>
        </div>
        <c:if test="${not theme.isDefault()}">
        <div class="panel panel-danger">
          <div class="panel-heading">Danger Zone</div>
          <div class="panel-body">
            <p class="help-block">Once you delete a theme, there is no going back. Please be certain.</p>
            <button data-toggle="modal" data-target="#deleteModal" class="btn btn-danger">Delete this Theme</button>
          </div>
        </div>
        </c:if>
    </div>
</div>

<div class="modal fade" id="deleteModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Are you sure?</h4>
      </div>
      <div class="modal-body">
        <p>You are about to delete the theme '<c:out value="${theme.name}" />'. There is no way to rollback this opeartion. Are you sure? </p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
        <button type="button" onclick="$('#deleteThemeForm').submit();" class="btn btn-danger">Yes</button>
        <form action="delete" method="post" id="deleteThemeForm">${csrf.field()}</form> 
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


