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
    <h1>Content Management
          <c:if test="${cmsSettings.canManageSettings()}">
          <button type="button" class="btn btn-link" data-target="#sites-settings" data-toggle="modal"><i class="icon icon-tools"></i></button>
          </c:if>
          <small>
              <ol class="breadcrumb">
                    <li><a href="${pageContext.request.contextPath}/cms/sites">Sites</a></li>
                </ol>
          </small>
    </h1>
</div>


<c:if test="${empty query and empty tag}">
<style>
  .site{
    padding-left: 24px !important;
  }
</style>
</c:if>

<style>
  .folder{
    padding-top: 20px !important;
  }
  .folder i{
    float:left;
    color: #888;
    padding-right: 5px;
  }

  .folder h5{
    text-transform: uppercase;
    font-weight: bold;
    margin-bottom: 0px;
    margin-top: 0px;
  }
</style>
<p>

<div class="row">
  <div class="col-sm-8">
    <c:if test="${cmsSettings.canManageSettings()}">
      <div class="btn-group">
      <a href="${pageContext.request.contextPath}/cms/sites/new" class="btn btn-primary"><i class="glyphicon glyphicon-plus"></i> New</a>
      <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
        <span class="caret"></span>
        <span class="sr-only">Toggle Dropdown</span>
      </button>
      <ul class="dropdown-menu">
        <li><a href="#" onclick="$('#import-button').click();">Import</a></li>
        <form id="import-form" method="post" action="${pageContext.request.contextPath}/cms/sites/import" enctype='multipart/form-data'>
            ${csrf.field()}
            <input id="import-button" class="hidden" type="file" name="attachment" onchange="$('#import-form').submit();" />
        </form>
      </ul>
      </div>
    </c:if>

    <c:if test="${cmsSettings.canManageThemes()}">
      <a href="${pageContext.request.contextPath}/cms/themes" class="btn btn-default"><i class="icon icon-brush"></i> Themes</a>
    </c:if>

  </div>
  <div class="col-sm-4">
  <div class="input-group">
      <div class="input-group-btn">
        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          <c:if test="${folder != null and folder != 'no-folder'}">${folder.functionality.title.content}</c:if>
          <c:if test="${folder != null and folder == 'no-folder'}">Untagged</c:if>
          <c:if test="${folder == null}">Tag</c:if>
         <span class="caret"></span></button>
        <ul class="dropdown-menu">
          <c:if test="${not empty tag}">
            <li><a href="${pageContext.request.contextPath}/cms">All</a></li>
          </c:if>
          <c:forEach var="f" items="${foldersSorted}">
            <c:if test="${f == null}">
              <li><a href="?tag=&#10008;">Untagged</a></li>
            </c:if> 
            <c:if test="${f != null}">
              <li><a href="?tag=${f.functionality.path}">${f.functionality.title.content}</a></li>
            </c:if>
          </c:forEach>
        </ul>
      </div><!-- /btn-group -->
      <input id="search-query" type="text" class="form-control" placeholder="Search for..." value="${query}" autofocus>
    </div><!-- /input-group -->
  </div>
  </div>
</p>


