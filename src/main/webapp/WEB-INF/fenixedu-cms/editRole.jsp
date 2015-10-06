<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://fenixedu.com/cms/permissions" prefix="permissions" %>

${portal.toolkit()}

<div class="page-header">
    <h1>Permissions</h1>
    <h2><a href="${pageContext.request.contextPath}/cms/sites/${role.site.slug}/edit#permissions"><small>Manage Role '${role.name.content}'</small></a></h2>
</div>

<c:if test="${permissions:canDoThis(role.site, 'MANAGE_ROLES')}">
    <p>
        <button type="button" data-toggle="modal" data-target="#edit-modal" class="btn btn-primary">
            <i class="glyphicon glyphicon-edit"></i> Associate Users
        </button>
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