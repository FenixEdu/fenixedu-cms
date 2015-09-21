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

<script src="${pageContext.request.contextPath}/static/js/moment.js" type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/static/js/jsdiff.js" type="text/javascript" charset="utf-8"></script>

${portal.toolkit()}

<c:set var="isPageVersion" value="${page != null}" />
<c:set var="backUrl" value="${page != null ? page.getEditUrl() : post != null ? post.getEditUrl() : '#'}" />
<c:set var="previewUrl" value="${page != null ? page.getAddress() : post != null ? post.getAddress() : '#'}" />

<div class="page-header">
    <h1>${site.name.content}</h1>

    <h2><small><a href="${backUrl}">Edit - ${post.name.content}</small></a></h2>

    <div class="row">
        <div class="col-sm-12">
            <button class="btn btn-primary revisionToRevert">
                <span class="glyphicon glyphicon-floppy-disk"></span> Revert
            </button>
            <a href="${backUrl}" class="btn btn-default">
                <span class="glyphicon glyphicon-edit"></span> Edit
            </a>
            <a href="#" class="btn btn-default disabled">
                <span class="glyphicon glyphicon-time"></span> Versions
            </a>
            <button type="button" class="btn btn-default disabled">
                <span class="glyphicon glyphicon-cog"></span> Metadata
            </button>
            <a target="_blank" href="${previewUrl}" class="btn btn-default disabled">
                <span class="glyphicon glyphicon-link"></span> Link
            </a>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-sm-4">
        <button id="next" class="btn btn-default"><i class="icon icon-left-open"></i></button>
    </div>
    <div class="col-sm-4">
        
    </div>
    <div class="col-sm-4 ">
        <button id="previous" class="btn btn-default pull-right"><i class="icon icon-right-open"></i></button>
    </div>
</div>

<p></p>

<div class="panel panel-default">
    <div class="panel-body">
       <div class="row">
    <div class="col-sm-6">
        <div id="author"></div>
        <div id="revDate"></div>
    </div>
    <div class="col-sm-6">
        <div class="pull-right">
            <button class="btn btn-default sidebyside">Side by Side</button>
        </div>
    </div>
</div>

<style>
    .code{
        font-family: Inconsolata;
    }
</style>

<div class="row merge">
    <div class="col-sm-12">
        <div id="merged" class="form-control code"></div>
    </div>
</div>

<div class="row side" style="display:none;">
    <div class="col-sm-6">
        <div class="form-control code before"></div>
    </div>
    <div class="col-sm-6">
        <div class="form-control code after"></div>
    </div>
</div>

<div class="modal fade" id="revert-modal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/cms/versions/${site.slug}/${post.slug}/revertTo" id="revertForm" method="post">
                ${csrf.field()}

                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h3 class="modal-title">Revert Content</h3>
                    <small>Revert to previous version of '${post.name.content}'</small>
                </div>

                <div class="modal-body">
                    <input type="hidden" name="revision" id="revisionToRevert">
                    <div class="form-group">
                        <p>Are you sure you want to revert the content of this post ?</p>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="reset" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-danger">Revert</button>
                </div>

            </form>
        </div>
    </div>
</div>

<style>
    .diff-removed{
        background-color: #F7CBCC;
    }
    .diff-added{
        background-color: #A8F2A9;
    }
    .code{
        height:auto;
    }
</style>

<script>
    $.ajaxSetup({headers: {'${csrf.headerName}': '${csrf.token}'}});

    function loadRevision(id){
        if (!id) {
            var params = {}
        }else{
            var params = {revision:id}
        }

        $.post(Bennu.contextPath + "/cms/versions/${site.slug}/${post.slug}/data", params).done(function(e){
            var content = Bennu.localizedString.getContent(e.content);
            var previousContent = "";;
            if (e.previousContent){
              var previousContent =  Bennu.localizedString.getContent(e.previousContent);
            }

            var diff = JsDiff.diffWordsWithSpace(previousContent, content);
            var x = ""
            var before = "";
            var after = "";
            diff.forEach(function(part){
                // green for additions, red for deletions
                // grey for common parts
                if(part && part.value) {
                    var val = part.value.replace(/[\u00A0-\u9999<>\&]/gim, function(i) {
                        return '&#'+i.charCodeAt(0)+';';
                    }).replace(/\n/gim,function(i){

                    });
                    if (part.added){
                        x += "<span class='diff-added'>" + val + "</span>"
                        after += "<span class='diff-added'>" + val + "</span>";
                    }else if(part.removed){
                        x += "<span class='diff-removed'>" + val + "</span>"
                        before += "<span class='diff-removed'>" + val + "</span>";
                    }else{
                        x += val;
                        after += val
                        before += val
                    }
                }
            });

            if (e.next){
                $("#next").removeAttr("disabled").data("id", e.next);
            }else{
                $("#next").attr("disabled","disabled").data("id", null);
            }

            if (e.previous){
                $("#previous").removeAttr("disabled").data("id", e.previous);
            }else{
                $("#previous").attr("disabled","disabled").data("id", null);
            }
            $("#author").html(e.userName);
            $("#revDate").html(moment(e.modifiedAt).locale(Bennu.locale.tag).format('MMMM Do YYYY, h:mm:ss a'));
            $("#revDate").data("current",e.id)
            $("#merged").html(x);
            $(".before").html(before)
            $(".after").html(after)
        });
    }
    $("#next").on("click",function(){
        if($("#next").data("id")){
            loadRevision($("#next").data("id"));
        }
    });
    $("#previous").on("click",function(){
        if($("#previous").data("id")){
            loadRevision($("#previous").data("id"));
        }
    });
    $(".sidebyside").on("click",function(){
        if($(".sidebyside").hasClass("active")){
            $(".side").hide();
            $(".merge").show();
            $(".sidebyside").removeClass("active");
        }else{
            $(".side").show();
            $(".merge").hide();
            $(".sidebyside").addClass("active");
        }
    });
    $(".revisionToRevert").on("click",function(){
        var form = $("#revertForm");
        $("#revisionToRevert", form).val($("#revDate").data("current"));
        $('#revert-modal').modal();
    });
    loadRevision();

    $(".sidebyside").trigger("click");
</script>