<c:if test="${empty query and empty tag}">
<c:choose>
    <c:when test="${foldersSorted.size() == 0}">
        <div class="panel panel-default">
          <div class="panel-body">
            <spring:message code="site.manage.label.emptySites"/>
          </div>
        </div>
    </c:when>

  <c:otherwise>

    <table class="table">
  <thead>
    <tr>
      <th>Name</th>
      <th>Created</th>
      <th>Published</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach var="f" items="${foldersSorted}">
    <c:set var="sites" value="${sitesByFolders.get(f)}" />
    <c:set var="totalCount" value="${foldersCount.get(f)}" />
      <tr>
        <td colspan="3" class="folder">
        <i class="icon icon-down-dir"></i>

       <a href="?tag=${f == null ? '&#10008;' : f.functionality.path}"><h5>
          <c:if test="${f == null}">Untagged</c:if> 
          <c:if test="${f != null}">${f.functionality.title.content}</c:if> 
        <span class="badge">${totalCount}</span></h5></a>
        </td>
      </tr>
      
       <c:forEach var="i" items="${sites}" >
      <tr>
        <td class="col-md-8 site">

        <a href="${pageContext.request.contextPath}/cms/sites/${i.slug}">${i.getName().getContent()}</a> 

        <c:if test="${i.getEmbedded()}">
          <span class="label label-info">Embedded</span></c:if><c:if test="${i.isDefault()}"><span class="label label-success"><spring:message code="site.manage.label.default"/></span>
        </c:if>

        </td>
        <td class="col-md-2">${cms.prettyDate(i.creationDate)}</td>
        <td class="col-md-2">
            <div class="switch switch-success">
              <input type="checkbox" ng-model="post.active" id="success" class="ng-pristine ng-valid">
              <label for="success">Privileged</label>
            </div>
            <div class="dropdown pull-right">
              <a class="dropdown-toggle" href="#" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                <span class="glyphicon glyphicon-option-vertical"></span>
              </a>
              <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenu1">
                <li><a href="${pageContext.request.contextPath}/cms/sites/${i.slug}/edit">View details</a></li>
                <c:if test="${i.published}">
                  <li><a href="${i.fullUrl}">Visit public URL</a></li>
                </c:if>
                <c:if test="${permissions:canDoThis(i, 'MANAGE_ROLES')}">
                  <li><a href="${pageContext.request.contextPath}/cms/sites/${i.slug}/roles">View roles</a></li>
                </c:if>
              </ul>
            </div>
        </td>
      </tr>
      </c:forEach>
    </c:forEach>
  </tbody>
</table>
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
</c:if>

<c:if test="${not empty query or not empty tag}">
<c:choose>
    <c:when test="${foldersSorted.size() == 0}">
        <div class="panel panel-default">
          <div class="panel-body">
            <spring:message code="site.manage.label.emptySites"/>
          </div>
        </div>
    </c:when>

  <c:otherwise>

    <table class="table">
  <thead>
    <tr>
      <th>Name</th>
      <th>Created</th>
      <th>Published</th>
    </tr>
  </thead>
  <tbody>  
       <c:forEach var="i" items="${sites}" >
      <tr>
        <td class="col-md-8 site">

        <a href="${pageContext.request.contextPath}/cms/sites/${i.slug}">${i.getName().getContent()}</a> 

        <c:if test="${i.getEmbedded()}">
          <span class="label label-info">Embedded</span></c:if><c:if test="${i.isDefault()}"><span class="label label-success"><spring:message code="site.manage.label.default"/></span>
        </c:if>

        </td>
        <td class="col-md-2">${cms.prettyDate(i.creationDate)}</td>
        <td class="col-md-2">
            <div class="switch switch-success">
              <input type="checkbox" ng-model="post.active" id="success" class="ng-pristine ng-valid">
              <label for="success">Privileged</label>
            </div>
            <div class="dropdown pull-right">
              <a class="dropdown-toggle" href="#" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                <span class="glyphicon glyphicon-option-vertical"></span>
              </a>
              <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenu1">
                <li><a href="${pageContext.request.contextPath}/cms/sites/${i.slug}/edit">View details</a></li>
                <c:if test="${i.published}">
                  <li><a href="${i.fullUrl}">Visit public URL</a></li>
                </c:if>
                <c:if test="${permissions:canDoThis(i, 'MANAGE_ROLES')}">
                  <li><a href="${pageContext.request.contextPath}/cms/sites/${i.slug}/roles">View roles</a></li>
                </c:if>
              </ul>
            </div>
        </td>
      </tr>
      </c:forEach>
  </tbody>
</table>
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
</c:if>

