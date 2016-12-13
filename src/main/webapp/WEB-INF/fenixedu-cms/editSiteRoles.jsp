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
    <h1>Manage site roles</h1>
    <h2><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}"><small>Site</small></a></h2>
</div>

<c:if test="${templates.size() > 0}">
	<p>
	    <button type="button" data-toggle="modal" data-target="#add-role-modal" class="btn btn-primary">
	        <i class="glyphicon glyphicon-edit"></i> Associate Role
	    </button>
	</p>
</c:if>

<c:choose>
    <c:when test="${roles.size() == 0}">
        <div class="panel panel-default">
            <div class="panel-body">
                <i>There are no roles for this site.</i>
            </div>
        </div>
    </c:when>

    <c:otherwise>
        <table class="table">
            <thead>
            <tr>
                <th><spring:message code="page.manage.label.name"/></th>
                <th>Permissions</th>
                <th>Users</th>
                <th><spring:message code="page.manage.label.operations"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="role" items="${roles}">
                <tr>
                    <td>
                        <h5><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}/roles/${role.externalId}/edit">${role.roleTemplate.description.content}</a></h5>
                    </td>
                    <td>${role.roleTemplate.permissions.get().size()}</td>
                    <td>${role.group.toGroup().getMembers().size()}</td>
                    <td>
                        <div class="btn-group">
	                        <button data-toggle="modal" data-target="#delete-modal" class="btn btn-icon btn-danger" data-role-id="${role.externalId}"><i class="glyphicon glyphicon-trash"></i></button>
                            
                            <a href="${pageContext.request.contextPath}/cms/sites/${site.slug}/roles/${role.externalId}/edit" class="btn btn-icon btn-primary">
                               <i class="glyphicon glyphicon-edit"></i>
                            </a>

                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>


<div class="modal fade" id="add-role-modal">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
        <form method="post" class="form-horizontal" role="form" action="${pageContext.request.contextPath}/cms/sites/${site.slug}/roles/add">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h3 class="modal-title">Add Role</h3>
                <small>Make a new role available for this site</small>
            </div>
            <div class="modal-body">
               ${csrf.field()}
               <div class="form-group">
                    <label class="col-sm-2 control-label">Role</label>
                    <div class="col-sm-10">
	                    <select name="roleTemplateId" class="form-control" autofocus>
	                        <c:forEach var="roleTemplate" items="${templates}">
	                            <option value="${roleTemplate.externalId}">${roleTemplate.description.content}</option>
	                        </c:forEach>
	                    </select>
                    </div>
                </div>
                <p class="help-block">Please select the role you want to make available for this site.</p>
            </div>

            <div class="modal-footer">
                <button type="reset" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary">Save</button>
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
        <p>You are about to delete this role. There is no way to rollback this operation. Are you sure? </p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
        <button type="button" onclick="$('#deleteForm').submit();" class="btn btn-danger">Yes</button>
        <form action="#" method="post" id="deleteForm">${csrf.field()}</form> 
      </div>
    </div>
  </div>
</div>

<script type="application/javascript">			
	$('[data-role-id]').click(function(e){
		var action = '${pageContext.request.contextPath}/cms/sites/${site.slug}/roles/' + $(this).data('role-id') + '/delete';
		$('#delete-modal form').attr('action', action);
	});
</script>
