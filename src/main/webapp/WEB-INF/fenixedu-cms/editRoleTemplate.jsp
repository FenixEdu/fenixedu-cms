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
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
${portal.toolkit()}

<div class="page-header">
    <h1>Permissions</h1>
    <h2><a href="${pageContext.request.contextPath}/cms/permissions"><small>Manage Role</small></a></h2>
</div>

<div row="row">
  <div class="col-sm-6">
    <a href="#" data-toggle="modal" data-target="#edit-modal" class="btn btn-default btn-primary">
      <span class="glyphicon glyphicon-edit"></span>&nbsp;Edit
    </a>
    <a href="#" data-toggle="modal" data-target="#connect-site-modal" class="btn btn-default">
      <span class="glyphicon glyphicon-plus"></span>&nbsp;Site
    </a>
  </div>

  <div class="col-sm-6">
    <button data-toggle="modal" data-target="#delete-modal" class="btn pull-right btn-danger">Delete</button>
  </div>
</div>

<div class="row">
  <div class="col-sm-8">
    <c:choose>
      <c:when test="${roleTemplate.permissions.get().size() == 0}">
        <div class="panel panel-default">
          <div class="panel-body">
            <i>There are no sites or roles associated with this template.</i>
          </div>
        </div>
      </c:when>
      <c:otherwise>
        <ul class="list-group">
          <c:forEach var="permission" items="${roleTemplate.permissions.get()}">
            <li class="list-group-item">
              <h3>${permission.localizedName.content}</h3>
              <p class="help-block">${permission.localizedDescription.content}</p>
            </li>
          </c:forEach>
        </ul>
      </c:otherwise>
    </c:choose>
  </div>

  <div class="col-sm-4">
      <div class="panel panel-primary">
        <div class="panel-heading">Details</div>
        <div class="panel-body">
          <dl class="dl-horizontal">
              <dt>Description</dt>
              <dd>${roleTemplate.name.content}</dd>
              <dt>Number of Sites</dt>
              <dd>${roleTemplate.numSites}</dd>
          </dl>
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
      <form action="${pageContext.request.contextPath}/cms/permissions/${roleTemplate.externalId}/addSite" method="post">
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

<div class="modal fade" id="delete-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Are you sure?</h4>
      </div>
      <div class="modal-body">
        <p>You are about to delete the role '<c:out value="${roleTemplate.name.content}" />'. There is no way to rollback this operation. Are you sure? </p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
        <button type="button" onclick="$('#deleteForm').submit();" class="btn btn-danger">Yes</button>
        <form action="${pageContext.request.contextPath}/cms/permissions/${roleTemplate.externalId}/delete" method="post" id="deleteForm">${csrf.field()}</form> 
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="edit-modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form method="POST" action="${pageContext.request.contextPath}/cms/permissions/${roleTemplate.externalId}/edit">
                ${csrf.field()}
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><spanclass="sr-only"></span></button>
                    <h4>Update</h4>
                    <small>Change the permissions of this role</small>
                </div>  


                <div class="modal-body">
                    <div class="form-group" id="role-description">
                        <label class="col-sm-2 control-label">Description</label>
                        <div class="col-sm-10">
                            <input bennu-localized-string required-any name="description" placeholder="Enter a description for this role template." value='${roleTemplate.name.json()}'>
                        </div>
                    </div>

                    <input type="text" name="permissions" id="permissions-json" class="hidden">


                    <c:forEach var="permission" items="${allPermissions}">
                        <div class="form-group permissions-inputs">
                            <div class="col-sm-12">
                                <div class="checkbox">
                                    <input type="checkbox" data-permission-name="${permission.name()}" ${roleTemplate.permissions.get().contains(permission) ? 'checked' : ''}/>
                                    <label class="control-label">${permission.localizedName.content}</label>
                                </div>
                            </div>
                            <p class="help-block">${permission.localizedDescription.content}</p>
                        </div>
                    </c:forEach>


                </div>

                <div class="modal-footer">
                    <button type="reset" class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></button>
                    <button type="submit" class="btn btn-primary"><spring:message code="action.save"/></button>
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

