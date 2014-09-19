<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1><spring:message code="folder.manage.label.folders"/></h1>
<p>
  <button class="btn btn-default" data-toggle="modal" data-target="#newFolderModal"><spring:message code="folder.manage.label.new.folder"/></button>
</p>

<c:if test="${! empty folders}">
	<table class="table table-striped table-bordered">
		<thead>
			<th><spring:message code="folder.manage.label.description"/></th>
			<th><spring:message code="folder.manage.label.path"/></th>
			<th><spring:message code="folder.manage.label.site.number"/></th>
			<th><spring:message code="folder.manage.label.operations"/></th>
		</thead>
		<tbody>
			<c:forEach var="folder" items="${folders}">
				<tr>
					<td>${folder.functionality.description.content}</td>
					<td>
						<code>${folder.functionality.fullPath}</code>
						<c:if test="${folder.resolver != null}">
							<span class="badge">Custom Resolver</span>
						</c:if>
					</td>
					<td>${folder.siteSet.size()}</td>
					<td>
						<div class="btn-group">
							<a href="${pageContext.request.contextPath}/cms/folders/resolver/${folder.externalId}" class="btn btn-default">Resolver</a>
							<a href="${pageContext.request.contextPath}/cms/folders/delete/${folder.externalId}" class="btn btn-danger"
							   ${folder.siteSet.size() > 0 ? 'disabled' : ''}>
								<spring:message code="action.delete"/>
							</a>
						</div>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>


<div class="modal fade" id="newFolderModal" tabindex="-1" role="dialog" aria-hidden="true">
	<form class="form-horizontal" method="post">
	    <div class="modal-dialog">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
	                        class="sr-only">Close</span></button>
	                <h4><spring:message code="folder.manage.label.new.folder"/></h4>
	            </div>
	            <div class="modal-body">
                    <div class="form-group">
                        <label for="path" class="col-sm-2 control-label"><spring:message code="folder.manage.label.path"/>:</label>
                        <div class="col-sm-10">
                            <input type="text" name="path" id="path" class="form-control">
                            <p class="help-block"><spring:message code="folder.manage.label.path.help"/></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="description" class="col-sm-2 control-label"><spring:message code="folder.manage.label.description"/>:</label>
                        <div class="col-sm-10">
							<input bennu-localized-string required-any type="text" name="description" class="form-control" id="description"\>
                        </div>

                    </div>
                    
	            </div>
	            <div class="modal-footer">
	                <form action="deleteTemplate" id="templateDeleteForm" method="POST">
	                    <button type="submit" class="btn btn-primary"><spring:message code="action.create"/></button>
	                </form>
	            </div>
	        </div>
	    </div>
    </form>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
<script src="${pageContext.request.contextPath}/static/js/toolkit.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/toolkit/toolkit.css"/>
