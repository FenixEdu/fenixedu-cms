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
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://fenixedu.com/cms/permissions" prefix="permissions" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/jquery.jsonview.css"/>
<script src="${pageContext.request.contextPath}/static/js/jquery.jsonview.js"></script>

${portal.angularToolkit()}

<script src="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/jquery-ui.min.js"></script>
<link href="${pageContext.request.contextPath}/static/css/skin-awesome/ui.fancytree.css" rel="stylesheet" type="text/css">
<script src="${pageContext.request.contextPath}/static/js/jquery.fancytree-all.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/jquery.js" type="text/javascript"></script>

<script src="${pageContext.request.contextPath}/static/js/fancytree-directive.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/js/ng-file-upload-shim.js" type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/static/js/ng-file-upload.js" type="text/javascript" charset="utf-8"></script>

<div class="page-header">
    <h1>${post.name.content}
        <small>
      		<ol class="breadcrumb">
                <li><a href="${pageContext.request.contextPath}/cms/sites">Content Management</a></li>
                <li><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></li>
                <li><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}/posts">Posts</a></li>
            </ol>
        </small>
    </h1>
</div>

<div ng-app="editPostApp" ng-controller="PostCtrl">


		<div class="row">
			<div class="col-sm-12">
				<button ng-class="{disabled: !post.name}" type="button" class="btn btn-primary" ng-click="update()">
					<span class="glyphicon glyphicon-floppy-disk"></span> Update
				</button>
	            <button type="button" class="btn btn-default disabled">
            		<span class="glyphicon glyphicon-edit"></span> Edit
			    </button>
				<a href="${pageContext.request.contextPath}/cms/versions/${site.slug}/${post.slug}" class="btn btn-default">
					<span class="glyphicon glyphicon-time"></span> Versions
				</a>
				<c:if test="${permissions:canDoThis(site, 'SEE_METADATA')}">
					<button ng-class="{disabled: !post.metadata}" class="btn btn-default" data-toggle="modal" data-target="#viewMetadata">
						<span class="glyphicon glyphicon-cog"></span> Metadata
					</button>
				</c:if>
				<a ng-class="{disabled: !post.active || !post.address}" target="_blank" href="{{ post.address }}" class="btn btn-default">
					<span class="glyphicon glyphicon-link"></span> Link
				</a>
			</div>
		</div>
	<fieldset>

		<!-- NAME -->
		<div class="form-group">
			<input bennu-localized-string="post.name" required-any placeholder="<spring:message code="post.edit.label.name" />">
			<p class="text-danger" ng-show="!post.name"><spring:message code="post.edit.error.emptyName"/></p>
		</div>
		
		<!-- BODY -->
	    <div class="form-group">
    		<textarea bennu-localized-html-editor="post.body" on-image-added="onImageAdded"></textarea>
	    </div>

		<!-- PUBLISHED -->
		<c:if test="${permissions:canDoThis(site, 'PUBLISH_POSTS')}">
		    <div class="panel panel-default">
		        <div class="panel-heading">Publish</div>
		        <div class="panel-body">
		            <dl class="dl-horizontal">
		                <dt>Published</dt>
		                <dd>
                            <div class="switch switch-success">
                                <input type="checkbox" ng-model="post.active" id="success">
                                <label for="success">Privileged</label>
                            </div>
		                </dd>
		                <dt>Publication Begin</dt>
		                <dd><input type="text" class="form-control" bennu-date-time="post.publicationBegin"></dd>
		                <dt>Publication End</dt>
		                <dd><input type="text" class="form-control" bennu-date-time="post.publicationEnd"></dd>
		                <c:if test="${permissions:canDoThis(site, 'CHANGE_OWNERSHIP_POST')}">
			                <dt>Author</dt>
			                <dd><input type="text" class="form-control" bennu-user-autocomplete="post.createdBy" /></dd>
		                </c:if>
	     	            <dt>Access Control</dt>
		                <dd><input type="text" class="form-control" bennu-group="post.canViewGroup" allow="public,users" /></dd>
		            </dl>
		        </div>
		    </div>
	    </c:if>

		<!-- CATEGORIES -->
        <c:if test="${permissions:canDoThis(site, 'LIST_CATEGORIES,EDIT_CATEGORY')}">
			<div class="panel panel-default">
				<div class="panel-heading"><spring:message code="site.manage.label.categories"/></div>
				<div class="panel-body">
			
	                <p>
	                    <c:choose>
	                        <c:when test="${permissions:canDoThis(site, 'CREATE_CATEGORY')}">
	                            <button type="button" data-toggle="modal" data-target="#addCategory" class="btn btn-default btn-xs">
	                                <i class="icon icon-plus"></i> Create Category
	                            </button>
	                        </c:when>
	                        <c:otherwise>
	                            <button type="button" class="btn btn-default btn-xs disabled">
	                                <i class="icon icon-plus"></i> Create Category
	                            </button>
	                        </c:otherwise>
	                    </c:choose>
	                </p>

					<div ng-show="post.categories && post.categories.length">
						<div class="checkbox" class="col-sm-4" ng-repeat="category in post.categories">
							<label><input type="checkbox" ng-model="category.use" /> {{category.name | i18n }}</label>
						</div>
					</div>

					<div ng-hide="post.categories && post.categories.length">
						<i>Post has no categories.</i>
					</div>
				</div>
			</div>
		</c:if>

		<!-- FILES -->
		<div class="panel panel-default">
			<div class="panel-heading">Files</div>
			<div class="panel-body">

				<p>
					<a class="btn btn-default btn-xs" ngf-select ngf-change="upload($files)">
						<span class="glyphicon glyphicon-plus"></span> Add File
					</a>
				</p>
				
				<div ng-show="post && post.files && post.files.length">
					<table class="table table-striped table-bordered">
		                <thead>
		                    <tr>
		                        <th class="col-md-1">#</th>
		                        <th class="col-md-7"><spring:message code="theme.view.label.name"/></th>
		                        <th class="col-md-2"><spring:message code="theme.view.label.type"/></th>
		                        <th class="col-md-2">&nbsp;</th>
		                    </tr>
		                </thead>

						<tbody>
							<tr ng-repeat="file in post.files | orderBy: index">
								<td><h5>{{file.index}}</h5></td>
								<td>
									<h5>
										<a href="{{file.editUrl}}">{{file.displayName}}&nbsp;</a>
										<span ng-show="file.isEmbedded" class="label label-default">Embedded</span>
										<span ng-hide="file.isEmbedded" class="label label-info">Attachment</span>
									</h5>
								</td>
								<td>
									<code ng-show="file.contentType" title="{{file.contentType}}" data-toggle="tooltip" tooltip>
										{{file.contentType | limitTo: 20}}{{file.contentType.length > 20 ? '...' : ''}}
									</code>
								</td>
								<td>
									<a href="{{file.editUrl}}" class="btn btn-default btn-sm" data-toggle="tooltip" title="Edit File" tooltip>
										<i class="glyphicon glyphicon-edit"></i>
									</a>
									<div class="btn-group">
										<button type="button" class="btn btn-default btn-sm" ng-class="{disabled: file.index == post.files.length - 1}" data-toggle="tooltip" title="Move file downwards" ng-click="updatePosition(file, +1)" tooltip>
											<span class="glyphicon glyphicon-chevron-down"></span>
										</button>

										<button type="button" class="btn btn-default btn-sm" ng-class="{disabled: file.index == 0}" data-toggle="tooltip" title="Move file upwards" ng-click="updatePosition(file, -1)" tooltip>
											<span class="glyphicon glyphicon-chevron-up"></span>
										</button>
									</div>
								</td>
							</tr>
						</tbody>
					</table>
				</div>

				<i ng-hide="post.files && post.files.length">Post has no files.</i>

			</div>
		</div>

	</fieldset>

	<!-- ADD ATTACHMENT MODAL -->
	<div class="modal fade" id="addAttachment" tabindex="-1" role="dialog" aria-hidden="true">
		<fieldset class="form-horizontal">

			<div class="modal-dialog modal-lg">
				<div class="modal-content">

					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span>
							<span class="sr-only">Close</span>
						</button>
						<h4><spring:message code="action.new"/></h4>
						<small>Please choose the name that should be used to present the new file.</small>
					</div>

					<div class="modal-body">
						<div class="form-group">
							<label class="col-sm-2 control-label">Name:</label>
							<div class="col-sm-10"><input type="text" class="form-control" required-any ng-model="newFile.name"></div>
						</div>

						<div class="form-group" ng-show="newFile.attachment.type">
							<label class="col-sm-2 control-label">Type:</label>
							<div class="col-sm-10"><pre>{{ newFile.attachment.type | limitTo: 20 }}</pre></div>
						</div>

						<div class="form-group" ng-show="newFile.attachment.size">
							<label class="col-sm-2 control-label">Size:</label>
							<div class="col-sm-10"><pre>{{ newFile.attachment.size }} bytes</pre></div>
						</div>
					</div>

					<div class="modal-footer">
						<button type="reset" class="btn btn-default" data-dismiss="modal">
							Cancel
						</button>
						<button type="submit" class="btn btn-primary" ng-click="createAttachment()" data-dismiss="modal">
							<spring:message code="label.make"/>
						</button>
					</div>
				</div>
			</div>
		</fieldset>
	</div>

    <c:if test="${permissions:canDoThis(site, 'CREATE_CATEGORY')}">
		<!-- CREATE CATEGORY MODAL -->
		<div class="modal fade" id="addCategory" tabindex="-1" role="dialog" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
		            <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true"> </button>
		                <h3 class="modal-title">New Category</h3>
		                <small>Please specify the name for the new category</small>
		            </div>
						
						<div class="modal-body">
							<div class="form-group">
								<label class="col-sm-2 control-label"><spring:message code="categories.create.label.name"/></label>
								<div class="col-sm-10">
									<input bennu-localized-string="newCategory.name" required-any />
									<p ng-show="!newCategory.name" class="text-danger"><spring:message code="categories.create.error.emptyName"/></p>
								</div>
							</div>
			                <div class="form-group">
			                    <label class="col-sm-2 control-label">Slug</label>

			                    <div class="col-sm-10">
			                        <input name="type" class="form-control" type="text" id="category-slug" readonly="true" value="{{ newCategory.slug }}">
			                        <p class="help-block">This code is used internally and is not shared with the users. However it must be unique.</p>
			                    </div>
			                </div>
						</div>

						<div class="modal-footer">
							<button type="reset" class="btn btn-default" data-dismiss="modal">
								Cancel
							</button>
							<button type="button" class="btn btn-primary" ng-click="createCategory()" data-dismiss="modal">
								<spring:message code="label.make"/>
							</button>
						</div>
				</div>
			</div>
		</div>
	</c:if>

	<c:if test="${permissions:canDoThis(site, 'SEE_METADATA')}">
		<div class="modal fade" id="viewMetadata" tabindex="-1" role="dialog" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">

					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4>Metadata</h4>
					</div>
				
					<div class="modal-body">
						<div class="clearfix">
							<div class="form-group">
								<div class="col-sm-12">
									<c:if test="${permissions:canDoThis(site, 'EDIT_METADATA')}">
										<p>
											<a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/${post.slug}/metadata" class="btn btn-default">
			                					<span class="glyphicon glyphicon-edit"></span> Edit
			            					</a>
		            					</p>
	            					</c:if>
									<pre ng-show="{{Object.keys(post.metadata).length}}">{{ post.metadata }}</pre>
									<p ng-hide="{{Object.keys(post.metadata).length}}">There is no metadata for this post.</p>
								</div>
							</div>
						</div>
					</div>
				
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					</div>

				</div>
			</div>
		</div>
	</c:if>
