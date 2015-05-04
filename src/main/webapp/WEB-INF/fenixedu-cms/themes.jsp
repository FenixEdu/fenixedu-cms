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

<div class="page-header">
  <h1>Sites</h1>
  <h2><small>Themes</small></h2>
</div>
<p>
  <a href="${pageContext.request.contextPath}/cms/themes/new" class="btn btn-default">New</a>
  <a href="${pageContext.request.contextPath}/cms/themes/create" class="btn btn-default">Import</a>
  <c:if test="${themes.size() == 0}">
    <a href="${pageContext.request.contextPath}/cms/themes/loadDefault" class="btn btn-default"><spring:message code="site.manage.label.loadDefault" /></a>
  </c:if>
</p>

<c:choose>
      <c:when test="${themes.size() == 0}">
        <div class="panel panel-default">
        <div class="panel-body">
     	  <spring:message code="site.manage.label.emptySites" />
          </div>
        </div>
      </c:when>

      <c:otherwise>
	      	<div class="row">
				<c:forEach var="i" items="${themes}">
				<div class="col-sm-6 col-md-4">

                <div class="thumbnail">
                    <img src="http://i.imgur.com/t8i1Zrn.png" style="max-width:100%" data-holder-rendered="true" >
                  <div class="caption">
                    <h3><a href="${pageContext.request.contextPath}/cms/themes/${i.type}/see">${i.getName()}</a></h3>
                    <p>${i.getDescription()}</p>
                        <c:if test="${i.isDefault()}">
                            <span class="label label-success"><spring:message code="site.manage.label.default" /></span>
                        </c:if>
                    <div class="btn-group pull-right">
                        <a class="btn btn-icon btn-default" href="${pageContext.request.contextPath}/cms/themes/${i.type}/see"><i class="glyphicon glyphicon-cog"></i></a>
                    </div>
				    </div>
                </div>
                </div>
				</c:forEach>
			</div>
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