<c:if test="${cmsSettings.canManageSettings()}">
  <div class="modal fade" id="sites-settings">
    <div class="modal-dialog modal-lg">
      <form method="post" class="form-horizontal" action="${pageContext.request.contextPath}/cms/sites/cmsSettings">
        ${csrf.field()}
        <div class="modal-content">
          <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal"><span class="sr-only">Close</span></button>
              <h3 class="modal-title">Settings</h3>
              <small>Costumize your content managment system</small>
          </div>
          <div class="modal-body">
            <div role="tabpanel">
                <!-- Nav tabs -->
                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="active">
                        <a href="#general" aria-controls="general" role="tab" data-toggle="tab">General</a>
                    </li>
                    <c:if test="${cmsSettings.canManageFolders()}">
                    <li role="presentation">
                        <a href="#tags" aria-controls="tags" role="tab" data-toggle="tab">Tags</a>
                    </li>
                    </c:if>
                    <c:if test="${cmsSettings.canManageRoles()}">
                    <li role="presentation">
                        <a href="#roles" aria-controls="roles" role="tab" data-toggle="tab">Roles</a>
                    </li>
                    </c:if>

                    <c:if test="${isManager}">
                    <li role="presentation">
                        <a href="#acl" aria-controls="acl" role="tab" data-toggle="tab">Access Control</a>
                    </li>
                    </c:if>
                </ul>
                <!-- Tab panes -->
                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="general">
                      <div class="form-group">
                          <label class="col-sm-3 control-label">Default Site</label>

                          <div class="col-sm-9">
                              <select class="form-control" name="slug">
                                  <option value="**null**">-</option>
                                  <c:forEach var="i" items="${sites}">
                                     <option ${i.isDefault() ? 'selected' : ''}  value="${i.slug}">${i.name.content}</option>
                                  </c:forEach>
                              </select>
                              <p class="help-block">The Default Site is the site that is used when you visit the root of the server.</p>
                          </div>
                      </div>
                    </div>
                    <c:if test="${cmsSettings.canManageFolders()}">
                    <div role="tabpanel" class="tab-pane" id="tags">
                    <div class="row">
                    <div class="col-md-12">
                        <a class="btn btn-primary" href="#" data-toggle="modal" data-target="#newFolderModal">
                            <span class="glyphicon glyphicon-plus"></span>&nbsp;New</a>
                        </a>
                    </div>
                    </div>
                        <c:choose>
                          <c:when test="${folders.size() == 0}">
                              <div class="panel panel-default">
                                <div class="panel-body">
                                  There are no tags.
                                </div>
                              </div>
                          </c:when>

                        <c:otherwise>
                        <table class="table">
                           <thead>
                            <tr>
                              <th>Name</th>
                              <th>Path</th>
                              <th>Websites</th>
                            </tr>
                          </thead>
                          <tbody>
                          <c:forEach var="folder" items="${folders}">
                            <tr>
                              <td class="col-md-8">${folder.functionality.title.content}</td>
                              <td class="col-md-3">/${folder.functionality.path}</td>
                              <td class="col-md-1">
                                ${folder.siteSet.size()}
                                <div class="dropdown pull-right">
                                  <a class="dropdown-toggle" href="#" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                                    <span class="glyphicon glyphicon-option-vertical"></span>
                                  </a>
                                  <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenu1">
                                    <li><a href="${pageContext.request.contextPath}/cms/folders/resolver/${folder.externalId}">Edit custom resolver</a></li>
                                    <c:if test="${folder.siteSet.size() == 0}">
                                      <li><a class="delete-tag-link" data-id="${folder.externalId}" href="#"><spring:message code="action.delete"/></a></li>
                                    </c:if>
                                  </ul>
                                </div>
                              </td>
                            </tr>
                          </c:forEach>
                          </tbody>
                        </table>
                        </c:otherwise>
                        </c:choose>
                    </div>
                    </c:if>

                    <c:if test="${cmsSettings.canManageRoles()}">
                    <div role="tabpanel" class="tab-pane" id="roles">
                    <div class="row">
                    <div class="col-md-12">
                        <a class="btn btn-primary" href="#" data-toggle="modal" data-target="#create-role-modal">
                            <span class="glyphicon glyphicon-plus"></span>&nbsp;New</a>
                        </a>
                    </div>
                    </div>
                        <c:choose>
                          <c:when test="${folders.size() == 0}">
                              <div class="panel panel-default">
                                <div class="panel-body">
                                  There are no roles.
                                </div>
                              </div>
                          </c:when>

                        <c:otherwise>
                        <table class="table">
                           <thead>
                            <tr>
                              <th>Role Name</th>
                              <th>Websites</th>
                            </tr>
                          </thead>
                          <tbody>

                          <c:forEach var="role" items="${roles}">
                            <tr>
                              <td class="col-md-10">
                                ${role.description.content}
                                <a href="${pageContext.request.contextPath}/cms/permissions/${role.externalId}/edit" class="btn btn-small btn-default pull-right">Edit</a>
                              </td>
                              <td class="col-md-2">
                                ${role.roles.size()}
                                <div class="dropdown pull-right">
                                  <a class="dropdown-toggle" href="#" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                                    <span class="glyphicon glyphicon-option-vertical"></span>
                                  </a>
                                  <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenu1">
                                    <li><a href="${pageContext.request.contextPath}/cms/permissions/${role.externalId}/edit">Edit permissions</a></li>
                                    <li><a class="connect-site-link" data-id="${role.externalId}" >Add to website</a></li>
                                    <li><a class="delete-role-link" data-id="${role.externalId}" >Delete</a></li>
                                  </ul>
                                </div>
                              </td>
                            </tr>
                          </c:forEach>
                          </tbody>
                        </table>
                        </c:otherwise>
                        </c:choose>
                        </div>
                        </c:if>
       
                        <c:if test="${isManager}">
                        <div role="tabpanel" class="tab-pane" id="acl">
                        <div class="form-group">
                            <label class="control-label col-sm-3">Themes managers:</label>
                            <div class="col-sm-9">
                                <input bennu-group allow="public,users,managers,custom" name="themesManagers" type="text" value='${cmsSettings.themesManagers.toGroup().expression}'/>
                                <p class="help-block">Users that are allowed to manage <a href="${pageContext.request.contextPath}/cms/themes">themes</a>.</p>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-sm-3 control-label">Roles managers:</label>
                            <div class="col-sm-9">
                                <input bennu-group allow="public,users,managers,custom" name="rolesManagers" type="text" value='${cmsSettings.rolesManagers.toGroup().expression}'/>
                                <p class="help-block">Users that are allowed to manage <a href="${pageContext.request.contextPath}/cms/permissions">roles</a>.</p>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-sm-3 control-label">Folders managers:</label>
                            <div class="col-sm-9">
                                <input bennu-group allow="public,users,managers,custom" name="foldersManagers" type="text" value='${cmsSettings.foldersManagers.toGroup().expression}'/>
                                <p class="help-block">Users that are allowed to manage <a href="${pageContext.request.contextPath}/cms/folders">folders</a>.</p>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-sm-3 control-label">Global Settings:</label>
                            <div class="col-sm-9">
                                <input bennu-group allow="public,users,managers,custom" name="settingsManagers" type="text" value='${cmsSettings.settingsManagers.toGroup().expression}'/>
                                <p class="help-block">Users that are allowed to global settings such setting the <a href="${pageContext.request.contextPath}/cms/sites">default site</a> or <a href="${pageContext.request.contextPath}/cms/sites/new">create new sites</a></p>
                            </div>
                        </div>
                        </div>
                        </c:if>

                </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <button type="submit" class="btn btn-primary">Done</button>
          </div>
        </form>

      </div>

    </div>
  </div>
