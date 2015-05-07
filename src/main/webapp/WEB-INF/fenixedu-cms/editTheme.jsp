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

${portal.angularToolkit()}
<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular-route.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/ng-context-menu.js" type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/static/js/ng-file-upload-shim.js" type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/static/js/ng-file-upload.js" type="text/javascript" charset="utf-8"></script>

<div class="page-header">
  <h1>Sites</h1>
  <h2><small>${theme.name}</small></h2>
</div>
<div ng-app="cmsFileViewer">
<div ng-view class="fileViwer">
</div>
</div>
<style type="text/css">
    .position-fixed {
  position: fixed;
}

.fileViwer{
    min-height: 500px;
}
.dragover{
    background: red;
}
</style>
<script>

var Directory = function(name, path, parent){
    this.name = name;
    this.path = path;
    this.size = 0;
    this.contentType = 'application/vnd.fenixedu.docs.directory';
    this.modified = "";
    this.files = {};
    this.parent = parent;
    this.getFiles = function(){
        function alphabetical(a, b)
        {
             var A = a.name.toLowerCase();
             var B = b.name.toLowerCase();
             if (A < B){
                return -1;
             }else if (A > B){
               return  1;
             }else{
               return 0;
             }
        }

        var that = this;
        return Object.keys(this.files).map(function(e){
            return that.files[e];
        }).sort(alphabetical);
        
    }
    this.getPath = function(){
        var pivot = this;
        var result = []
        while(true){
            if (pivot.parent == null){
                break    
            }
            result.unshift(pivot);
            pivot = pivot.parent;
        }
        return result;
    }
}

function generateTree(files){
    function _subdir(existent, path, file, traversed){
        if (path.length == 1){
            existent.files[path] = file;
        }else{
            if (!(path[0] in existent.files)){
                existent.files[path[0]] = new Directory(path[0],traversed + "/" + path[0], existent);
            }
            _subdir(existent.files[path[0]], path.slice(1),file, traversed + "/" + path[0] );
        }
    }

    var tld = new Directory("","", null);
    for (var i = 0; i < files.length; i++) {
        var file = files[i];

        if (file.path.indexOf("/") > -1){
            var path = file.path.split('/');    
            _subdir(tld, path, file, "");
        }else{
            tld.files[file.name] = file;
        }
    };
    return tld;
}

function chdir(rootNode, path){
    if (!path || path == "" || path == "/"){
        return rootNode;
    }

    var pathArr = path.split("/");

    var pivot = rootNode;
    for (var i = 0; i < pathArr.length; i++) {
        var cur = pathArr[i];
        console.log(cur)
        pivot = pivot.files[cur];
        if (!pivot){
            throw "Directory not found '" + pathArr + "'"
        }
    };
    return pivot;
}

var app = angular.module('cmsFileViewer', ['ngRoute','ng-context-menu','ngFileUpload']);

app.config(['$routeProvider','$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider.when("/:resourceUrl*?", {
        templateUrl: Bennu.contextPath + "/static/templates/FileViewer.html",
        controller:'viewerCtrl',
    })
}]).run(function($http,$rootScope,$location){
    $http.get("/cms/themes/${theme.type}/listFiles").success(function(e){ 
        $rootScope.root = generateTree(e);
        $rootScope.theme = "${theme.name}"
        if ($location.path()){
            $location.path($location.path());
        }else{
            $location.path("/");
        }
    });
});

app.controller('viewerCtrl', function($scope,$http,$rootScope,$location,$routeParams,Upload) {
    $scope.$watch('files', function () {
        $scope.upload($scope.files);
    });

    $scope.upload = function (files) {
        if (files && files.length) {
            for (var i = 0; i < files.length; i++) {
                var file = files[i];
                var filename = file.name;
                if($scope.node.parent){
                    filename = $scope.node.path + "/" + filename;
                }
                Upload.upload({
                    url: 'importFile',
                    fields: {'filename': filename},
                    fileFormDataName: 'uploadedFile',
                    file: file
                }).progress(function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    console.log('progress: ' + progressPercentage + '% ' + evt.consolefig.file.name);
                }).success(function (data, status, headers, config) {
                    console.log('file ' + config.file.name + 'uploaded. Response: ');
                    $http.get("/cms/themes/${theme.type}/listFiles").success(function(e){ 
                        $rootScope.root = generateTree(e);
                        $rootScope.theme = "${theme.name}"
                        $scope.node = chdir($rootScope.root,$routeParams.resourceUrl);
                    });
                });
            }
        }
    };

    $scope.contextFor = function(f){
        $scope.contextFile = f;
    }

    $scope.editFile = function(){
        $scope.open($scope.contextFile);
    }

    $scope.deleteContextFile = function(){
        $.post(Bennu.contextPath + "/cms/themes/${theme.type}/deleteFile", {
            path : $scope.contextFile.path
        }, function() {
            $http.get("/cms/themes/${theme.type}/listFiles").success(function(e){ 
                $rootScope.root = generateTree(e);
                $rootScope.theme = "${theme.name}"
                $scope.node = chdir($rootScope.root,$routeParams.resourceUrl);
            });
        });
    }


    $scope.node = chdir($rootScope.root,$routeParams.resourceUrl);
    
    $scope.open = function(node){       
        if (node.contentType === "application/vnd.fenixedu.docs.directory"){
            $location.path(node.path);
        }else{
            window.location = Bennu.contextPath + "/cms/themes/${theme.type}/editFile/" + node.path;
        }
    }
});

