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
${portal.toolkit()}

<div class="page-header">
    <h1>Media Library - ${postFile.files.displayName}</h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/media/${site.slug}">${site.name.content}</a></small></h2>
</div>

<p>
    <button type="button" data-toggle="modal" data-target="#updateModal" class="btn btn-primary">
        <i class="glyphicon glyphicon-edit"></i> Edit
    </button>
    <a href="${cms.downloadUrl(postFile.files)}" target="_blank" class="btn btn-default" download="${postFile.files.filename}">
        <i class="glyphicon glyphicon-cloud-download"></i> Download
    </a>
</p>


<div class="row">
    <div class="col-sm-7">
        <a href="${cms.downloadUrl(postFile.files)}" target="_blank">
            <div class="hidden-xs thumbnail">
            <c:choose>
                <c:when test="${postFile.files.contentType.startsWith('image/')}">
                    <div style="max-width:100%;">
                        <img src="${cms.downloadUrl(postFile.files)}" class="img-responsive" data-holder-rendered="true" />
                    </div>
                </c:when>

                <c:otherwise>
                    <div style="min-height:600px; background:#efefef;">
                        <div style="font-size: 60px; color: #999; display: table; margin: 0 auto; padding-top: 300px;">
                            <center>
                                <h5 style="background:#efefef; color: #999">No thumbnail available</h5>
                            </center>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        </a>

    </div>
    
    <div class="col-sm-5">
        <div class="panel panel-primary">
          <div class="panel-heading">Details</div>
          <div class="panel-body">
            <dl class="dl-horizontal">
                <dt>Display Name</dt>
                <dd><span alt="The name that will be presented">${postFile.files.displayName}</span></dd>

                <dt>File Name</dt>
                <dd><span alt="The name of the file when it is downloaded">${postFile.files.filename}</span></dd>

                <dt>Type</dt>
                <dd><samp><c:out value="${postFile.files.contentType}"/></samp></dd>

                <dt><span alt="Size of the file">Size</span></dt>
                <dd>${cms.prettySize(postFile.files.size)}</dd>

                <dt>URL</dt>
                <dd><input type="text" value="${cms.downloadUrl(postFile.files)}" readonly="true" onclick="$(this).select();"></dd>

                <dt>Presentation</dt>
                <dd>
                    <c:choose>
                        <c:when test="${postFile.isEmbedded}">
                            <span alt="This file is directly shown in the post content">Embedded</span>
                        </c:when>
                        <c:otherwise>
                            <span alt="This file is shown as an attachment of the post and is listed at the end of the post content">Attachment</span>
                        </c:otherwise>
                    </c:choose>
                </dd>

                <dt>Post</dt>
                <dd><a href="${postFile.post.editUrl}">${postFile.post.name.content}</a></dd>

                <dt>Access Control</dt>
                <dd>This file is visible to <samp>${postFile.files.accessGroup.getPresentationName()}</samp></dd>
            </dl>
          </div>
        </div>

        <div class="panel panel-danger">
          <div class="panel-heading">Danger Zone</div>
          <div class="panel-body">
            <p class="help-block">Once you delete a file, there is no going back. Please be certain.</p>
            <button data-toggle="modal" data-target="#deleteModal" class="btn btn-danger">Delete this File</button>
          </div>
        </div>
    </div>
</div>

<div class="modal fade" id="deleteModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Are you sure?</h4>
      </div>
      <div class="modal-body">
        <p>You are about to delete the file '<c:out value="${postFile.files.displayName}" />'. There is no way to rollback this operation. Are you sure? </p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
        <button type="button" onclick="$('#deleteForm').submit();" class="btn btn-danger">Yes</button>
        <form action="delete" method="post" id="deleteForm">${csrf.field()}</form> 
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="updateModal">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
        <form method="post" class="form-horizontal" role="form">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h3 class="modal-title">Edit File</h3>
                <small>Change information about file '${postFile.files.displayName}'</small>
            </div>
            <div class="modal-body">
               ${csrf.field()}
                <div class="form-group">
                    <label class="col-sm-2 control-label">Display Name</label>
                    <div class="col-sm-10">
                        <input type="text" name="displayName" class="form-control" placeholder="Display Name" value="${postFile.files.displayName}">
                    </div>
                </div>
                
                <div class="form-group">
                    <label class="col-sm-2 control-label">File Name</label>
                    <div class="col-sm-10">
                        <input type="text" name="filename" class="form-control" placeholder="File Name" value="${postFile.files.filename}">
                    </div>
                </div>
                
                <c:if test="${!postFile.getIsEmbedded()}">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">Access Group</label>
                        <div class="col-sm-10">
                           <input bennu-group allow="public,users,managers,custom" name="accessGroup" type="text" value="${postFile.files.accessGroup.expression}"/>
                        </div>
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