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
				<tr class="item-theme">
					<td>
						<h5><span class="item-theme-name">${i.getName()}</span>
							<c:if test="${i.isDefault()}">
								<span class="label label-success"><spring:message code="site.manage.label.default" /></span>
							</c:if>
						</h5>
						<div><small><spring:message code="site.manage.label.type" />:<code class="item-theme-type">${i.type}</code></small></div>
						<div><small class="item-theme-description">${i.getDescription()}</small></div>
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
                        <a class="btn btn-default btn-sm btn-duplicate" href="#" ><spring:message code="action.duplicate" /></a>
                        <form id="deleteThemeForm" action="${pageContext.request.contextPath}/cms/themes/${i.type}/delete" method="post"></form>
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
        </table>
      </c:otherwise>
</c:choose>


<div class="modal fade" id="duplicateModal" tabindex="-1" role="dialog" aria-hidden="true">
    <form action="" class="form-horizontal" method="post">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                            class="sr-only">Close</span></button>
                    <h4><spring:message code="action.newTemplate"/></h4>
                </div>
                <div class="modal-body">

                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="theme.new.label.type"/>:</label>
                        <div class="col-sm-10">
                            <input type="text" name="newThemeType" class="form-control" placeholder="Type">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="theme.new.label.name"/>:</label>
                        <div class="col-sm-10">
                            <input type="text" name="name" class="form-control" placeholder="Name">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="theme.new.label.description"/>:</label>
                        <div class="col-sm-10">
                            <textarea name="description" class="form-control" placeholder="Description">

                            </textarea>
                        </div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><spring:message code="label.make"/></button>
                </div>
            </div>
        </div>
    </form>
</div>

<script>
    function showDuplicateModal(e){
        e = $(e.target);

        var item = e.closest(".item-theme");
        var origType = $(".item-theme-type",item).html();
        var name = $(".item-theme-name",item).html();
        var description = $(".item-theme-description",item).html();
        var modal = $("#duplicateModal");
        $("form", modal).attr("action","themes/" + origType + "/duplicate");
        $("input[name='newThemeType']", modal).val("");
        $("input[name='name']", modal).val(name);
        $("textarea[name='description']", modal).val(description);
        modal.modal('show');
    }
    $(".btn-duplicate").on("click",showDuplicateModal)
</script>