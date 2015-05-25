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

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/jquery.jsonview.css"/>
<script src="${pageContext.request.contextPath}/static/js/jquery.jsonview.js"></script>

${portal.toolkit()}

<!-- <h2 class="page-header" style="margin-top: 0">
    <spring:message code="post.edit.title"/>
    <small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small>
    <div class="pull-right">
        <c:if test="${not empty post.address}">
            <a href="${post.address}" target="_blank" class="btn btn-default">Link</a>
        </c:if>
    </div>
</h2> -->

    <div class="page-header">
    <h1>Sites</h1>
    <h2><small>${site.name.content}</small></h2>
    </div>
<form class="form" action="" method="post" role="form" novalidate>

        <div class="row">
            <div class="col-sm-12">
                <button type="submit" class="btn btn-primary">Update</button>
                <a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/${post.slug}/versions" class="btn btn-default">Versions</a>
                <a href="#" class="btn btn-default" data-toggle="modal" data-target="#viewMetadata">Metadata</a>
            </div>
        </div>
        <p></p>
    <!-- Nav tabs -->
<!--     <ul class="nav nav-tabs" role="tablist">
        <li class="active"><a href="#postContent" role="tab" data-toggle="tab"><spring:message code="page.edit.label.post"/></a></li>
        <li><a href="#files" role="tab" data-toggle="tab">Post Files</a></li>
        <li><a href="#attachments" role="tab" data-toggle="tab">Attachments</a></li>

    </ul> -->
            

            <div class="${emptyName ? "form-group has-error" : "form-group"}">

                    <input bennu-localized-string required-any name="name" id="inputEmail3"
                           placeholder="<spring:message code="post.edit.label.name" />"
                           value='<c:out value="${post.name.json()}"/>'>
                    <c:if test="${emptyName != null}"><p class="text-danger"><spring:message
                            code="post.edit.error.emptyName"/></p></c:if>
            </div>
            <p>

            <div>Permalink: 
            input
            <samp>/${site.baseUrl}/${site.viewPostPage.slug}/</samp> 
            <input required type="hidden" name="newSlug" class="form-control" id="inputEmail3"
                               placeholder="<spring:message code="site.edit.label.slug" />" value='${post.slug}' \>

                               <button class="btn btn-default btn-xs">Edit</button> <a href="${post.address}"class="btn btn-default btn-xs">View Post</a></div>
            </p>

            <div class="form-group">
                    <textarea id="htmlEditor" bennu-html-editor bennu-localized-string name="body" rows="3"><c:out value="${post.body.json()}"/></textarea>
            </div>

<div class="panel panel-default">
    <div class="panel-heading">Publish</div>
    <div class="panel-body">
            <dl class="dl-horizontal">
                <dt>Published</dt>
                <dd><input type="checkbox" value="true" ${post.active ? 'checked="checked"' : ''} name="active" /></dd>
                <dt>Author</dt>
                <dd><input bennu-user-autocomplete class="form-control" type="text" value="${post.createdBy.username}"></dd>
                <dt>Access Control</dt>
                <dd><input bennu-group allow="public,users,managers,custom" name="viewGroup" type="text"
                           value="${ post.canViewGroup.expression }"/></dd>
            </dl>

            <div class="form-group">
                
            </div>

    </div>
</div>

<div class="panel panel-default">
  <div class="panel-heading">Excerpt</div>
  <div class="panel-body">
    <div class="row">
        <div class="col-sm-12">

            <textarea bennu-localized-string name="excerpt" id="excerpt" value=''></textarea>
            <p class="help-block">Excerpts are optional hand-crafted summaries of your content that can be used in your theme.</p>
        </div>

    </div>
  </div>
</div>

<div class="panel panel-default">
  <div class="panel-heading">Visible period</div>
  <div class="panel-body">
    <div class="row">
        <div class="col-sm-6">
            <label>
                Start
            </label>
            <input bennu-datetime name="publicationStarts" id="inputEmail3" value='${post.publicationBegin}'>
        </div>

        <div class="col-sm-6">
            <label>
                End
            </label>
            <input bennu-datetime name="publicationEnds" id="inputEmail3" value='${post.publicationEnd}'>
        </div>
    </div>
  </div>
</div>
<c:if test="${site.categoriesSet.size() > 0}">
<div class="panel panel-default">
  <div class="panel-heading"><spring:message code="site.manage.label.categories"/></div>
  <div class="panel-body">
    <div class="row">

                        <c:forEach var="c" items="${site.categoriesSet}" varStatus="loop">
                            <div class="col-sm-4">
                                <div class="checkbox">
                                    <label>
                                        <c:choose>
                                            <c:when test="${post.categoriesSet.contains(c)}">
                                                <input type="checkbox" name="categories" value="${c.slug}"
                                                       checked="checked"/> ${c.name.content}
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="categories"
                                                       value="${c.slug}"/> ${c.name.content}
                                            </c:otherwise>
                                        </c:choose>
                                    </label>
                                </div>
                            </div>
                        </c:forEach>
    </div>
    <div class="row">
        <div class="col-sm-12">
            <button class="btn btn-default btn-xs">Add New Category</button>
        </div>
    </div>
  </div>
</div>
</c:if>