</c:if>

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
    var tag = getParameterByName('tag');
    if (tag){
      searchQueryObj.tag = tag;
    }

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

<c:if test="${cmsSettings.canManageFolders()}">
<div class="modal fade" id="newFolderModal" tabindex="-1" role="dialog" aria-hidden="true">
    <form class="form-horizontal" action="${pageContext.request.contextPath}/cms/folders" method="post">
        ${csrf.field()}
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                            class="sr-only">Close</span></button>
                    <h4><spring:message code="folder.manage.label.new.folder"/></h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="path" class="col-sm-2 control-label"><spring:message
                                code="folder.manage.label.path"/>:</label>

                        <div class="col-sm-10">
                            <input type="text" name="path" id="path" class="form-control">

                            <p class="help-block"><spring:message code="folder.manage.label.path.help"/></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="description" class="col-sm-2 control-label"><spring:message
                                code="folder.manage.label.description"/>:</label>

                        <div class="col-sm-10">
                            <input bennu-localized-string required-any type="text" name="description" class="form-control"
                                   id="description" \>
                        </div>

                    </div>
                </div>
                <div class="modal-footer">
                    <form action="deleteTemplate" id="templateDeleteForm" method="POST">
                        ${csrf.field()}
                        <button type="submit" class="btn btn-primary"><spring:message code="action.create"/></button>
                    </form>
                </div>
            </div>
        </div>
    </form>
</div>
<script>
  function deleteFolder(e){
    $("#deleteFolderModal form").attr("action", "${pageContext.request.contextPath}/cms/folders/delete/" + $(e.target).data("id"));

    $("#deleteFolderModal").modal("show");  
  }

  $(".delete-tag-link").on("click", deleteFolder);
