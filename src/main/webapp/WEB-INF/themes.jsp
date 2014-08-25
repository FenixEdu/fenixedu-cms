<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1><spring:message code="site.manage.title" /></h1>
<p>
  <a href="${pageContext.request.contextPath}/cms/themes/new" class="btn btn-default"><spring:message code="site.manage.label.newTheme" /></a>
  <a href="${pageContext.request.contextPath}/cms/themes/create" class="btn btn-default"><spring:message code="site.manage.label.addTheme" /></a>
  <c:if test="${themes.size() == 0}">
    <a href="${pageContext.request.contextPath}/cms/themes/loadDefault" class="btn btn-default"><spring:message code="site.manage.label.loadDefault" /></a>
  </c:if>
</p>

<c:choose>
      <c:when test="${themes.size() == 0}">
     	<spring:message code="site.manage.label.emptySites" />
      </c:when>

      <c:otherwise>
		<table class="table table-striped table-bordered">
	      	<thead>
				<tr>
					<th class="col-md-6"><spring:message code="site.manage.label.name" /></th>
					<th><spring:message code="site.manage.label.createdBy" /></th>
					<th><spring:message code="site.manage.label.templates" /></th>
                    <th>&nbsp;</th>
				</tr>
	      	</thead>
			<tbody>
				<c:forEach var="i" items="${themes}">
				<tr>
					<td>
						<h5>${i.getName()}
							<c:if test="${true}">
								<span class="label label-success"><spring:message code="site.manage.label.default" /></span>
							</c:if>
						</h5>
						<div><small><spring:message code="site.manage.label.type" />:<code>${i.type}</code></small></div>
						<div><small>${i.getDescription()}</small></div>
					</td>
					<td>

                        <c:choose>
                            <c:when test="${i.createdBy.username != null}">
                                ${i.createdBy.username}
                            </c:when>
                            <c:otherwise>
                                <i>Imported</i>
                            </c:otherwise>
                        </c:choose>
                    </td>
					<td>${i.templatesSet.size()}</td>
					<td>
						<div class="btn-group">
                            <a class="btn btn-danger btn-sm" onclick="document.getElementById('deleteThemeForm').submit();"><span class="glyphicon glyphicon-trash"></a>
							<a class="btn btn-default btn-sm" href="${pageContext.request.contextPath}/cms/themes/${i.type}/see"><spring:message code="action.more" /></a>
						</div>
                        <a class="btn btn-default btn-sm" href="${pageContext.request.contextPath}/cms/themes/${i.type}/see"><spring:message code="action.duplicate" /></a>
                        <form id="deleteThemeForm" action="${pageContext.request.contextPath}/cms/themes/${i.type}/delete" method="post"></form>
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
        </table>
      </c:otherwise>
</c:choose>
