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

<script>
    // monkey patch until bennu 4
    Bennu = Bennu || {};

    Bennu.utils = Bennu.utils || {};
    Bennu.utils.hasAttr = function(obj,attr){
        var val = $(obj).attr(attr);
        return (typeof val !== typeof undefined && val !== false);
    }

    function dateOptions(e, options){
        if (Bennu.utils.hasAttr(e, "min-date")) {
            options.minDate = new Date(e.attr("min-date"));
        }

        if (Bennu.utils.hasAttr(e, "max-date")) {
            options.maxDate = new Date(e.attr("max-date"));
        }

        if (Bennu.utils.hasAttr(e, "requires-future")) {
            options.minDate = new moment();
        }

        if (Bennu.utils.hasAttr(e, "requires-past")) {
            options.maxDate = new moment();
        }

        if (Bennu.utils.hasAttr(e, "requires-future") && Bennu.utils.hasAttr(e, "requires-past")) {
            throw "Error: Due to limitations on the space-time continuum, choosing dates of past and future would break the causality principle."
        }

        if (Bennu.utils.hasAttr(e, "unavailable-dates")) {

            var dates = e.attr("unavailable-dates").split(",")
            var result = [];
            for (var i = 0; i < dates.length; i++) {
                result.add(new Date(dates[i]));
            }
            options.disabledDates = result;
        }

        if (Bennu.utils.hasAttr(e, "available-dates")) {
            var dates = e.attr("available-dates").split(",")
            var result = [];
            for (var i = 0; i < dates.length; i++) {
                result.add(new Date(dates[i]));
            }
            options.enabledDates = result;
        }

        return
    };

    function timeOptions(e, options){
        if (Bennu.utils.hasAttr(e,"minute-stepping")) {
            options.minuteStepping = parseInt(e.attr("minute-stepping"));
        }
    }

    Bennu.datetime = Bennu.datetime || {};

    Bennu.datetime.createDateTimeWidget = function (e) {
                e = $(e);
                var widget = $('<div class="bennu-datetime-input-group input-group date"><span class="input-group-addon">' +
                        '<span class="glyphicon glyphicon-calendar"></span></span><input data-date-format="DD/MM/YYYY HH:mm:ss" type="text" class="bennu-datetime-input form-control"/></div>');

                var currentDate = e.val();

                if (currentDate && currentDate.trim() != "") {
                    currentDate = new Date(currentDate);
                    e.val(moment(currentDate).format("YYYY-MM-DDTHH:mm:ss.SSSZ"));
                }

                var options = {
                    sideBySide: true,
                    language: Bennu.lang,
                    pickDate: true,
                    pickTime: true,
                    useSeconds: true,
                    showToday: true,
                };

                if(currentDate){
                    options['defaultDate'] = currentDate;
                }

                dateOptions(e,options);
                timeOptions(e,options);

                $("input", widget).on("change", function (x) {
                    x = $(x.target);
                    var value = x.val().trim()
                    if (value == ""){
                        if ("" !== e.val()){
                            e.val("");
                            e.trigger("change");
                        }
                    }else{
                        var r = moment(value, "DD/MM/YYYY HH:mm:ss").format("YYYY-MM-DDTHH:mm:ss.SSSZ");
                        if (r !== e.val()){
                            e.val(r);
                            e.trigger("change");
                        }
                    }
                }).datetimepicker(options);

                e.after(widget);
                e.data("input");
                widget.data("related", e);

                e.on("change.bennu", function(ev){
                    var data = $(e).val();

                    if (data.trim() == "") {
                        e.val("");
                        $(".bennu-datetime-input", widget).data("DateTimePicker").setDate("");
                    } else {
                        data = new Date(data);

                        e.val(moment(data).format("YYYY-MM-DDTHH:mm:ss.SSSZ"));
                        var r = $(".bennu-datetime-input", widget).data("DateTimePicker").getDate()
                        if (r) { r=r.format("DD/MM/YYYY HH:mm:ss"); }
                        var t = moment(data).format("DD/MM/YYYY HH:mm:ss");

                        if (r !== t){
                            $(".bennu-datetime-input", widget).data("DateTimePicker").setDate(t);
                        }

                    }

                    e.data("handler").trigger();
                });
                return Bennu.widgetHandler.makeFor(e);
            }
