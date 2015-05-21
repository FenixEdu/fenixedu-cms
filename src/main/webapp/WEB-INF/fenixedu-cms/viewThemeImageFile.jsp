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

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/jquery.jsonview.css"/>
<script src="${pageContext.request.contextPath}/static/js/jquery.jsonview.js"></script>

<script type="text/javascript">
    var supported=["text/plain","text/html","text/javascript","application/javascript","application/json","text/css","image/jpeg","image/jp2","image/jpx","image/jpm","video/mj2","image/vnd.ms-photo","image/jxr","image/webp","image/gif","image/png","video/x-mng","image/tiff","image/tiff-fx","image/svg+xml","application/pdf","image/x‑xbitmap","image/bmp"];
</script>

<div class="page-header">
  <h1>Sites</h1>
  <h2><small><c:out value="${theme.name}" /></small></h2>
</div>
<div class="btn-toolbar" style="background:white" role="toolbar">
  <div class="btn-group">
  </div>
  <div class="btn-group">
    <a href="<c:url value="${ linkBack }" />" type="button" class="btn btn-default"><span class="glyphicon glyphicon-chevron-left"></span> <spring:message code="action.back"></spring:message></a>
    <a href="#" class="btn btn-default" data-toggle="modal" data-target="#viewMetadata">Metadata</a>
  </div>

  </div>
<p>
    <div class="row">
        <div class="col-sm-8"><div class="thumbnail">
        <c:if test="${isSVG}">
            ${content}
        </c:if>
        <c:if test="${not isSVG}">
            <img src="data:${type};base64,${content}" />
        </c:if>
    </div></div>
        <div class="col-sm-4">
            <div class="panel panel-primary">
                  <div class="panel-heading">
                        <h3 class="panel-title">Image</h3>
                  </div>
                  <div class="panel-body">
                        <dl class="dl-horizontal">
                            <dt>File</dt>
                            <dd><samp>${file.fullPath}</samp></dd>
                            <dt>Dimensions</dt>
                            <dd><samp>${width}x${height}</samp></dd>
                            <dt>Content Type</dt>
                            <dd><samp>${file.contentType}</samp></dd>
                        </dl>
                  </div>
            </div>

        </div>
    </div>
</p>

<div class="modal fade" id="viewMetadata" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                     <button type="button" class="close" data-dismiss="modal"><span class="sr-only">Close</span></button>
                <h3 class="modal-title">Metadata</h3>
                <small>Extra information of your file</small>
            </div>
            <div class="modal-body">
                <div class="clearfix">
                    <div class="form-group">
                        <div class="col-sm-12">
                            <div class="json-data"></div>
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

<script>
    <c:if test="${not isSVG}">
        $(".json-data").JSONView(${metadata}, {});
    </c:if>
</script>
