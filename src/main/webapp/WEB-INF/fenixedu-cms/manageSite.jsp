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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
${portal.toolkit()}

<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.5/d3.js"></script>

<c:set var="locale" value="<%= org.fenixedu.commons.i18n.I18N.getLocale() %>"/>

	<div class="page-header">
	    <h1>${site.name.content}
	        <c:if test="${permissions:canDoThis(site, 'EDIT_SITE_INFORMATION')}">
	          	<button type="button" data-toggle="modal" data-target="#site-settings" class="btn btn-link"><i class="icon icon-tools"></i></button>
	        </c:if>
	        <small>
          		<ol class="breadcrumb">
                    <li><a href="${pageContext.request.contextPath}/cms/sites">Content Management</a></li>
                </ol>
	        </small>
	    </h1>
	</div>

	<div class="row">
		<div class="col-sm-6">
			<h3>At a glance</h3>
			
			<div class="input-group">
				<div class="input-group-btn">
					<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						New...
						<span class="caret"></span>
						<span class="sr-only">Toggle Dropdown</span>
					</button>
					<ul class="dropdown-menu">
						<c:if test="${permissions:canDoThis(site, 'CREATE_POST')}">
							<li><a href="${pageContext.request.contextPath}/cms/posts/${site.slug}#new">Post</a></li>
						</c:if>
						<c:if test="${permissions:canDoThis(site, 'CREATE_PAGE')}">
							<li><a href="${pageContext.request.contextPath}/cms/pages/${site.slug}#new">Page</a></li>
						</c:if>
						<c:if test="${permissions:canDoThis(site, 'CREATE_CATEGORY')}">
							<li><a href="${pageContext.request.contextPath}/cms/categories/${site.slug}#new">Category</a></li>
						</c:if>
						<c:if test="${permissions:canDoThis(site, 'CREATE_MENU')}">
							<li><a href="${pageContext.request.contextPath}/cms/menus/${site.slug}#new">Menu</a></li>
						</c:if>
					</ul>
				</div>
				<input type="text" class="form-control" placeholder="Search this website..." id="search-query" autofocus>
			</div>
		
			<br />
			
			<ul class="list-group">
				<c:if test="${permissions:canDoThis(site, 'EDIT_POSTS')}">
					<li class="list-group-item"><a href="${pageContext.request.contextPath}/cms/posts/${site.slug}">Posts<span class="badge pull-right">${site.nonStaticPostsStream.count()}</span></a></li>
				</c:if>
				<c:if test="${permissions:canDoThis(site, 'SEE_PAGES,EDIT_PAGE')}">
					<li class="list-group-item"><a href="${pageContext.request.contextPath}/cms/pages/${site.slug}">Pages<span class="badge pull-right">${site.pagesSet.size()}</span></a></li>
				</c:if>
				<li class="list-group-item"><a href="${pageContext.request.contextPath}/cms/media/${site.slug}">Media<span class="badge pull-right">${site.filesSet.size()}</span></a></li>
				<c:if test="${permissions:canDoThis(site, 'LIST_CATEGORIES')}">
					<li class="list-group-item"><a href="${pageContext.request.contextPath}/cms/categories/${site.slug}">Categories<span class="badge pull-right">${site.categoriesSet.size()}</span></a></li>
				</c:if>
				<c:if test="${permissions:canDoThis(site, 'LIST_MENUS')}">
					<li class="list-group-item"><a href="${pageContext.request.contextPath}/cms/menus/${site.slug}">Menus<span class="badge pull-right">${site.menusSet.size()}</span></a></li>
				</c:if>
			</ul>

			<h3>Properties</h3>
            <dl class="dl-entity-horizontal">
                <dt>Theme</dt>
            	<c:choose>
            		<c:when test="${site.theme != null && cmsSettings.canManageThemes()}"><dd><a href="${pageContext.request.contextPath}/cms/themes/${site.theme.type}/see">${site.theme.name}</a></dd></c:when>
            		<c:when test="${site.theme != null && !cmsSettings.canManageThemes()}"><dd>${site.theme.name}</dd></c:when>
            		<c:otherwise><dd><span class="label label-warning">None</span></dd></c:otherwise>
            	</c:choose>

                <dt>Visibility</dt>
				<dd>${site.canViewGroup}</dd>

                <dt>Published</dt>
                <dd>
                    <c:if test="${site.published}">
                        <i class="icon icon-check"></i>
                    </c:if>
                </dd>

                <dt>Homepage</dt>
            	<c:choose>
            		<c:when test="${site.initialPage != null}"><dd><a href="${site.initialPage.editUrl}">${site.initialPage.name.content}</a></dd></c:when>
            		<c:otherwise><dd><span class="label label-warning">None</span></dd></c:otherwise>
            	</c:choose>

                <dt>Author</dt>
                <dd>${site.createdBy.displayName}</dd>
                <dt>Created</dt>
                <dd>${cms.prettyDate(site.creationDate)}</dd>
            </dl>

		</div>
		<div class="col-sm-6">
    		<div class="graph" style="display: none;">
				<h3>Analytics</h3>

				<svg id="visualisation" width="100%" height="255">
					
					<defs>
					  <pattern id="pattern1" x="0" y="0" width="49" height="49" patternUnits="userSpaceOnUse" >
					      <rect x="0" y="0" width="50" height="50" style="fill:white;stroke-width:2;stroke:#f3f3f3;"/>
					  </pattern>
					</defs>

					<rect x="0" y="0" width="100%" height="450" style=" fill: url(#pattern1);" />    
				</svg>
			</div>
			<h3>Activity</h3>
			<c:set var="activities" value="${site.lastFiveDaysOfActivity}"/>
		
			<c:choose>
			    <c:when test="${activities.size() == 0}">
				    <div class="panel panel-default">
			          <div class="panel-body">
			            <spring:message code="site.manage.label.emptySites"/>
			          </div>
			        </div>
			    </c:when>

			    <c:otherwise>
				    <ul class="events">
				    	<c:forEach var="activity" items="${activities}">
				    		<c:forEach var="item" items="${activity.items}">
								<li>${item.getRender()}<time class="pull-right">${cms.prettyDate(item.eventDate)}</time></li>
							</c:forEach>
						</c:forEach>
					</ul>
				</c:otherwise>
		    </c:choose>
		</div>

		<c:if test="${permissions:canDoThis(site, 'EDIT_SITE_INFORMATION')}">

			<div class="modal fade" id="site-settings" tabindex="-1" role="dialog" aria-hidden="true">
				<div class="modal-dialog">
					<form class="form-horizontal" action="${pageContext.request.contextPath}/cms/sites/${site.slug}/edit" method="post" role="form">
					    ${csrf.field()}
					    <div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal">
									<span aria-hidden="true">&times;</span>
									<span class="sr-only">Close</span>
								</button>
								<h4>Settings</h4>
								<small>Customize your website</small>
							</div>
					    	<div class="modal-body">
							    <div role="tabpanel">
							        <p>
							            <ul class="nav nav-tabs" role="tablist">
							                <li role="presentation" class="active"><a href="#settings" aria-controls="settings" role="tab" data-toggle="tab">General</a></li>
							                <c:if test="${permissions:canDoThis(site, 'MANAGE_ROLES')}">
							                	<li role="presentation"><a href="#roles" aria-controls="roles" role="tab" data-toggle="tab">Roles</a></li>
							                </c:if>
							                <c:if test="${permissions:canDoThis(site, 'MANAGE_ANALYTICS')}">
							                    <li role="presentation"><a href="#connections" aria-controls="connections" role="tab" data-toggle="tab">Connections</a></li>
							                </c:if>
							            </ul>
							        </p>
							    </div>
							    <div class="tab-content">
							        <div role="tabpanel" class="tab-pane active form-horizontal" id="settings">
				                        <div class="${emptyName ? "form-group has-error" : "form-group"}">
				                            <label for="site-name" class="col-sm-2 control-label"><spring:message code="site.edit.label.name"/></label>

				                            <div class="col-sm-10">
				                                <input bennu-localized-string required-any type="text" name="name" class="form-control"
				                                    id="site-name" placeholder="<spring:message code="site.edit.label.name" />" value='${site.name.json()}' \>
				                                <c:if test="${emptyName != null}">
				                                    <p class="text-danger"><spring:message code="site.edit.error.emptyName"/></p>
				                                </c:if>
				                            </div>
				                        </div>

				                        <div class="form-group">
				                            <label for="site-description" class="col-sm-2 control-label"><spring:message code="site.edit.label.description"/></label>

				                            <div class="col-sm-10">
				                                <input id="site-description" bennu-localized-string name="description" class="form-control" value='${site.description.json()}' \>
				                            </div>
				                        </div>

				                        <div class="form-group">
				                            <label for="theme" class="col-sm-2 control-label"><spring:message code="site.edit.label.theme"/></label>

				                            <div class="col-sm-10">
				                                <select name="theme" id="theme" class="form-control" ${permissions:canDoThis(site, 'CHANGE_THEME') ? '' : 'disabled'}>
				                                    <c:forEach var="theme" items="${themes}">
				                                        <option value="${theme.type}" ${theme == site.theme ? 'selected' : ''}>${theme.name}</option>
				                                    </c:forEach>
				                                </select>
				                            </div>
				                        </div>

				                        <div class="form-group">
				                            <label for="folder" class="col-sm-2 control-label">Tag</label>

				                            <div class="col-sm-10">
				                                <select name="folder" id="" class="form-control" ${permissions:canDoThis(site, 'CHOOSE_PATH_AND_FOLDER') ? '' : 'disabled'}>
				                                    <option value ${site.folder == null ? 'selected': ''}>--</option>

				                                    <c:forEach items="${folders}" var="folder">
				                                        <option value="${folder.externalId}" ${site.folder == folder ? 'selected': ''}>${folder.functionality.description.content}</option>
				                                    </c:forEach>
				                                </select>
				                            </div>
				                        </div>

				                        <div class="${emptyName ? "form-group has-error" : "form-group"}">
				                            <label class="col-sm-2 control-label"><spring:message code="site.edit.label.slug"/></label>

				                            <div class="col-sm-10">
				                                <div class="input-group">
				                                    <span class="input-group-addon"><code>${site.folder == null ? '' : site.folder.functionality.fullPath}/</code></span>
				                                    <input required type="text" name="newSlug" class="form-control" placeholder="<spring:message code="site.edit.label.slug" />" value='${site.slug}' ${permissions:canDoThis(site, 'CHOOSE_PATH_AND_FOLDER') ? '' : 'disabled'} \>
				                                </div>
				                            </div>
				                        </div>

				                        <div class="form-group">
				                            <label class="col-sm-2 control-label">Homepage:</label>

				                            <div class="col-sm-10">
				                                <select name="initialPageSlug" class="form-control" ${permissions:canDoThis(site, 'CHOOSE_DEFAULT_PAGE') ? '' : 'disabled'}>
				                                    <option value="---null---">-</option>
				                                    <c:forEach var="p" items="${site.pages}">
				                                        <option ${p == site.initialPage ? 'selected' : ''} value="${p.slug}">${p.name.content}</option>
				                                    </c:forEach>
				                                </select>
				                            </div>
				                        </div>

				                        <div class="form-group">
				                            <label class="col-sm-2 control-label">Visibility</label>

				                            <div class="col-sm-10">
				                                <input bennu-group name="viewGroup" type="text" value='${site.getCanViewGroup().getExpression()}'/>
				                            </div>
				                        </div>

				                        <div class="form-group">
				                            <label class="col-sm-2 control-label"><spring:message code="site.create.label.published"/></label>

				                            <div class="col-sm-10">
					                            <c:choose>
			                                        <c:when test="${permissions:canDoThis(site, 'PUBLISH_SITE')}">
	                                                    <div class="switch switch-success">
			                                            	<input id="published" name="published" type="checkbox" value="true" ${site.published ? "checked='checked'" : ""}>
															<label for="published">Published</label>
											            </div>
			                                        </c:when>
			                                        <c:when test="${site.published}">
			                                            <span class="label label-success">Published</span>
			                                        </c:when>
			                                        <c:otherwise>
			                                            <span class="label label-default">NOT Published</span>
			                                        </c:otherwise>
			                                    </c:choose>
				                            </div>
				                        </div>
							        </div>

						            <c:if test="${permissions:canDoThis(site, 'MANAGE_ROLES')}">
								        <div role="tabpanel" class="tab-pane form-horizontal" id="roles">
								            <ul class="list-group">
							                	<table class="table">
							                		<thead><tr><th colspan="2">Role Name</th></tr></theader>
							                		<tbody>
										                <c:forEach var="role" items="${site.roles}">
								                			<tr>
								                				<td>
									                				<a href="${pageContext.request.contextPath}/cms/sites/${site.slug}/roles/${role.externalId}/edit">
										                                ${role.name.content}
										                            </a>
									                            </td>
									                            <td>
									                            	<div class="pull-right">
										                            	<button type="button" class="btn btn-default btn-xs add-user-btn" data-role-id="${role.externalId}" data-role-name="${role.name.content}" data-role-group='${role.getGroup().toGroup().getExpression()}'>Add user</button>
							                            	            <div class="dropdown">
																			<a class="dropdown-toggle" href="#" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
																				<span class="glyphicon glyphicon-option-vertical"></span>
																			</a>
																			<ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenu1">
																				<li><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}/roles/${role.externalId}/edit">View</a></li>
																				<c:if test="${cmsSettings.canManageRoles()}">
																					<li><a href="#delete-role-modal" data-target="#delete-role-modal" data-toggle="modal" data-role-id="${role.externalId}">Remove</a></li>
																				</c:if>
																			</ul>
																		</div>
																	</div>
									                            </td>
								                			</tr>
										                </c:forEach>
							                		</tbody>
							                	</table>
								            </ul>
								        </div>

							        </c:if>

							        <c:if test="${permissions:canDoThis(site, 'MANAGE_ANALYTICS')}">
							            <div role="tabpanel" class="tab-pane form-horizontal" id="connections">
							                <c:if test="${google.isConfigured()}">
							                    <h3>Google</h3>

							                    <c:if test="${googleUser == null}">
							                        <p class="help-block">To use <a href="http://www.google.com/analytics/">Analytics</a>, <a href="https://plus.google.com">Plus</a>, <a href="https://maps.google.com">Maps</a> and other integrationss from Google you need to connect your site with your Google account.</p>
							                        <a class="btn btn-primary" href="${google.getAuthenticationUrlForUser(user)}">Connect with Google</a>
							                    </c:if>

							                    <c:if test="${googleUser != null}">
							                        <div class="form-group">
							                            <label for="inputEmail3" class="col-sm-2 control-label">Analytics Property</label>
							                            <div class="col-sm-10">
							                                <span class="property"></span> <a class="btn btn-xs btn-default" data-toggle="modal" href='#select-property'>Select property</a>
							                                <p class="help-block">Integrate this site with Google Analytics so you can receive information about the number of visits and correlate them with your posting history. </p>
							                            </div>
							                        </div>

							                        <div class="modal fade" id="select-property">
							                            <div class="modal-dialog">
							                                <div class="modal-content">
							                                    <div class="modal-header">
							                                        <button type="button" class="close" data-dismiss="modal"><span class="sr-only">Close</span></button>
							                                        <h3 class="modal-title">Select Property</h3>
							                                        <small>Choose one of your properties</small>
							                                    </div>
							                                    <div class="modal-body">

							                                        <div class="form-group">
							                                                <label class="col-sm-2 control-label">Property</label>
							                                                <div class="col-sm-10">
							                                                    <div class="row">
							                                                        <div class="col-sm-6">
							                                                            <select name="accountId" class="form-control" value="${site.analyticsAccountId}">
							                                                                <option class="form-control" value="">---</option>
							                                                                <c:forEach var="account" items="${accounts}">
							                                                                    <option value="${account.id}" class="account" ${account.getId().equals(site.analyticsAccountId) ? 'selected' : ''} class="form-control">${account.name}</option>
							                                                                </c:forEach>
							                                                            </select>
							                                                        </div>
							                                                        <div class="col-sm-6">
							                                                            <select name="analyticsCode" class="form-control" value="${site.getAnalyticsCode()}">
							                                                                <option class="form-control" value="">---</option>
							                                                                <c:forEach var="account" items="${accounts}">
							                                                                    <c:forEach var="property" items="${account.properties}">
							                                                                        <option value="${property.id}" data-account="${account.id}" class="google-property" ${property.getId().equals(site.getAnalyticsCode()) ? 'selected' : ''} class="form-control">${property.name}</option>
							                                                                    </c:forEach>
							                                                                </c:forEach>
							                                                            </select>
							                                                        </div>

							                                                    </div>
							                                                    <p class="help-block">
							                                                        If you need to create a new property for this site, go to <a href="https://www.google.com/analytics">Google Analytics</a> create it there and then selected it here.
							                                                    </p>
							                                                </div>

							                                        </div>

							                                    </div>
							                                    <div class="modal-footer">
							                                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
							                                        <button type="submit" class="save btn btn-primary">Save changes</button>
							                                    </div>
							                                </div>
							                            </div>
							                        </div>
							                        <script src="https://apis.google.com/js/client.js?onload=authorize"></script>
							                    </c:if>
							                </c:if>
							            </div>
							        </c:if>
							    </div>
						    </div>
						    <div class="modal-footer">
								<div class="form-group">
							        <div class="col-sm-12">
							        	<button type="button" data-toggle="modal" data-target="#confirmDeleteModal" class="btn btn-danger pull-left">Delete</button>
							            <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.save"/></button>
							            <a href="${pageContext.request.contextPath}/cms/sites/${site.slug}" class="btn btn-default">
							            	<spring:message code="action.cancel"/>
						            	</a>
							        </div>
							    </div>    	
						    </div>
					    </div>
					    
					</form>

					<div class="modal fade" id="confirmDeleteModal" role="dialog" aria-hidden="true">
					    <form method="post" action="${pageContext.request.contextPath}/cms/sites/${site.slug}/delete">
					        ${csrf.field()}
					        <div class="modal-dialog">
					            <div class="modal-content">
					                <div class="modal-header">
					                    <button type="button" class="close" data-dismiss="modal"><span class="sr-only">Close</span></button>
					                    <h3 class="modal-title">Delete Site</h3>
					                    <small>Are you sure?</small>
					                </div>
					                <div class="modal-body">
					                    You are about to delete the site '<c:out value="${site.name.content}" />'. You will also be deleting all content, including ${site.postSet.size() } posts. There is no way to rollback this opeartion. Are you sure?
					                </div>
					                <div class="modal-footer">

					                    <button type="button" data-dismiss="modal" class="btn btn-default"><spring:message
					                            code="action.cancel"/></button>
					                    <button type="submit" class="btn btn-danger"><spring:message code="action.delete"/></button>
					                </div>
					            </div>
					        </div>
					    </form>
					</div>

					<c:if test="${cmsSettings.canManageRoles()}">
						<div class="modal fade" id="delete-role-modal">
						    <div class="modal-dialog">
						      <div class="modal-content">
						        <div class="modal-header">
						          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						          <h4 class="modal-title">Are you sure?</h4>
						        </div>
						        <div class="modal-body">
						          <p>There is no way to rollback this operation. Are you sure? </p>
						        </div>
						        <div class="modal-footer">
						          <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
						          <button type="button" onclick="$('#delete-role-form').submit();" class="btn btn-danger">Yes</button>
						          <form action="#" method="post" id="delete-role-form">${csrf.field()}</form> 
						        </div>
						      </div>
						    </div>
						</div>

						<div class="modal fade" id="edit-role-modal" tabindex="1">
						  <div class="modal-dialog">
						    <div class="modal-content">
						        <form id="edit-role-form" method="post" class="form-horizontal" role="form">

						            <div class="modal-header">
						                <button type="reset" class="close" aria-label="Close" onclick="$('#edit-role-modal').modal('hide');"><span aria-hidden="true">&times;</span></button>
						                <h3 class="modal-title">Edit role</h3>
						                <small>Change the members that have access to this role</small>
						            </div>
						            <div class="modal-body">
						               ${csrf.field()}           
						                <div class="form-group">
						                    <label class="col-sm-2 control-label">Members</label>
						                    <div class="col-sm-10">
						                    	<div id="edit-role-group"></div>
						                    </div>
						                </div>
						            </div>

						            <div class="modal-footer">
						                <button type="reset" class="btn btn-default" onclick="$('#edit-role-modal').modal('hide');" >Cancel</button>
						                <button type="submit" class="btn btn-primary">Save</button>
						            </div>

						        </form>

						    </div>
						</div>

					</c:if>
				</div>
			</div>

		</c:if>

	</div>
