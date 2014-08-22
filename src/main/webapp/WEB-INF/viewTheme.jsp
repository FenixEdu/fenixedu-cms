<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1><spring:message code="theme.view.title" /></h1>
<h2>${theme.name}</h2>
<p>${theme.description}</p>
<div>Type:<code>${theme.type}</code></div>

<div class="col-sm-8">
	<h2 id="files">Files <span class="badge">${cms.prettySize(theme.files.totalSize)}</span></h2>
	<table class="table table-striped table-bordered">
		<thead>
			<tr>
				<th><spring:message code="theme.view.label.path" /></th>
				<th><spring:message code="theme.view.label.size" /></th>
				<th><spring:message code="theme.view.label.last.modification"/></th>
				<th><spring:message code="theme.view.label.operations" /></th>
			</tr>
			</thead>
		<tbody>
			<c:forEach var="i" items="${theme.files.files}">
				<tr>
					<td><code title="${i.contentType}">${i.fileName}</code></td>
					<td>${cms.prettySize(i.fileSize)}</td>
					<td>${i.lastModified.toString('dd-MM-YYYY hh:mm:ss')}</td>
					<td>
						<button class="btn btn-danger btn-sm" data-toggle="modal" data-file="${i.fullPath}" data-target="#fileDeleteModal">
							<spring:message code="action.delete" />
						</button>
						<c:if test="${supportedTypes.contains(i.contentType)}">
							<a href="editFile/${i.fullPath}" class="btn btn-primary btn-sm"><spring:message code="action.edit" /></a>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
<div class="col-sm-4">
	<h2>Templates</h2>
	<table class="table table-striped table-bordered">
      <thead>
        <tr>
          <th class="col-md-6"><spring:message code="theme.view.label.name" /></th>
          <th><spring:message code="theme.view.label.type" /></th>
          <th><spring:message code="theme.view.label.path" /></th>
        </tr>
      </thead>
      <tbody>
		<c:forEach var="template" items="${theme.templatesSet}">
		  <tr>
		    <td>
		      <h5>${template.name}</h5>
		      <div><small>${template.description}</small></div>
		    </td>
		    <td><code>${template.type}</code></td>
		    <td><code>${template.filePath}</code></td>
		  </tr>
		</c:forEach>
        </tbody>
	</table>
</div>
<div class="modal fade" id="fileDeleteModal" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
			     <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4><spring:message code="action.delete"/></h4>
			</div>
			<div class="modal-body">
			<spring:message code="theme.view.label.delete.confirmation"/> <code id="fileName"></code>?
			</div>
			<div class="modal-footer">
				<form action="deleteFile" id="fileDeleteForm" method="POST">
					<input type="hidden" name="path" />
					<button type="submit" class="btn btn-danger"><spring:message code="label.yes"/></button>
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="label.no"/></button>
				</form>
			</div>
		</div>
	</div>
</div>

<script>
$("button[data-target='#fileDeleteModal']").on('click', function(event) {
	var fileName = $(event.target).attr('data-file');
	$('#fileName').html(fileName);
	$('#fileDeleteForm')[0].path.value = fileName;
});
</script>