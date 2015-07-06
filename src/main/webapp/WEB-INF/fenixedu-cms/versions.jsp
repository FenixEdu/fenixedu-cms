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

<script src="${pageContext.request.contextPath}/static/js/jsdiff.js" type="text/javascript" charset="utf-8"></script>

${portal.toolkit()}

<div class="page-header">
    <h1>Sites</h1>
    <h2><small>${site.name.content}</small></h2>
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
            <button class="btn btn-default revisionToRevert">Revert this revision</button>
        </div>
    </div>
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

    function loadRevision(id){
        if (!id) {
            var params = {}
        }else{
            var params = {revision:id}
        }
        
        $.post(Bennu.contextPath + "/cms/posts/${site.slug}/${post.slug}/versionData",params).done(function(e){
            
            var content = e.content['en-GB'];
            var previousContent = e.previousContent['en-GB'];
   
            var diff = JsDiff.diffWordsWithSpace(previousContent, content);
            var x = ""
            var before = "";
            var after = "";
            diff.forEach(function(part){
                // green for additions, red for deletions
                // grey for common parts
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
        form.submit();
    });
    loadRevision();
</script>

<form action="revertTo" id="revertForm" method="post">
    ${csrf.field()}
    <input type="hidden" name="revision" id="revisionToRevert">
</form>