</script>
<script src="${pageContext.request.contextPath}/bennu-admin/fancytree/jquery-ui.min.js"></script>
<link href="${pageContext.request.contextPath}/static/css/skin-awesome/ui.fancytree.css" rel="stylesheet" type="text/css">
<script src="${pageContext.request.contextPath}/static/js/jquery.fancytree-all.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/js/bennu-angular.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/jquery.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/js/fancytree-directive.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/js/ng-file-upload-shim.js" type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/static/js/ng-file-upload.js" type="text/javascript" charset="utf-8"></script>

<div ng-app="editPostApp" ng-controller="PostCtrl">
    <div class="page-header">
        <h1>${site.name.content}</h1>

        <h2><small><a href="${pageContext.request.contextPath}/cms/pages/${site.slug}">Edit Page - {{post.name | i18n }}</a></small></h2>

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
    </div>

    <fieldset ng-show="post">
        <!-- NAME -->
        <div class="form-group">
            <input bennu-localized-string="post.name" required-any placeholder="<spring:message code="page.edit.label.name" />">
            <p class="text-danger" ng-show="!post.name"><spring:message code="page.edit.error.emptyName"/></p>
        </div>

        <!-- BODY -->
        <div class="form-group">
            <div class="panel-heading">Body</div>
            <textarea bennu-localized-html-editor="post.body" on-image-added="onImageAdded"></textarea>
        </div>
        <div class="form-group">
            <div class="panel-heading">Excerpt</div>
            <textarea bennu-localized-html-editor="post.excerpt" on-image-added="onImageAdded"></textarea>
        </div>

        <!-- PUBLISHED -->
        <c:if test="${permissions:canDoThis(site, 'PUBLISH_PAGES')}">
            <div class="panel panel-default">
                <div class="panel-heading">Publish</div>
                <div class="panel-body">
                    <dl class="dl-horizontal">
                        <dt>Published</dt>
                        <dd>
                            <div class="switch switch-success">
                                <input type="checkbox" ng-model="post.active" id="success">
                                <label for="success">Active</label>
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
                            <dd><input bennu-group="post.canViewGroup" allow="public,users,managers,custom" name="viewGroup" type="text"/></dd>
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
                                    <i class="glyphicon glyphicon-plus"></i> Create Category
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button type="button" class="btn btn-default btn-xs disabled">
                                    <i class="glyphicon glyphicon-plus"></i> Create Category
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
                        <i>Page has no categories.</i>
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
                                    <a href="{{file.editUrl}}" class="btn btn-default" data-toggle="tooltip" title="Edit File" tooltip>
                                        <i class="glyphicon glyphicon-edit"></i>
                                    </a>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-default" ng-class="{disabled: file.index == post.files.length - 1}" data-toggle="tooltip" title="Move file downwards" ng-click="updatePosition(file, +1)" tooltip>
                                            <span class="glyphicon glyphicon-chevron-down"></span>
                                        </button>

                                        <button type="button" class="btn btn-default" ng-class="{disabled: file.index == 0}" data-toggle="tooltip" title="Move file upwards" ng-click="updatePosition(file, -1)" tooltip>
                                            <span class="glyphicon glyphicon-chevron-up"></span>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <i ng-hide="post.files && post.files.length">Page has no files.</i>

            </div>
        </div>

        <!-- MENUS -->
        <c:if test="${permissions:canDoThis(site, 'LIST_MENUS,EDIT_MENU')}">
            <div class="panel panel-default">
                <div class="panel-heading">Menu</div>
                <div class="panel-body">

                    <c:if test="${permissions:canDoThis(site, 'CREATE_MENU_ITEM')}">
                        <div class="switch switch-success" ng-show="menus && menus.length">
                              <input type="checkbox" checked name="toggle" id="use-menu" ng-model="useMenu">
                              <label for="use-menu">Show on menu</label>
                        </div>
                    </c:if>

                    <a href="${pageContext.request.contextPath}/cms/menus/${site.slug}" target="_blank" class="btn btn-default pull-right">
                        <span class="glyphicon glyphicon-cog"></span> Manage Menus
                    </a>

                    <p ng-hide="useMenu" class="text-info"><i>This page will not be shown on the menu.</i></p>
                    <p ng-show="useMenu" class="text-info"><i>Use drag and drop to change the position of this page on the menu.</i></p>
                    <div ng-show="useMenu"><fancy-tree items="menus" selected="selected" drag-only-one></fancy-tree></div>
                </div>
            </div>
        </c:if>

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
                                            <a href="${pageContext.request.contextPath}/cms/pages/${site.slug}/${page.slug}/metadata" class="btn btn-default">
                                                <span class="glyphicon glyphicon-edit"></span> Edit
                                            </a>
                                        </p>
                                    </c:if>
                                    <pre ng-show="{{Object.keys(post.metadata).length}}">{{ post.metadata }}</pre>
                                    <p ng-hide="{{Object.keys(post.metadata).length}}">There is no metadata for this page.</p>
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
    var createPostFilesUrl = '${pageContext.request.contextPath}/cms/posts/${site.slug}/${post.slug}/files';

    angular.module('editPostApp', ['bennuToolkit', 'fancyTreeDirective', 'ngFileUpload'])
        .controller('PostCtrl', ['$scope', '$http','Upload', '$timeout', function($scope, $http, Upload, $timeout){


            function findMenuItem() {
                function isMenuItemPagePredicate(menuItem) {
                    return menuItem && menuItem.use && menuItem.use == 'page' && menuItem.page && menuItem.page == $scope.post.slug;
                }
                return findMenus($scope.menus, isMenuItemPagePredicate);
            }

            function findMenuItemParent(mi) {
                function isMenuItemPageParentPredicate(menuItem) {
                    return menuItem && menuItem.children && menuItem.children.length && menuItem.children.indexOf(mi) !== -1;
                }
                return findMenus($scope.menus, isMenuItemPageParentPredicate);
            }

            function findMenus(menus, predicate) {

                function treeFind(node, predicate) {
                    if(predicate(node)) {
                        return node;
                    } else if(node.children && node.children.length) {
                        for(var i = 0; i < node.children.length; ++ i) {
                            var found = treeFind(node.children[i],predicate);
                            if(found) return found;
                        }
                    }
                }

                for(var i = 0; i < menus.length; ++i) {
                    var found = treeFind(menus[i], predicate);
                    if(found) return found;
                }
            }


            function init(data) {

                function initMenus() {
                    $scope.menus = data.menus;
                    $scope.post = data.post;
                    $scope.newCategory = {};
                    $scope.newFile = {};
                    addClassToSelectedMenuItem();
                    $scope.menuItem = findMenuItem();
                    $scope.useMenu =  $scope.menuItem !== undefined;
                    if ($scope.useMenu) {
                        $scope.menuItem.draggable = true;
                    }
                    $timeout(function(){
                        $scope.$watch('useMenu', function(useMenu) {
                            if(useMenu) {
                                createNewMenuItem();
                                $scope.menuItem = findMenuItem();
                                $scope.menuItem.draggable =true;
                            }
                        });
                    });

                    function addClassToSelectedMenuItem() {
                        var menuItem = findMenuItem();
                        if(menuItem) {
                            $scope.selectedMenuItem = menuItem;
                            $scope.selectedMenuItem.extraClasses = "page-item";
                        }
                    }

                    function createNewMenuItem() {
                        if(findMenuItem() === undefined) {
                            var firstMenu = $scope.menus[0];
                            if(!firstMenu.children) {
                                firstMenu.children = [];
                            }
                            firstMenu.children.unshift({
                                name: $scope.post.name,
                                title: Bennu.localizedString.getContent($scope.post.name),
                                use: 'page',
                                page: $scope.post.slug,
                                key: $scope.post.slug,
                                extraClasses: "page-item"
                            });
                        }
                    }

                };


                initMenus();
            }


            $http.get("data").success(function(data){
                init(data);

                $scope.$watch('newCategory.name', function() {
                    if($scope.newCategory && $scope.newCategory.name) {
                        var name = $scope.newCategory.name && Bennu.localizedString.getContent($scope.newCategory.name);
                        $scope.newCategory.slug = (name && slugify(name)) || "";
                    }
                });

                $scope.update = function() {
                    var menuItem = findMenuItem();
                    if(menuItem != null) {
                        if (typeof $scope.useMenu === 'boolean' && $scope.useMenu === false) {
                            menuItem.remove = true
                        } else {
                            var parent = findMenuItemParent(menuItem)
                            if (parent.root) {
                                menuItem.menuKey = parent.key;
                            }
                            menuItem.parentId = parent.key;
                        }
                    }

                    var data = {post: $scope.post, menuItem: menuItem};
                    $http.post(updatePostUrl, angular.toJson(data)).success(init);
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
                        for (var i = 0; i < $scope.post.files.size; i++) {
                            $scope.post.files[i].index = i;
                        }
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
                            data && data.url && cb([data.url]);
                            $scope.post.files.push(data);
                            $scope.post.files = $scope.post.files.sort(function(pf1, pf2) { return pf1.index - pf2.index});
                        });
                    }
                };

            });
        }]);

</script>

<style type="text/css">
.ui-fancytree {
    outline: none;
    min-height: 300px !important;
}
.page-item {
    font-weight: bold;
}
</style>