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
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>

${portal.angularToolkit()}

<script src="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/jquery-ui.min.js"></script>
<link href="${pageContext.request.contextPath}/static/css/skin-awesome/ui.fancytree.css" rel="stylesheet" type="text/css">
<script src="${pageContext.request.contextPath}/static/js/jquery.fancytree-all.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/jquery.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/js/fancytree-directive.js" type="text/javascript"></script>
<%@taglib uri="http://fenixedu.com/cms/permissions" prefix="permissions" %>

<div class="page-header">
    <h1><spring:message code="menu.edit.title" /> ${menu.name.content}</h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/menus/${site.slug}">${site.name.content}</a></small></h2>
</div>

<div class="container" ng-app="menuApp">
    <div class="row" ng-controller="MenuCtrl">
        <div class="col-md-4 col-sm-12">
            <br />
            <fancy-tree items="menuItems" selected="selectedItem"></fancy-tree>
        </div>
        <div class="col-md-8 col-sm-12">
            <div class="btn-group">
                <button class="btn btn-primary btn-sm" ng-click="save()">
                    <span class="glyphicon glyphicon-floppy-disk"></span> <spring:message code="label.save"/>
                </button>
                <button class="btn btn-default btn-sm" ng-show="canCreateMenuItem" ng-click="create()">
                    <span class="glyphicon glyphicon-plus"></span> <spring:message code="action.create"/>
                </button>
                <button ng-class="{disabled: selectedItem.root}" class="btn btn-danger btn-sm" ng-click="delete()">
                    <span class="glyphicon glyphicon-trash"></span> <spring:message code="action.delete"/>
                </button>
            </div>

            <p class="text-danger">{{error}}</p>

            <fieldset>
                <h3>{{selectedItem.title}}</h3>

                <div class="form-group">
                    <label class="control-label"><spring:message code="menu.edit.label.name"/></label>
                    <input type="text" required="true" ng-localized-string="selectedItem.name" class="form-control" placeholder="<spring:message code="menu.edit.label.name"/>">
                    <p class="text-info" ng-class="{'text-danger': !selectedItem.name}">
                        Please enter the name that should be presented for this menu item.
                    </p>
                    <c:if test="${permissions:canDoThis(site, 'EDIT_PRIVILEGED_MENU,CREATE_PRIVILEGED_MENU,DELETE_PRIVILEGED_MENU')}">
                        <div class="switch switch-success" ng-show="selectedItem.root">
                            <input type="checkbox" checked name="toggle" id="success" ng-model="selectedItem.privileged">
                            <label for="success">Privileged</label>
                            <p class="text-info" ng-show="selectedItem.privileged">Only users with permission to Edit <strong>privileged menus</strong> will be able to use and edit this menu.</p>
                            <p class="text-info" ng-hide="selectedItem.privileged">All users with permissions to edit menus will be able to use and edit this menu.</p>
                        </div>

                    </c:if>
                </div>
                <div class="menuItemEdit" ng-hide="selectedItem.root">

                    <!-- MENU ITEM IS AN URL -->
                    <div class="radio form-group">
                        <label>
                            <input type="radio" ng-model="selectedItem.use" value="url">
                            <spring:message code="menu.edit.label.url"/>
                        </label>
                        <div ng-show="selectedItem.use=='url'">
                            <input type="text" class="form-control" ng-model="selectedItem.url" placeholder="http://www.google.com">
                            <p class="text-danger" ng-hide="selectedItem.url">Please enter a valid URL.</p>
                        </div>
                        <p class="text-info">Link this menu item with an external URL.</p>
                    </div>

                    <!-- MENU ITEM IS A LINK TO A PAGE -->
                    <div class="radio form-group">
                        <label>
                            <input type="radio" ng-model="selectedItem.use" value="page">
                            <spring:message code="menu.edit.label.linkToPage"/>
                        </label>

                        <div ng-show="selectedItem.use=='page'">
                            <div class="row">
                                <div class="col-sm-6">
                                    <select class="form-control" ng-model="selectedItem.page" ng-options="page.slug as page.name | i18n for page in toArray(pages) | filter: searchText">
                                            <option value="">Select Page</option>
                                    </select>
                                </div>

                                <div class="col-sm-4">
                                    <input type="text" ng-model="searchText" class="form-control" placeholder="Search Page">
                                </div>

                                <div class="col-sm-2">
                                    <a class="btn btn-default" ng-class="{disabled: !selectedItem.page || !pages[selectedItem.page]}" ng-href="{{pages[selectedItem.page].editUrl}}">
                                        <i class="glyphicon glyphicon-link"></i>
                                    </a>
                                </div>
                            </div>
                            <p class="text-danger" ng-hide="selectedItem.page">Please select a page.</p>
                        </div>
                        <p class="text-info">Link this menu item with a given page of this site.</p>

                        <!-- MENU ITEM IS A FOLDER -->
                        <div class="radio">
                            <label>
                                <input type="radio" ng-model="selectedItem.use" value="folder">
                                <spring:message code="menu.edit.label.folder"/>
                            </label>
                            <p class="text-info"><spring:message code="menu.edit.label.folderDescription"/></p>
                        </div>

                    </div>
                </div>
            </fieldset>
        </div>
    </div>