<div class="modal fade" id="addAttachment" tabindex="-1" role="dialog" aria-hidden="true">
    <form action="addAttachment" enctype="multipart/form-data" class="form-horizontal" method="post">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span><span
                            class="sr-only">Close</span></button>
                    <h4><spring:message code="action.new"/></h4>
                </div>
                <div class="modal-body">

                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message
                                code="theme.view.fileName"/>:</label>

                        <div class="col-sm-10">
                            <input required type="text" name="name" class="form-control">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message
                                code="theme.add.label.file"/>:</label>

                        <div class="col-sm-10">
                            <input type="file" name="attachment" class="form-control">
                        </div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><spring:message code="label.make"/></button>
                </div>
            </div>
        </div>
    </form>
</div>

<div class="modal fade" id="attachmentDeleteModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4><spring:message code="action.delete"/></h4>
            </div>
            <div class="modal-body">
                <spring:message code="theme.view.label.delete.confirmation"/> <b id="fileName"></b>?
            </div>
            <div class="modal-footer">
                <form action="deleteAttachment" id="deleteAttachment" method="POST">
                    <input type="hidden" name="file"/>
                    <button type="submit" class="btn btn-danger"><spring:message code="label.yes"/></button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message
                            code="label.no"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="addFile" tabindex="-1" role="dialog" aria-hidden="true">
    <form action="addFile" enctype="multipart/form-data" class="form-horizontal" method="post">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span><span
                            class="sr-only">Close</span></button>
                    <h4><spring:message code="action.new"/></h4>
                </div>
                <div class="modal-body">

                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message
                                code="theme.add.label.file"/>:</label>

                        <div class="col-sm-10">
                            <input type="file" name="attachment" class="form-control">
                        </div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><spring:message code="label.make"/></button>
                </div>
            </div>
        </div>
    </form>
</div>

<div class="modal fade" id="viewMetadata" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4>Metadata</h4>
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
    <c:if test="${post.metadata != null}">$(".json-data").JSONView(${post.metadata}, {collapsed: true});
    </c:if>
    <c:if test="${post.metadata == null}">$(".json-data").JSONView({}, {collapsed: true});
    </c:if>
</script>

<style>
    .json-data {
        height: 400px;
        overflow: scroll;
        border: 1px solid #ddd;
        padding: 20px;
        margin-bottom: 20px;
        border-radius: 3px;
    }
</style>

<div class="modal fade" id="fileDeleteModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4><spring:message code="action.delete"/></h4>
            </div>
            <div class="modal-body">
                <spring:message code="theme.view.label.delete.confirmation"/> <b id="fileName"></b>?
            </div>
            <div class="modal-footer">
                <form action="deleteFile" id="deleteFile" method="POST">
                    <input type="hidden" name="file"/>
                    <button type="submit" class="btn btn-danger"><spring:message code="label.yes"/></button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message
                            code="label.no"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    $("[data-target='#attachmentDeleteModal']").on('click', function (event) {
        var index = $(event.target).closest("[data-file-index]").attr('data-file-index');
        var filename = $(event.target).closest("[data-file]").attr('data-file');
        $('#fileName').html(filename);
        $('#deleteAttachment')[0].file.value = index;
    });
</script>

<script>
    $("[data-target='#fileDeleteModal']").on('click', function (event) {
        var index = $(event.target).closest("[data-file-oid]").attr('data-file-oid');
        var filename = $(event.target).closest("[data-file]").attr('data-file');
        $('#fileName', $("#fileDeleteModal")).html(filename);
        $('#deleteFile')[0].file.value = index;
    });
</script>

<script>
    $("[data-origin]").on("click", function (e) {
        e = $(e.target).closest("[data-origin]")
        var form = $("#moveAttachment")[0];
        form.origin.value = e.data("origin");
        form.destiny.value = e.data("destiny");
        form.submit();
    });
</script>

<script>
    setTimeout(function () {
        if (window.location.hash === "#attachments") {
            var z = $("[href='#attachments']")
            z.tab('show')
            window.location.hash = '';
        }
    }, 150)
</script>

<script>
    setTimeout(function () {
        if (window.location.hash === "#files") {
            var z = $("[href='#files']")
            z.tab('show')
            window.location.hash = '';
        }
    }, 150)
</script>

<script>
    function submitFiles(files, cb) {

        var formData = new FormData();
        for (var i = 0; i < files.length; i++) {
            formData.append('attachment', files[i]);
        }


        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'addFile.json');

        function transferCanceled(event) {

        }

        function transferFailed(event) {

        }

        function transferComplete(event) {
            var objs = JSON.parse(event.currentTarget.response);
            cb(objs.map(function (x) {
                return x.url
            }));
        }

        function updateProgress(event) {
            if (event.lengthComputable) {
                var complete = (event.loaded / event.total * 100 | 0);
                //progress.value = progress.innerHTML = complete;
                console.log(complete);
            }
        }

        xhr.addEventListener("progress", updateProgress, false);
        xhr.addEventListener("load", transferComplete, false);
        xhr.addEventListener("error", transferFailed, false);
        xhr.addEventListener("abort", transferCanceled, false);

        xhr.send(formData);
    }
    $("#htmlEditor").data("fileHandler", submitFiles);
</script>

