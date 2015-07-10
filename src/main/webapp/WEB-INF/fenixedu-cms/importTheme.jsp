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

${portal.angularToolkit()}
<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular-route.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/ng-file-upload-shim.js" type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/static/js/ng-file-upload.js" type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/static/js/jszip.js" type="text/javascript" charset="utf-8"></script>

<script>
    (function () {
      'use strict';

      angular.module('file-model', [])
      
      .directive('fileModel', [
        '$parse',
        function ($parse) {
          return {
            restrict: 'A',
            link: function(scope, element, attrs) {
              var model = $parse(attrs.fileModel);
              var modelSetter = model.assign;

              element.bind('change', function(){
                scope.$apply(function(){
                  if (element[0].files.length > 1) {
                    modelSetter(scope, element[0].files);
                  }
                  else {
                    modelSetter(scope, element[0].files[0]);
                  }
                });
              });
            }
          };
        }
      ]);

    })();
</script>

<div class="page-header">
  <h1>Themes</h1>
  <h2><a href="."><small>Import Theme</small></a></h2>
</div>

<div ng-app="uploadTheme">
    <div ng-controller="upload">
        <div ng-if="file == null">
            <div class="row">
                <div class="col-sm-12">
                    <input type="file" class="hidden" file-model="$parent.file" id="selectFileInput">
                    <button id="selectFile" class="btn btn-primary">Select File</button>
                    <script>
                        $('#selectFile').on('click', function() {
                            $('#selectFileInput').trigger('click');
                        });
                    </script>
                </div>
            </div>

            <div class="drop-box" ngf-drop ng-model="$parent.files" ngf-drag-over-class="dragover" ngf-multiple="false" ngf-allow-dir="false">
                <p><span>Drag and drop file</span>Upload a new theme</p>
            </div>
        </div>
        <div ng-if="theme != null" class="interfaceUpload" style="display:none;">
            <h3 class="sub-header">{{theme.name}}</h3>
            <p>{{theme.description}}</p>
            <div class="row">
                <div class="col-sm-6">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            Templates
                        </div>
                        <div class="panel-body">
                            <div class="row" ng-repeat="(key, value) in theme.templates">
                                <div class="col-sm-12">
                                    <h5>{{value.name}}</h5>
                                    <p>{{value.description}}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            Information
                        </div>
                        <div class="panel-body">
                           <dl class="dl-horizontal">
                                <dt>Type</dt>
                                <dd><samp>{{theme.type}}</samp></dd>
                                <dt>Size</dt>
                                <dd>{{files[0].size | bytes}}</dd>
                                <dt>Files</dt>
                                <dd>{{theme.files.length}}</dd>
                           </dl>
                        </div>
                    </div>
                </div>
            </div>
            
            <button ng-click="uploadData()" type="button" class="btn btn-primary">Import</button>

        </div>
    </div>
</div>

<script type="application/javascript">
    function getReadableFileSizeString(fileSizeInBytes) {
        var i = -1;
        var byteUnits = [' kB', ' MB', ' GB', ' TB', 'PB', 'EB', 'ZB', 'YB'];
        do {
            fileSizeInBytes = fileSizeInBytes / 1024;
            i++;
        } while (fileSizeInBytes > 1024);

        return Math.max(fileSizeInBytes, 0.1).toFixed(1) + byteUnits[i];
    }

    var app = angular.module('uploadTheme', ['ngFileUpload','file-model']);

    app.controller('upload', function($scope,Upload) {

        $scope.$watch('files', function () {
            if ($scope.files && $scope.files.length) {
                $scope.file = $scope.files[0];
            }
        });

        $scope.$watch('file', function () {
            if ($scope.file) {
                $scope.upload($scope.file);
            }
        });

        $scope.upload = function (file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                var zip = new JSZip(e.target.result);
                a = zip;
                
                $scope.theme = JSON.parse(zip.file("theme.json").asText());
                $scope.theme.files = Object.keys(zip.files)
                $scope.$apply();
                $(".interfaceUpload").show();
            }
            reader.readAsArrayBuffer(file);
        };

        $scope.uploadData = function(){
            Upload.upload({
                url: 'create',
                fields: { },
                fileFormDataName: 'uploadedFile',
                file: $scope.file
            }).progress(function (evt) {

            }).success(function (data, status, headers, config) {
                window.location = Bennu.contextPath + "/cms/themes/" + $scope.theme.type + "/see"
            });
        }
    });

    angular.module("uploadTheme").filter('bytes', function () {
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

    app.config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.headers.common = $httpProvider.defaults.headers.common || {};
        $httpProvider.defaults.headers.common['${csrf.headerName}'] = '${csrf.token}';
    }]);
</script>

<style>
.dragover{
    border: 1px dashed #39f;
    color: #39f;
    transition: all 0.15s ease-in-out;
}
.drop-box{
    height:300px;
}
</style>