</div>

<script type="text/javascript">
    var context = "${pageContext.request.contextPath}/cms/menus/${site.slug}/${menu.slug}";
    var menuDataUrl = context + "/data";
    var saveMenuUrl = context + "/edit";

    angular.module('menuApp', ['bennuToolkit', 'fancyTreeDirective'])
        .config(['$httpProvider', function($httpProvider) {
            $httpProvider.defaults.headers.common = $httpProvider.defaults.headers.common || {};
            $httpProvider.defaults.headers.common['${csrf.headerName}'] = '${csrf.token}';
        }])
        .controller('MenuCtrl', ['$scope', '$http', function($scope, $http){
            var self = this;
            $http.get(menuDataUrl).success(function(data) {
                $scope.error = undefined;
                var menu = data.menu;
                $scope.selectedItem = menu;
                $scope.menuItems = [menu]
                $scope.menu = menu;
                $scope.pages = data.pages;
                $scope.canCreateMenuItem = ${permissions:canDoThis(site, 'CREATE_MENU_ITEM')};

                $scope.$watch('selectedItem.page', function(value) {
                    if($scope.selectedItem.page && (!$scope.selectedItem.name || !Object.keys($scope.selectedItem.name).length)) {
                        $scope.selectedItem.name = $scope.pages[$scope.selectedItem.page].name;
                    }
                })

                $scope.create = function() {
                    if(!$scope.selectedItem.children) $scope.selectedItem.children = [];
                    var newItem = {key: "" + Math.random(), title:'New Item', name: {} , folder: false, use: "folder"};
                    $scope.selectedItem.folder = true;
                    $scope.selectedItem.children.push(newItem);
                    setTimeout(function() {
                        $scope.$apply(function(){
                            $scope.selectedItem = newItem;
                        });
                    });
                };

                $scope.save = function() {
                    $http.post(saveMenuUrl, $scope.menuItems[0]).
                        success(function(data, status, headers, config) {
                            var menu = data.menu;
                            $scope.menuItems = [menu]
                            $scope.menu = menu;
                            $scope.pages = data.pages;
                            $scope.selectedItem = $scope.menuItems[0];
                        });
                };

                $scope.delete = function() {
                    var parent = getMenuItemParent($scope.menuItems[0], $scope.selectedItem.key);
                    if(parent) {
                        var selectedIndex = parent.children.map(function(child){return child.key;}).indexOf($scope.selectedItem.key);
                        parent.children.splice(selectedIndex, 1);
                        $scope.selectedItem = parent;
                    }
                };

                function getMenuItemParent(root, menuItemKey) {
                    if(root && root.children && menuItemKey) {
                        if(root.children.map(function(el){return el.key;}).filter(function(childKey){ return childKey == menuItemKey; }).length) return root;

                        for(var i=0; i<root.children.length; ++i) {
                            var found = getMenuItemParent(root.children[i], menuItemKey);
                            if(found) return found;
                        }
                    }
                    return null;
                };

                $scope.toArray = function(object)  {
                    var array = [];
                    if(typeof(object) === "object") {
                        angular.forEach(object, function(value, key) {
                            array.push(value);
                        });
                    }
                    return array;
                };


                $(window).bind('keydown', function(event) {
                    if (event.ctrlKey || event.metaKey) {
                        switch (String.fromCharCode(event.which).toLowerCase()) {
                        case 's':
                            event.preventDefault();
                            $scope.save();
                            break;
                        }
                    }
                });
            })
            .error(function(e){
                debugger;
                $scope.error = "An error occurred while trying to save the menu."
            });

        }])


</script>

<style type="text/css">
.ui-fancytree {
    outline: none;
    min-height: 300px !important;
}
</style>