<style>
ul.events {
	display: table;
	list-style: none;
	margin-left: 0;
	position: relative;
	width: 100%;
}
ul.events:before {
	border-left: 1px solid #ddd;
	bottom: 5px;
	content: "";
	left: 10px;
	position: absolute;
	top: 12px;
}
ul.events li {
	clear: both;
	color: #888;
	position: relative;
}
ul.events li:before {
	background-color: #ddd;
	border: 1px solid #ddd;
	border-radius: 50%;
	box-shadow: inset 0 0 0 2pt #fff;
	content: "";
	display: block;
	height: 13px;
	left: -36px;
	margin-top: 5px;
	position: absolute;
	width: 13px;
}
ul.events li .avatar {
	display: table-cell;
	float: left;
	margin-top: 0;
	width: 30px;
}
ul.events li .avatar+p {
	display: table-cell;
	float: left;
	width: 70%;
}
ul.events li .avatar+p+time {
	display: table-cell;
	float: right;
	text-align: right;
	width: 22%;
}
ul.events li.expanded {
	margin-top: 15px;
}
ul.events li.expanded:before {
	background-color: #bbb;
	border: 1px solid #bbb;
	box-shadow: inset 0 0 0 2pt #fff;
	height: 30px;
	margin-left: -8px;
	margin-top: -2px;
	width: 30px;
}
ul.events li a {
	color: #444;
}
ul.events li time
{
	font-style: italic;
}
.avatar{ 
	margin-top:4px
}
.avatar img {
	width:22px;
	height:auto;
	border-radius:2px;
	margin-right:5px
}
</style>

