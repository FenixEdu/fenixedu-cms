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
<%@taglib uri="http://fenixedu.com/cms/permissions" prefix="permissions" %>

${portal.toolkit()}

<div class="page-header">
    <h1><spring:message code="menu.manage.title" /></h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small></h2>
</div>

<p>
    <c:choose>
        <c:when test="${permissions:canDoThis(site, 'EDIT_MENU') && permissions:canDoThis(site, 'CREATE_MENU')}">
            <button type="button" data-toggle="modal" data-target="#create-menu" class="btn btn-primary">
                <i class="glyphicon glyphicon-plus"></i> New
            </button>
        </c:when>
        <c:otherwise>
            <button type="button" class="btn btn-primary disabled">
                <i class="glyphicon glyphicon-plus"></i> New
            </button>
        </c:otherwise>
    </c:choose>
</p>

<c:choose>
      <c:when test="${menus.size() == 0}">
        <div class="panel panel-default">
          <div class="panel-body">
            <i><spring:message code="menu.manage.title.label.emptyMenus" /></i>
          </div>
        </div>
      </c:when>

      <c:otherwise>
        <table class="table">
          <thead>
              <tr>
                  <th><spring:message code="post.manage.label.name"/></th>
                  <th><spring:message code="post.manage.label.creationDate"/></th>
                  <th>Items</th>
              </tr>
          </thead>
          <tbody>
              <c:forEach var="menu" items="${menus}">
                <c:set var="menuEditUrl" value="${pageContext.request.contextPath}/cms/menus/${site.slug}/${menu.slug}/edit"></c:set>
                <c:set var="menuBaseUrl" value="${pageContext.request.contextPath}/cms/menus/${site.slug}/${menu.slug}"></c:set>
                  <tr>
                  <td>
                    <c:choose>
                      <c:when test="${permissions:canDoThis(site, 'EDIT_MENU')}">
                          <h5><a href="${menuEditUrl}">${menu.name.content}</a></h5>
                      </c:when>
                      <c:otherwise>
                          <h5>${menu.name.content}</h5>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>${menu.creationDate.toString('dd MMMM yyyy, HH:mm', locale)}</td>
                  <td>
                    <span class="badge">${menu.getItemsSet().size()}</span>
                    <div class="btn-group pull-right">
                        <button type="button" class="btn btn-link dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <span class="glyphicon glyphicon glyphicon-chevron-down"></span>
                        </button>
                        <ul class="dropdown-menu">
                            <c:if test="${permissions:canDoThis(site, 'EDIT_MENU')}">
                                <li><a href="${menuEditUrl}"><i class="glyphicon glyphicon-edit"></i>&nbsp;Edit</a></li>
                            </c:if>
                            <c:if test="${permissions:canDoThis(site, 'EDIT_MENU') && menu.order>1}">
                                <li><a data-menuup="${menu.slug}" href="#"><i class="glyphicon glyphicon-chevron-up"></i>&nbsp;Up</a></li>
                            </c:if>
                            <c:if test="${permissions:canDoThis(site, 'EDIT_MENU') && menu.order<site.menusSet.size()}">
                                <li><a data-menudown="${menu.slug}" href="#"><i class="glyphicon glyphicon-chevron-down"></i>&nbsp;Down</a></li>
                            </c:if>
                            <c:if test="${permissions:canDoThis(site, 'DELETE_MENU')}">
                                <li><a data-menu="${menu.slug}" href="#"><i class="glyphicon glyphicon-trash"></i>&nbsp;Delete</a></li>
                            </c:if>
                        </ul>
                    </div>
                  </td>
                </tr>
              </c:forEach>
          </tbody>
        </table>
      </c:otherwise>

</c:choose>

<c:if test="${permissions:canDoThis(site, 'DELETE_MENU')}">
  <div class="modal fade" id="delete-modal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog">
          <div class="modal-content">
              <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                  </button>
                  <h4><spring:message code="menu.manage.label.delete.menu"/></h4>
              </div>
              <div class="modal-body">
                  <p><spring:message code="menu.manage.label.delete.menu.message"/></p>
              </div>
              <div class="modal-footer">
                  <form id="delete-form" method="POST">
                      ${csrf.field()}
                      <button type="submit" class="btn btn-danger"><spring:message code="action.delete"/></button>
                      <a class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></a>
                  </form>
              </div>
          </div>
      </div>
  </div>

  <script type="application/javascript">
    $(document).ready(function () {
        $("a[data-menu]").on('click', function(el) {
            var menuSlug = $(this).data('menu');
            $('#delete-form').attr('action', '${pageContext.request.contextPath}/cms/menus/${site.slug}/' + menuSlug + '/delete');
            $('#delete-modal').modal('show');
        });
        $("a[data-menuup]").on('click', function(el) {
            $.post('${pageContext.request.contextPath}/cms/menus/${site.slug}/'+$(this).data('menuup')+'/up',function(data){location.reload();});
        });
        $("a[data-menudown]").on('click', function(el) {
            $.post('${pageContext.request.contextPath}/cms/menus/${site.slug}/'+$(this).data('menudown')+'/down',function(data){location.reload();});
        });
        setTimeout(function() {
          if(window.location.hash === '#new') {
            $('#create-menu').modal();
          }
        });
    });
  </script>
</c:if>

<c:if test="${permissions:canDoThis(site, 'CREATE_MENU')}">
  <div class="modal fade" id="create-menu" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
          <form class="form-horizontal" action="${pageContext.request.contextPath}/cms/menus/${site.slug}/create" method="post" role="form">
              ${csrf.field()}
              <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-hidden="true"> </button>
                  <h3 class="modal-title">New Menu</h3>
                  <small>This could be the start of something great!</small>
              </div>
              <div class="modal-body">
                  <div class="${emptyName ? "form-group has-error" : "form-group"}">
                      <label class="col-sm-2 control-label"><spring:message code="post.create.label.name"/></label>

                      <div class="col-sm-10">
                          <input bennu-localized-string required-any name="name" placeholder="<spring:message code="post.create.label.name" />">
                      </div>
                  </div>
              </div>
              <div class="modal-footer">
                  <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                  <button type="Submit" class="btn btn-primary">Make</button>
              </div>
          </form>
      </div>
    </div>
  </div>
</c:if>
