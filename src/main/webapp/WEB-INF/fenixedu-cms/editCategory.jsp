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
    <h1>Category</h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/categories/${site.slug}">${category.name.content}</a></small></h2>
</div>

<p>
    <c:choose>
        <c:when test="${!category.privileged || permissions:canDoThis(site, 'EDIT_PRIVILEGED_CATEGORY')}">
            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#updateModal">
                <i class="glyphicon glyphicon-edit"></i> Edit
            </button>        
        </c:when>
        <c:otherwise>
            <button type="button" class="btn btn-default disabled">
                <i class="glyphicon glyphicon-edit"></i> Edit
            </button>  
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${permissions:canDoThis(site, 'CREATE_POST')}">
            <button type="button" class="btn btn-default" data-toggle="modal" data-target="#createCategoryPostModal">
                <i class="glyphicon glyphicon-plus"></i> Post
            </button>        
        </c:when>
        <c:otherwise>
            <button type="button" class="btn btn-default disabled">
                <i class="glyphicon glyphicon-plus"></i> Post
            </button>  
        </c:otherwise>
    </c:choose>
</p>


<div class="row">
    <div class="col-sm-8">
        <div class="latest">
            <h4>Latest posts using this category</h4>
            <c:choose>
                <c:when test="${category.getPostsSet().size() > 0}">
                    <div class="row">
                        <c:forEach var="post" items="${category.getLatestPosts()}" end="9">
                            <div class="col-sm-12 col-md-6">
                                <div class="thumbnail">
                                    <div class="caption">
                                        <h5><a href="${post.editUrl}">${post.name.content}</a></h3>
                                        <p><small>${post.creationDate.toString('dd MMMM yyyy, HH:mm', locale)}</small></p>
                                        <span class="label label-default">${post.createdBy.name}</small>

                                        <div class="btn-group pull-right">
                                            <a href="${post.editUrl}" class="btn btn-icon btn-primary pull-right">
                                                <i class="glyphicon glyphicon-cog"></i>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <p>
                        <a href="${pageContext.request.contextPath}/cms/posts/${category.site.slug}?category=${category.slug}" class="btn btn-xs btn-default">View all</a>
                    </p>

                </c:when>
                <c:otherwise>
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <i>There are no posts using this category.</i>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <div class="col-sm-4">
        <div class="panel panel-primary">
          <div class="panel-heading">Details</div>
          <div class="panel-body">
            <dl class="dl-horizontal">
                <dt>Name</dt>
                <dd>${category.name.content}</dd>

                <dt>Slug</dt>
                <dd>${category.slug}</dd>

                <dt>Usages</dt>
                <dd>${category.getPostsSet().size()}</dd>

            </dl>
          </div>
        </div>
        <c:if test="${permissions:canDoThis(site, 'DELETE_CATEGORY') && (!category.privileged || permissions:canDoThis(site, 'EDIT_PRIVILEGED_CATEGORY'))}">
            <div class="panel panel-danger">
              <div class="panel-heading">Danger Zone</div>
              <div class="panel-body">
                <p class="help-block">Once you delete a category, there is no going back. Please be certain.</p>
                <button data-toggle="modal" data-target="#deleteModal" class="btn btn-danger">Delete this Category</button>
              </div>
            </div>
        </c:if>
    </div>
</div>

<c:if test="${permissions:canDoThis(site, 'DELETE_CATEGORY') && (!category.privileged || permissions:canDoThis(site, 'EDIT_PRIVILEGED_CATEGORY'))}">
    <div class="modal fade" id="deleteModal">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title">Are you sure?</h4>
          </div>
          <div class="modal-body">
            <p>You are about to delete the category '<c:out value="${category.name.content}" />'. There is no way to rollback this operation. Are you sure? </p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
            <button type="button" onclick="$('#deleteForm').submit();" class="btn btn-danger">Yes</button>
            <form action="${pageContext.request.contextPath}/cms/categories/${site.slug}/${category.slug}/delete" method="post" id="deleteForm">${csrf.field()}</form> 
          </div>
        </div>
      </div>
    </div>
</c:if>

<div class="modal fade" id="updateModal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form method="post" class="form-horizontal" role="form">

                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h3 class="modal-title">Edit Category</h3>
                    <small>Change information about category '${category.name.content}'</small>
                </div>

                <div class="modal-body">
                   ${csrf.field()}
                    <div class="form-group">
                        <label class="col-sm-2 control-label">Name</label>
                        <div class="col-sm-10">
                            <input bennu-localized-string required-any name="name" placeholder="<spring:message code="post.edit.label.name" />" value='<c:out value="${category.name.json()}"/>'>
                        </div>
                    </div>
                    <c:if test="${permissions:canDoThis(site, 'EDIT_PRIVILEGED_CATEGORY')}">
                        <div class="form-group">
                            <input type="checkbox" ${category.privileged ? 'checked' : ''} name="privileged" id="success">
                            <label for="success">Privileged</label>
                        </div>
                    </c:if>
                </div>

                <div class="modal-footer">
                    <button type="reset" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save</button>
                </div>

            </form>
        </div>
    </div>
</div>

<c:if test="${permissions:canDoThis(site, 'CREATE_POST')}">
    <div class="modal fade" id="createCategoryPostModal">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <form method="post" class="form-horizontal" role="form" action="${pageContext.request.contextPath}/cms/categories/${site.slug}/${category.slug}/createCategoryPost">

                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h3 class="modal-title">Create Post</h3>
                        <small>Creating a post with category '${category.name.content}'</small>
                    </div>

                    <div class="modal-body">
                       ${csrf.field()}
                        <div class="form-group">
                            <label class="col-sm-2 control-label">Name</label>
                            <div class="col-sm-10">
                                <input bennu-localized-string required-any name="name" placeholder="<spring:message code="post.edit.label.name" />" />
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
    </div>
</c:if>

<style type="text/css">
    .latest {
        height: 300px;
    }
</style>