<script type="application/javascript">
    $(document).ready(function(){
        if(window.location.hash) {
        	$('#site-settings').modal('show')
        	$("a[href='" + window.location.hash +"']").tab('show');
        }

        $('[data-target="#delete-role-modal"]').click(function(){
    		$('#delete-role-form').attr('action', "${pageContext.request.contextPath}/cms/sites/${site.slug}/roles/" + $(this).data("role-id") + "/delete");
        });

        $('.add-user-btn').click(function(){
        	var id = $(this).data('role-id');
        	var name = $(this).data('role-name');
        	var group = $(this).data('role-group');

        	var input = '<input bennu-group allow="nobody,custom" name="group" type="text" value="' + group + '" />';
        	$("#edit-role-form").attr("action", "${pageContext.request.contextPath}/cms/sites/${site.slug}/roles/" + id + "/change")
        	$('#edit-role-group').html(input);
        	$('#edit-role-modal').modal('show');
        })

        $('[name="accountId"]').change(function(){
            var accountId = $('[name="accountId"]').val();
            $('[name="analyticsCode"]').prop("disabled", !accountId)
            $('[name="analyticsCode"]').val("");
            $('.google-property')
                .each(function() {
                    if($(this).data('account') == accountId) {
                        $(this).show();
                    } else {
                        $(this).hide();
                    }
                });
        });

        function initGoogleAccounts() {
            var accountId = $('[name="accountId"]').val();
            $('[name="analyticsCode"]').prop("disabled", !accountId)
            $('.google-property')
                .each(function() {
                    if($(this).data('account') == accountId) {
                        $(this).show();
                    } else {
                        $(this).hide();
                    }
                });
        }
        
        initGoogleAccounts();

    });

	function loadAnalyticsGraph(db) {
		var listDb = []
		var i = 0;
		for (var x in db) {
			db[x].i = i++; 
			listDb.push(db[x]);
		};
		function genGraph(){
			$("path", $("#visualisation")).remove();
			$("circle", $("#visualisation")).remove();
		var vis = d3.select('#visualisation'),
		    WIDTH = $("#visualisation").width(),
		    HEIGHT = $("#visualisation").height(),
		    MARGINS = {
		      top: 20,
		      right: 20,
		      bottom: 20,
		      left: 20
		    },
		    xRange = d3.scale.linear().range([MARGINS.left, WIDTH - MARGINS.right]).domain([d3.min(listDb, function(d) {
		      return parseInt(d.i);
		    }), d3.max(listDb, function(d) {
		      return parseInt(d.i);
		    })]),
		    yRange = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([d3.min(listDb, function(d) {
		      return parseInt(d.pageviews);
		    }), d3.max(listDb, function(d) {
		      return parseInt(d.pageviews);
		    })]);


		var lineFunc = d3.svg.line()
		.x(function(d) {
			return xRange(parseInt(d.i));
		})
		.y(function(d) {
			return yRange(parseInt(d.pageviews));
		})
		.interpolate('cardinal');

		var lineFuncV = d3.svg.line()
		.x(function(d) {
			return xRange(parseInt(d.i));
		})
		.y(function(d) {
			return yRange(parseInt(d.visitors));
		})
		.interpolate('cardinal');

		var lineInitFunc = d3.svg.line()
		.x(function(d) {
			return xRange(parseInt(d.i));
		})
		.y(function(d) {
			return $("#visualisation").height();
		})
		.interpolate('cardinal');

		vis.append('svg:path')
		  .attr('d', lineInitFunc(listDb))
		  .attr('stroke', '#3399FF')
		  .attr('stroke-width', 2)
		  .attr('fill', 'none').transition().attr('d', lineFunc(listDb)).duration(1000).each("end",function(){
	  			vis.selectAll("foo").data(listDb).enter().append("svg:circle")
						.attr("stroke", "#3399FF")
	         		.attr("fill", function(d, i) { return "#3399FF" })
	         		.attr("cx", function(d, i) { return xRange(parseInt(d.i)); })
	         		.attr("cy", function(d, i) { return yRange(parseInt(d.pageviews)); })
	         		.attr("r", function(d, i) { return 3 });
		  });


		vis.append('svg:path')
		  .attr('d', lineInitFunc(listDb))
		  .attr('stroke', '#9AC338')
		  .attr('stroke-width', 2)
		  .attr('fill', 'none').transition().attr('d', lineFuncV(listDb)).duration(1000).each("end",function(){
	  			vis.selectAll("bar").data(listDb).enter().append("svg:circle")
						.attr("stroke", "#9AC338")
	         		.attr("fill", function(d, i) { return "#9AC338" })
	         		.attr("cx", function(d, i) { return xRange(parseInt(d.i)); })
	         		.attr("cy", function(d, i) { return yRange(parseInt(d.visitors)); })
	         		.attr("r", function(d, i) { return 3 });
		  });
		
		};
		genGraph();
		$( window ).resize(function() {
		  genGraph();
		});
	}

	$(document).ready(function() {
		$.get('${pageContext.request.contextPath}/cms/sites/${site.slug}/analytics').done(function(analyticsData) {
			if(analyticsData && !$.isEmptyObject(analyticsData) &&  !$.isEmptyObject(analyticsData.google)) {
				$('.graph').fadeIn();
				loadAnalyticsGraph(analyticsData.google)
			}
		}).error(function(err){
			$('.graph').hide();
		});

		$('#search-query').keypress(function (e) {
			if (e.which == 13) {
				var searchQuery = $('#search-query').val();
				if(searchQuery) {
					window.location.href = "${pageContext.request.contextPath}/cms/posts/${site.slug}?query=" + searchQuery;
				}
			}
		});
	});
</script>