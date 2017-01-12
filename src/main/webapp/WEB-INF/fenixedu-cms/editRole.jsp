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
<%@taglib uri="http://fenixedu.com/cms/permissions" prefix="permissions" %>

${portal.toolkit()}

<div class="page-header">
    <h1>Permissions</h1>
    <h2><a href="${pageContext.request.contextPath}/cms/sites/${role.site.slug}#roles"><small>Manage Role '${role.name.content}'</small></a></h2>
</div>

<c:if test="${permissions:canDoThis(role.site, 'MANAGE_ROLES')}">
    <p>
        <button type="button" data-toggle="modal" data-target="#edit-modal" class="btn btn-primary">
            <i class="glyphicon glyphicon-edit"></i> Associate Users
        </button>
        <c:if test="${cmsSettings.canManageGlobalPermissions()}">
          <a class="btn btn-default" href="${pageContext.request.contextPath}/cms/permissions/${role.roleTemplate.externalId}/edit">Template</a>
        </c:if>
    </p>
</c:if>

<div class="row">
	<div class="col-sm-8">
		<c:choose>
			<c:when test="${roleTemplate.permissions.get().size() == 0}">
			    <div class="panel panel-default">
			        <div class="panel-body">
			            <i>There are no permissions.</i>
			        </div>
			    </div>
			</c:when>

      <c:otherwise>
        <ul class="list-group">
          <c:forEach var="permission" items="${role.roleTemplate.permissions.get()}">
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
          <dt>Name</dt>
          <dd><span>${role.name.content}</span></dd>

          <dt>Site</dt>
          <dd><a href="${role.site.editUrl}">${role.site.name.content}</a></dd>
      		
          <dt>Number of users</dt>
      		<dd>${role.group.members.count()}</dd>
        </dl>
      </div>
    </div>
    <c:if test="${cmsSettings.canManageRoles()}">
      <div class="panel panel-danger">
        <div class="panel-heading">Danger Zone</div>
        <div class="panel-body">
          <p class="help-block">Once you delete a role, there is no going back. Please be certain.</p>
          <button data-toggle="modal" data-target="#delete-modal" class="btn btn-danger">Delete role</button>
        </div>
      </div>
    </c:if>
  </div>
	  
</div>

<c:if test="${permissions:canDoThis(role.site, 'MANAGE_ROLES')}">
  <div class="modal fade" id="delete-modal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title">Are you sure?</h4>
        </div>
        <div class="modal-body">
          <p>You are about to delete the role '<c:out value="${role.name.content}" />'. There is no way to rollback this operation. Are you sure? </p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
          <button type="button" onclick="$('#deleteForm').submit();" class="btn btn-danger">Yes</button>
          <form action="${pageContext.request.contextPath}/cms/sites/${role.site.slug}/roles/${role.externalId}/delete" method="post" id="deleteForm">${csrf.field()}</form> 
        </div>
      </div>
    </div>
  </div>
</c:if>

<div class="modal fade" id="edit-modal">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
        <form method="post" class="form-horizontal" role="form">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h3 class="modal-title">Edit Role</h3>
                <small>Change information about role '${role.name.content}'</small>
            </div>
            <div class="modal-body">
               ${csrf.field()}           
                <div class="form-group">
                    <label class="col-sm-2 control-label">Members</label>
                    <div class="col-sm-10">
                       <input bennu-group allow="nobody,custom" name="group" type="text" value='${role.getGroup().getExpression()}'/>
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <button type="reset" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary">Save</button>
            </div>

        </form>

  </div>
</div>