</div>
<style type="text/css">
	.json-data {
		height: 400px;
		overflow: scroll;
		border: 1px solid #ddd;
		padding: 20px;
		margin-bottom: 20px;
		border-radius: 3px;
	}
</style>

<script type="application/javascript">
	var updatePostUrl = 'edit';
    var postDataUrl = 'data';
    var createPostFilesUrl = 'files';

    angular.module('editPostApp', ['bennuToolkit', 'fancyTreeDirective', 'ngFileUpload'])
	    .config(['$httpProvider', function($httpProvider) {
	        $httpProvider.defaults.headers.common = $httpProvider.defaults.headers.common || {};
	        $httpProvider.defaults.headers.common['${csrf.headerName}'] = '${csrf.token}';
	    }])
        .controller('PostCtrl', ['$scope', '$http','Upload', function($scope, $http, Upload){
            $http.get("data").success(function(data){
	                $scope.post = data.post;
	                $scope.errors = undefined;
	            	$scope.newCategory = {};
	            	$scope.newFile = {};

	            	$scope.$watch('newCategory.name', function() {
						var name = $scope.newCategory.name && Bennu.localizedString.getContent($scope.newCategory.name);
						$scope.newCategory.slug = (name && slugify(name)) || "";
	            	});
	                
	                $scope.update = function() {
	                	$http.post(updatePostUrl, $scope.post).success(function(response) {
	                		$scope.post = response.post;
	                	}).error(function(response) {
	                		$scope.errors = response;
	                	});
	                };

					$(window).bind('keydown', function(event) {
					    if (event.ctrlKey || event.metaKey) {
					        switch (String.fromCharCode(event.which).toLowerCase()) {
					        case 's':
					            event.preventDefault();
					            $scope.update();
					            break;
					        }
					    }
					});

	                $scope.createAttachment = function() {
		                Upload.upload({
		                    url: createPostFilesUrl,
		                    fields: {'name': $scope.newFile.name, embedded: false},
		                    file: $scope.newFile.attachment
		                }).success(function (data, status, headers, config) {
		                	$scope.post.files.push(data);
		                	$scope.post.files = $scope.post.files.sort(function(pf1, pf2) { return pf1.index - pf2.index});
		                }).error(function (data, status, headers, config) {
		                    console.log('error uploading file: ', status);
		                });
	                };

	                $scope.createCategory = function() {
	                	$scope.newCategory.use = true;
	                	$scope.post.categories.push($scope.newCategory);
	                	$scope.newCategory = {};
	                };

	                $scope.upload = function (files) {
				        if (files && files.length) {
			                $scope.newFile.attachment = files[0];
			                $scope.newFile.name = files[0].name;
			                $('#addAttachment').modal('show');
			            }
				    };

				    $scope.updatePosition = function(postFile, offset) {
				    	var swapFiles = function(position1, position2) {
			    			var buffer = $scope.post.files[position1].index;
			    			$scope.post.files[position1].index = $scope.post.files[position2].index;
			    			$scope.post.files[position2].index = buffer;
			    			$scope.post.files = $scope.post.files.sort(function(pf1, pf2) { return pf1.index - pf2.index});
				    	};
			    		var currentPosition = $scope.post.files.indexOf(postFile);
			    		var newPosition = currentPosition + offset;
			    		if(currentPosition > -1 && newPosition > -1 && newPosition < $scope.post.files.length) { 
			    			swapFiles(currentPosition, newPosition); 
			    		}
				    };

	        		function slugify(text) {
					  return text.toString().toLowerCase().replace(/\s+/g, '-').replace(/[^\w\-]+/g, '').replace(/\-\-+/g, '-').replace(/^-+/, '').replace(/-+$/, '');
					};

					$scope.onImageAdded = function(files, cb) {
					    for(var i=0; i < files.length; ++i) {
					        Upload.upload({
					            url: createPostFilesUrl,
					            fields: {'name': files[i].name, embedded: true},
					            file: files[i]
					        }).success(function (data, status, headers, config) {
						    	if(data && data.url) { 
						    		cb([data.url]);
						    	}
			                	$scope.post.files.push(data);
	                			$scope.post.files = $scope.post.files.sort(function(pf1, pf2) { return pf1.index - pf2.index});
					        }).error(function (data, status, headers, config) {
					            console.log('error uploading file: ', status);
					        });
				    	}
					};
	    		}); 
        	}]).directive('jsonData', function() {
        		return {
		            restrict: 'A',
		            scope: {
						model: '=jsonData',
		            },
		            link: function(scope, el, attr) {
		                scope.$watch('model', function(value) {
		                	$(el).JSONView(value || {}, {collapsed: true});
		                });
		                $(el).change(function () {
		                    $timeout(function () {
		                        scope.model = $(el).val();
		                    });
		                });
					}
				};
        	});

</script>

