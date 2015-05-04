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
  <h2><small>Manage your content</small></h2>
</div>

<c:if test="${isManager}">
<p>
<div class="row">
  <div class="col-sm-8">
    <a href="${pageContext.request.contextPath}/cms/sites/new" class="btn btn-primary">Create</a>
    <a href="${pageContext.request.contextPath}/cms/folders" class="btn btn-default"><i class="glyphicon glyphicon-folder-close"></i> Folders</a>
    <a href="${pageContext.request.contextPath}/cms/themes" class="btn btn-default"><i class="icon icon-brush"></i> Themes</a>
    <button type="button" class="btn btn-default">Settings</button>
    <a href="#" data-toggle="modal" data-target="#defaultSite" class="btn btn-default">Default site</a>
  </div>
  <div class="col-sm-4">
    <input type="search" class="form-control pull-right" placeholder="Search sites">
  </div>
</div>
</p>
</c:if>
<c:choose>
    <c:when test="${sites.size() == 0}">
        <div class="panel panel-default">
          <div class="panel-body">
            <spring:message code="site.manage.label.emptySites"/>
          </div>
        </div>
    </c:when>

  <c:otherwise>

    <ul class="list-group">
      <c:forEach var="i" items="${sites}">
        <li class="list-group-item">
                <h3><a href="${pageContext.request.contextPath}/cms/sites/${i.slug}">${i.getName().getContent()}</a>
                    
                </h3>
            <div>
              ${i.getDescription().getContent()}
            </div>
          
              <c:choose>
                  <c:when test="${ i.published }">
                      <span class="label label-primary">Public</span>
                  </c:when>
                  <c:otherwise>
                      <span class="label label-default">Draft</span>
                  </c:otherwise>
              </c:choose>
              <c:if test="${i.getEmbedded()}">
                  <p><span class="label label-info">Embedded</span></p>
              </c:if>
              <c:if test="${i.isDefault()}">
                  <span class="label label-success"><spring:message code="site.manage.label.default"/></span>
              </c:if>
            <div class="btn-group pull-right">
              <a href="${i.fullUrl}" class="btn btn-icon btn-default"><i class="glyphicon glyphicon-link"></i></a>
              <a href="${pageContext.request.contextPath}/cms/sites/${i.slug}" class="btn btn-icon btn-default"><i class="glyphicon glyphicon-eye-close"></i></a>
              <a href="${pageContext.request.contextPath}/cms/sites/${i.slug}" class="btn btn-icon btn-primary"><i class="glyphicon glyphicon-cog"></i></a>
            </div>
        </li>
      </c:forEach>
    </ul>
    <c:if test="${numberOfPages != 1}">
    <div class="row">
        <div class="col-md-2 col-md-offset-5">
            <ul class="pagination">
                <li class="${currentPage <= 0 ? 'disabled' : 'active'}"><a href="${pageContext.request.contextPath}/cms/sites/manage/${page - 1}">«</a></li>
                <li class="disabled"><a href="#">${currentPage + 1} / ${numberOfPages}</a></li>
                <li class="${currentPage + 1 >= numberOfPages ? 'disabled' : 'active'}"><a href="${pageContext.request.contextPath}/cms/sites/manage/${page + 1}">»</a></li>
            </ul>
        </div>
    </div>
    </c:if>
    </c:otherwise>
</c:choose>

<div class="modal fade" id="defaultSite" tabindex="-1" role="dialog" aria-hidden="true">
    <form action="${pageContext.request.contextPath}/cms/sites/default" class="form-horizontal" method="post">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span><span
                            class="sr-only">Close</span></button>
                    <h4><spring:message code="action.set.default.site"/></h4>
                </div>
                <div class="modal-body">

                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message
                                code="theme.site"/>:</label>

                        <div class="col-sm-10">
                            <select class="form-control" name="slug">
                                <option value="">-</option>
                                <c:forEach var="i" items="${sites}">
                                    <option value="${i.slug}">${i.name.content}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><spring:message code="label.save"/></button>
                </div>
            </div>
        </div>
    </form>
</div>