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
  <h2><small><c:out value="${theme.name}" /></small></h2>
</div>

<p>
    <a href="${pageContext.request.contextPath}/cms/themes/${theme.type}/edit" class="btn btn-primary"><i class="glyphicon glyphicon-edit"></i> Edit</a>

    <a href="${pageContext.request.contextPath}/cms/themes/${theme.type}/edit" class="btn btn-default"><i class="glyphicon glyphicon-cloud-download"></i> Export</a>
</p>

<div class="row">
    <div class="col-sm-8">
        <div class="hidden-xs thumbnail">
             <img src="http://i.imgur.com/t8i1Zrn.png" style="max-width:100%" data-holder-rendered="true" />
        </div>


        <p>
            <c:out value="${theme.description}"/>
        </p>
        
    </div>
    <div class="col-sm-4">
        <div class="panel panel-primary">
          <div class="panel-heading">Details</div>
          <div class="panel-body">
            <dl class="dl-horizontal">
                <dt>Type</dt>
                <dd><samp><c:out value="${theme.type}" /></samp></dd>
                <c:if test="${theme.extended != null}">
                <dt>Extends</dt>
                <dd><a
        href="../${theme.extended.type}/see">${theme.extended.name}</a></dd>
                </c:if>
                <dt>Size</dt>
                <dd>${cms.prettySize(theme.files.totalSize)}</dd>
                <c:if test="${theme.isDefault()}">
                <dt>Default</dt>
                <dd><span class="label label-success">Yes</span></dd>
                </c:if>
            </dl>
          </div>
        </div>

        <div class="panel panel-danger">
          <div class="panel-heading">Danger Zone</div>
          <div class="panel-body">
            <p class="help-block">Once you delete a theme, there is no going back. Please be certain.</p>
            <button data-toggle="modal" data-target="#deleteModal" class="btn btn-danger">Delete this Theme</button>
            
          </div>
        </div>
    </div>
</div>

<h4>Some sites using this theme</h4>
<div class="row">
    <div class="col-sm-6 col-md-4">
            <div class="thumbnail">
              <div class="caption">
                <h5><a href="${pageContext.request.contextPath}/cms/themes/${i.type}/see">Teste</a></h3>
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Rerum dolorem maxime cupiditate illum! Labore recusandae consequatur eveniet quasi repudiandae commodi nam, incidunt cum maiores magni totam voluptas aperiam eligendi quisquam.</p>
                </div>
            </div>
    </div>
</div>
<p>
    <a href="" class="btn btn-xs btn-default">View all</a>
</p>

<div class="modal fade" id="deleteModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Are you sure?</h4>
      </div>
      <div class="modal-body">
        <p>You are about to delete the theme '<c:out value="${theme.name}" />'. There is no way to rollback this opeartion. Are you sure? </p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
        <button type="button" onclick="$('#deleteThemeForm').submit();" class="btn btn-danger">Yes</button>
        <form action="delete" method="post" id="deleteThemeForm">
        </form> 
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