angular.module('cmsFileViewer')
    .filter('fileIcon', function () {
        return function (obj) {
            

            var specificMimetypes = {
                'application/pdf': 'pdf',
                'application/vnd.fenixedu.docs.directory': 'folder',
                'text/css': 'html',
                'text/html': 'html',
                'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'xls',
                'application/vnd.ms-exel': 'xls',
                'application/vnd.openxmlformats-officedocument.presentationml.presentation': 'ppt',
                'application/vnd.openxmlformats-officedocument.presentationml.slideshow': 'ppt',
                'application/vnd.ms-powerpoint': 'ppt',
                'application/x-gzip': 'zip',
                'application/zip': 'zip',
                'multipart/x-gzip': 'zip',
                'multipart/x-zip': 'zip'
            };


            var knownExtensions = {
                xls : 'xls',
                xlsx : 'xls',
                csv : 'xls',
                pdf : 'pdf',
                css : 'html',
                less : 'html',
                sass : 'html',
                scss : 'html',
                html : 'html',
                ppt : 'ppt',
                pptx : 'ppt',
                pptm : 'ppt',
                pot : 'ppt,',
                zip : 'zip',
                rar : 'zip',
                'tar': 'zip',
                'gz' : 'zip',
                'tgz' : 'zip'
            };

            // generic mimetypes: application, audio, chemical, image, message, model, text, video, x-conference

            if ( specificMimetypes[obj] ) {
                return 'icon icon-filetype-' + specificMimetypes[obj];
            } else {
                return 'icon icon-filetype-' + obj.split('/')[0];
            }
        };
    });
angular.module('cmsFileViewer')
    .filter('bytes', function () {
        return function (bytes, precision, typeOfNull) {
            if ( isNaN(parseFloat(bytes)) || !isFinite(bytes) || !bytes ) {
                if ( typeof typeOfNull === 'undefined' ) {
                    return '-';
                }
                return typeOfNull;
            }
            if ( typeof precision === 'undefined' ) {
                precision = 1;
            }

            var units = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB'],
                number = Math.floor(Math.log(bytes) / Math.log(1024));

            if (number === 0) {
                precision = 0;
            }

            return (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) + ' ' + units[number];
        };
    });

angular.module('cmsFileViewer')
    .filter('fuzzy', function () {
        return function (date) {
            moment.locale(Bennu.locale.tag);
            return moment(date).fromNow();
        }; 
    });
</script>

<div class="modal fade" id="templates-modal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
                <h4 class="modal-title">Templates</h4>
            </div>
            <div class="modal-body">
                <div>
                    <table class="table table-hover table-bordered">
                        <c:forEach var="template" items="${theme.templatesSet}">
                            <tr>
                                <td>
                                    <h5>${template.name} (<samp>${template.type}</samp>) </h5>


                                    <div>
                                        ${template.description}
                                    </div>

                                    <p>
                                        <code>${template.filePath}</code>
                                    </p>

                                    <div class="btn-group pull-right">
                                        <a class="btn btn-danger btn-icon deleteBtn" data-toggle="modal" data-file="${template.type}">
                                            <i class="glyphicon glyphicon-trash"></i>
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                    <p class="help-block">If you want to create a new template, right click on the file and select 'Make a Template'</p>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<script type="text/javascript">
    $(function () {
        $('.deleteBtn').popover({
            placement:"right",
            title: 'Are you sure? <span class="close"></span>',
            html: true,
            content: '<p class="help-block">You are about to delete this template. The template file will remain unaffected. Are you sure?</p> <button class="btn btn-danger">Delete this Template</button>'
        });
    })
</script>
