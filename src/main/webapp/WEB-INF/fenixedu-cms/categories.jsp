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
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://fenixedu.com/cms/permissions" prefix="permissions" %>

${portal.toolkit()}

<div class="page-header">
    <h1><spring:message code="categories.manage.title" /></h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small></h2>
</div>

<p>
    <c:choose>
        <c:when test="${permissions:canDoThis(site, 'EDIT_CATEGORY,CREATE_CATEGORY')}">
            <button type="button" data-toggle="modal" data-target="#create-category" class="btn btn-primary">
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
    <c:when test="${categories.size() == 0}">
        <div class="panel panel-default">
          <div class="panel-body">
            <i><spring:message code="categories.manage.emptyCategories"/></i>
          </div>
        </div>
    </c:when>

    <c:otherwise>
    <table class="table">
        <thead>
            <tr>
                <th><spring:message code="post.manage.label.name"/></th>
                <th><spring:message code="post.manage.label.creationDate"/></th>
                <th>Posts</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="category" items="${categories}">
                <tr>
                    <td>
                       <c:choose>
                            <c:when test="${permissions:canDoThis(site, 'EDIT_CATEGORY')}">
                                <h5><a href="${category.getEditUrl()}">${category.name.content}</a></h5>
                            </c:when>
                            <c:otherwise>
                                <h5>${category.name.content}</h5>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${category.creationDate.toString('dd MMMM yyyy, HH:mm', locale)}</td>
                    <td>
                        <span class="badge">${category.postsSet.size()}</span>
                        <div class="btn-group pull-right">
                            <button type="button" class="btn btn-link dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <span class="carret"></span>
                            </button>
                            <ul class="dropdown-menu">
                                <c:if test="${permissions:canDoThis(site, 'EDIT_CATEGORY')}">
                                    <li><a href="${category.getEditUrl()}"><i class="glyphicon glyphicon-edit"></i>&nbsp;Edit</a></li>
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

<c:if test="${permissions:canDoThis(site, 'CREATE_CATEGORY')}">
    <div class="modal fade" id="create-category" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form class="form-horizontal" action="${pageContext.request.contextPath}/cms/categories/${site.slug}/create" method="post" role="form">
                ${csrf.field()}
                
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"> </button>
                    <h3 class="modal-title">New Category</h3>
                    <small>Please specify the name for the new category</small>
                </div>

                <div class="modal-body">
                    <div class="form-group">
                        <label class="col-sm-2 control-label"><spring:message code="categories.create.label.name"/></label>
                        <div class="col-sm-10">
                            <input bennu-localized-string required-any name="name" placeholder="<spring:message code="categories.create.label.name"/>">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">Slug</label>

                        <div class="col-sm-10">
                            <input name="type" class="form-control" type="text" id="category-slug" readonly="true">
                            <p class="help-block">This code is used internally and is not shared with the users. However it must be unique.</p>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button type="Submit" class="btn btn-primary"><spring:message code="action.create"/></button>
                </div>
            </form>
        </div>
      </div>
    </div>


    <script type="text/javascript">
        function slugify(text) {
          return text.toString().toLowerCase()
            .replace(/\s+/g, '-')           // Replace spaces with -
            .replace(/[^\w\-]+/g, '')       // Remove all non-word chars
            .replace(/\-\-+/g, '-')         // Replace multiple - with single -
            .replace(/^-+/, '')             // Trim - from start of text
            .replace(/-+$/, '');            // Trim - from end of text
        }

        $("#create-category [name='name']").change(function(e) {
          var nameJson = $(e.target).val();
          var name = Bennu.localizedString.getContent(JSON.parse(nameJson));
          var slug = slugify(name);
          $("#create-category [name='type']").val(slug);
        });
        
        $(document).ready(function() {
            setTimeout(function() {
                if(window.location.hash === '#new') {
                    $('#create-category').modal();
                }
            });
        });
    </script>

</c:if>