</script>
<div class="modal fade" id="deleteFolderModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Are you sure?</h4>
      </div>
      <div class="modal-body">
        <p>You are about to delete this tag. There is no way to rollback this operation. Are you sure? </p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
        <button type="button" onclick="$('#deleteTagForm').submit();" class="btn btn-danger">Yes</button>
        <form action="${pageContext.request.contextPath}/cms/folders/delete/" method="post" id="deleteTagForm">${csrf.field()}</form> 
      </div>
    </div>
  </div>
</div>

</c:if>

<c:if test="${cmsSettings.canManageRoles()}">

<div class="modal fade" id="delete-role-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Are you sure?</h4>
      </div>
      <div class="modal-body">
        <p>You are about to delete this role. There is no way to rollback this operation. Are you sure? </p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
        <button type="button" onclick="$('#deleteRoleForm').submit();" class="btn btn-danger">Yes</button>
        <form action="${pageContext.request.contextPath}/cms/permissions//delete" method="post" id="deleteRoleForm">${csrf.field()}</form> 
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="connect-site-modal">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Connect with a new site</h4>
        <small>Using this functionality you are able to make this role available for an existing site.</small>
      </div>
      <form action="${pageContext.request.contextPath}/cms/permissions//addSite" method="post">
        ${csrf.field()}
        <div class="modal-body">
          <div class="form-group">
              <label class="col-sm-2 control-label">Site</label>
              <div class="col-sm-10">
                  <input required-any name="siteSlug" placeholder="Enter the site slug" class="form-control">
                  <p class="help-block">Please enter the slug of the site you want to associate with.</p> 
              </div>
          </div>
        </div>

        <div class="modal-footer">
          <button type="reset" class="btn btn-default" data-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Connect</button>
        </div>

      </form> 
    </div>
  </div>
</div>

<script>
  function connectSiteModal(e){
    $("#connect-site-modal form").attr("action", "${pageContext.request.contextPath}/cms/permissions/" + $(e.target).data("id") + "/addSite");

    $("#connect-site-modal").modal("show");  
  }

  $(".connect-site-link").on("click", connectSiteModal);

    function deleteRoleModal(e){
    $("#delete-role-modal form").attr("action", "${pageContext.request.contextPath}/cms/permissions/" + $(e.target).data("id") + "/delete");

    $("#delete-role-modal").modal("show");  
  }

  $(".delete-role-link").on("click", deleteRoleModal);
</script>

<div class="modal fade" id="create-role-modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form method="POST" action="${pageContext.request.contextPath}/cms/permissions/create">
                ${csrf.field()}
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><spanclass="sr-only"></span></button>
                    <h4>Create</h4>
                    <small>Create a new Role that can be used by other sites</small>
                </div>  


                <div class="modal-body">

                    <div class="form-group" id="role-description">
                        <label class="col-sm-2 control-label">Description</label>
                        <div class="col-sm-10">
                            <input bennu-localized-string required-any name="description" placeholder="Enter a description for this role template.">
                        </div>
                    </div>
                    <input type="text" name="permissions" id="permissions-json" class="hidden">

                    <c:forEach var="permission" items="${allPermissions}">
                        <div class="form-group permissions-inputs">
                            <div class="col-sm-12">
                                <div class="checkbox">
                                    <input type="checkbox" data-permission-name="${permission.name()}" />
                                    <label class="control-label">${permission.localizedName.content}</label>
                                </div>
                            </div>
                            <p class="help-block">${permission.localizedDescription.content}</p>
                        </div>
                    </c:forEach>

                </div>


                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><spring:message code="action.create"/></button>
                    <button type="reset" class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></button>
                </div>
            </form>

        </div>

    </div>
</div>

<script>
    $(document).ready(function() {
        function updatePermissionsJson() {
            var permissions = $('.permissions-inputs input[type="checkbox"]').filter(function(){
                return $(this).is(":checked");
            }).map(function() {
                return $(this).data("permission-name");
            });
            $('#permissions-json').val(JSON.stringify(permissions.toArray()));
        }

        updatePermissionsJson();
        $(".permissions-inputs").click(updatePermissionsJson);    
    });
</script>
</c:if>