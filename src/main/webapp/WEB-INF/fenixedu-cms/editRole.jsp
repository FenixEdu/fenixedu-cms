<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
${portal.toolkit()}

<div class="page-header">
    <h1>Permissions</h1>
    <h2><a href="${pageContext.request.contextPath}/cms/permissions/${role.roleTemplate.externalId}/edit"><small>Manage Role '${role.name.content}'</small></a></h2>
</div>

<p>
    <button type="button" data-toggle="modal" data-target="#edit-modal" class="btn btn-primary">
        <i class="glyphicon glyphicon-edit"></i> Edit
    </button>
</p>

<div class="row">
	<div class="col-sm-8">
		<c:choose>
			<c:when test="${role.group.members.count() == 0}">
			    <div class="panel panel-default">
			        <div class="panel-body">
			            <i>There are no users with this role.</i>
			        </div>
			    </div>
			</c:when>

			<c:otherwise>
			    <c:forEach var="user" items="${role.group.members.toArray()}">
			        <li class="list-group-item">
			            <h3>${user.name}</h3>
			            <span class="label label-primary"> user</span>
			        </li>
			    </c:forEach>
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

          <dt>Template</dt>
          <dd>
          	<a href="${pageContext.request.contextPath}/cms/permissions/${role.roleTemplate.externalId}/edit">
          		${role.roleTemplate.description.content}
        		</a>
    		  </dd>

          <dt>Site</dt>
          <dd><a href="${role.site.editUrl}">${role.site.name.content}</a></dd>
      		
          <dt>Number of users</dt>
      		<dd>${role.group.members.count()}</dd>
        </dl>
        </div>
    </div>

    <div class="panel panel-primary">
      <div class="panel-heading">Permissions</div>
      <div class="panel-body">
        <dl class="dl-horizontal">
          <dt>Number of Permissions</dt>
          <dd><c:out value="${role.roleTemplate.permissions.get().size()}"></c:out></dd>
          <c:forEach var="permission" items="${role.roleTemplate.permissions.get()}">
            <dt>${permission.localizedName.content}</dt>
          </c:forEach>
        </dl>
      </div>
    </div>
	  
    <div class="panel panel-danger">
        <div class="panel-heading">Danger Zone</div>
        <div class="panel-body">
          <p class="help-block">Once you delete a role, there is no going back. Please be certain.</p>
          <button data-toggle="modal" data-target="#delete-modal" class="btn btn-danger">Delete this role</button>
        </div>
      </div>

	</div>
</div>


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
                    <label class="col-sm-2 control-label">Name</label>
                    <div class="col-sm-10">
                        <input type="text" name="name" bennu-localized-string placeholder="Name of this role" value='${role.name.json()}'>
                    </div>
                </div>
            
                <div class="form-group">
                    <label class="col-sm-2 control-label">Members</label>
                    <div class="col-sm-10">
                       <input bennu-group allow="nobody,custom" name="group" type="text" value='${role.getGroup().toGroup().getExpression()}'/>
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
        <form action="${pageContext.request.contextPath}/cms/permissions/${roleTemplate.externalId}/${role.externalId}/delete" method="post" id="deleteForm">${csrf.field()}</form> 
      </div>
    </div>
  </div>
</div>