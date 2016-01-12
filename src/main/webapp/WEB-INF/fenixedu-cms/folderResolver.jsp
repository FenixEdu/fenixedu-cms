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
<script src="${pageContext.request.contextPath}/static/js/ace/ace.js" type="text/javascript" charset="utf-8"></script>

<div class="page-header">
    <h1>${folder.functionality.description.content}
          <small>
              <ol class="breadcrumb">
                    <li><a href="${pageContext.request.contextPath}/cms">Sites</a></li>
                    <li><a href="${pageContext.request.contextPath}/cms">Tags</a></li>
                </ol>
          </small>
    </h1>
</div>

<div class="editor-content">
    <p>
        <div class="btn-toolbar" style="background:white" role="toolbar">
            <button class="btn btn-primary" id="saveBtn"><spring:message code="action.save" /></button>
            <div class="pull-right">
                <div class="btn-group">
                    <button class="btn btn-fullscreen btn-default"><i class="glyphicon glyphicon-fullscreen"></i></button>
                </div>
                <div class="btn-group">
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="theme-msg">Theme</span> <span class="caret"></span></button>
                    <ul class="dropdown-menu themes" role="menu">
                        <li><a data-theme="ambiance" href="#">Ambiance</a></li>
                        <li><a data-theme="chaos" href="#">Chaos</a></li>
                        <li><a data-theme="chrome" href="#">Chrome</a></li>
                        <li><a data-theme="clouds" href="#">Clouds</a></li>
                        <li><a data-theme="clouds_midnight" href="#">Clouds Midnight</a></li>
                        <li><a data-theme="cobalt" href="#">Cobalt</a></li>
                        <li><a data-theme="crimson_editor" href="#">Crimson Editor</a></li>
                        <li><a data-theme="dawn" href="#">Dawn</a></li>
                        <li><a data-theme="dreamweaver" href="#">Dreamweaver</a></li>
                        <li><a data-theme="eclipse" href="#">Eclipse</a></li>
                        <li><a data-theme="github" href="#">GitHub</a></li>
                        <li><a data-theme="solarized_dark" href="#">Solarized Dark</a></li>
                        <li><a data-theme="solarized_light" href="#">Solarized Light</a></li>
                        <li><a data-theme="terminal" href="#">Terminal</a></li>
                        <li><a data-theme="textmate" href="#">TextMate</a></li>
                        <li><a data-theme="xcode" href="#">XCode</a></li>
                    </ul>
                </div>

            </div>
        </div>
    </p>

    <p class="alert alert-danger" id="error" style="display: none"></p>

    <div id="folder-code-full"></div>

    <div class="row" id="folder-container">
        <div class="col-md-12" id="folder-code-default">
            <pre id="editor"><c:out value="${folder.resolver.code}"/></pre>
        </div>
    </div>
</div>

<style>
    #folder-code-full {
        display: initial;
    }
	#editor {
		height: 512px;
	}
    .fullscreen{
        position: absolute;
        top: 0px;
        left: 0px;
        height: 100%;
        width: 100%;
        z-index: 999999999;
        background: white;
        padding-left: 20px;
        padding-right: 20px;
    }
    .fullscreen #editor{
        height:90%;
    }
</style>

<script>
    var editor = ace.edit("editor");

    var submit = function() {
      $("#error").hide();
      $("#saveBtn").attr('disabled', true);
      $("#saveBtn").html('<spring:message code="action.saving"/>');
        $.ajax('${pageContext.request.contextPath}/cms/folders/resolver/${folder.externalId}',
             { data: editor.getValue(), type: 'PUT' }).always(function () {
                $("#saveBtn").attr('disabled', false); $("#saveBtn").html('<spring:message code="action.save" />');
             }).error(function (data) {
				$("#error").show(); $("#error").text(data.responseText);
             });
    };

    editor.setFontSize(13);
    editor.setTheme("ace/theme/clouds");
    editor.getSession().setMode("ace/mode/javascript");
    editor.setHighlightActiveLine(false);
    editor.setShowPrintMargin(false);
    editor.commands.addCommand({
        name: 'save',
        bindKey: {win: 'Ctrl-S',  mac: 'Command-S'},
        exec: submit
    });
    var theme;

    if(typeof(Storage) !== "undefined") {
      theme = localStorage.getItem("cmsThemeEditorTheme") || "clouds";
    }
    editor.setTheme("ace/theme/" + theme);

    $(".theme-msg").html($("a[data-theme='" + theme + "']").html())

    $(".themes a").on("click", function (e){
      e = $(e.target);
      editor.setTheme("ace/theme/" + e.data("theme"));
      localStorage.setItem("cmsThemeEditorTheme", e.data("theme"));
      $(".theme-msg").html($("a[data-theme='" + e.data("theme") + "']").html())
    });

    $(".btn-fullscreen").on("click",function() {
      if($(".btn-fullscreen").hasClass("active")) {
        $("#editor").detach().appendTo('#folder-code-default');
        $('#folder-container').show();
        $(".editor-content").removeClass("fullscreen");
        $(".btn-fullscreen").removeClass("active");
      } else {
        $("#editor").detach().appendTo('#folder-code-full');
        $('#folder-container').hide();
        $(".editor-content").addClass("fullscreen");
        $(".btn-fullscreen").addClass("active");
      }
    })

    $('#saveBtn').on('click', submit);
</script>
