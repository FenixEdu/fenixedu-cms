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

<script src="${pageContext.request.contextPath}/static/js/ace/ace.js" type="text/javascript" charset="utf-8"></script>

<div class="page-header">
  <h1>Folder</h1>
  <h2><small><c:out value="${theme.name}" /></small></h2>
</div>

<div class="editor-content">
  <p>
    <input type="hidden" name="content"/>
    <div class="btn-toolbar" style="background:white" role="toolbar">
      <div class="btn-group">
        <button type="button" id="saveBtn" class="btn btn-primary">
          <span class="glyphicon glyphicon glyphicon-save"></span> <span id="txt"><spring:message code="action.save" /></span></span>
        </button>
      </div>

      <div class="btn-group">
        <a href="<c:url value="${ linkBack }" />" type="button" class="btn btn-default">
          <span class="glyphicon glyphicon-chevron-left"></span> <spring:message code="action.back" />
        </a>
      </div>

      <div class="pull-right">
        <div class="btn-group">
          <button class="btn btn-fullscreen btn-default"><i class="glyphicon glyphicon-fullscreen"></i></button>
        </div>

        <div class="btn-group">
          <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
            <span class="theme-msg">Theme</span> <span class="caret"></span>
          </button>

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

        <div class="btn-group">
          <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
            <span class="modes-msg"><spring:message code="file.edit.label.highlight" /></span> <span class="caret"></span>
          </button>
          <ul class="dropdown-menu modes" role="menu">
            <li><a href="#" data-mode="text"><spring:message code="file.edit.label.text" /></a></li>
            <li><a href="#" data-mode="twig"><spring:message code="file.edit.label.html" /></a></li>
            <li><a href="#" data-mode="css"><spring:message code="file.edit.label.css" /></a></li>
            <li><a href="#" data-mode="less">LESS</a></li>
            <li><a href="#" data-mode="sass">SASS</a></li>

            <li><a href="#" data-mode="javascript"><spring:message code="file.edit.label.javascript" /></a></li>
            <li><a href="#" data-mode="json">JSON</a></li>
          </ul>
        </div>
      </div>
    </div>
  </p>

  <pre id="editor"><c:out value="${content}"/></pre>

</div>

<script type="application/javascript">

  var editor = ace.edit("editor");

  var submit = function() {
    $("#saveBtn").attr('disabled', true);
    $("#saveBtn #txt").html('<spring:message code="action.saving"/>');
    $.ajax('${pageContext.request.contextPath}/cms/themes/${theme.type}/editFile/${file.fullPath}',
    { headers: {"${csrf.headerName}":"${csrf.token}"}, data: editor.getValue(), type: 'PUT' }).done(function () {
    $("#saveBtn").attr('disabled', false); $("#saveBtn #txt").html('<spring:message code="action.save" />');
    });
  }

  editor.setFontSize(13);


  var theme;
  if(typeof(Storage) !== "undefined") {
    theme = localStorage.getItem("cmsThemeEditorTheme") || "clouds";
  }
  editor.setTheme("ace/theme/" + theme);
  $(".theme-msg").html($("a[data-theme='" + theme + "']").html())
  editor.getSession().setMode("ace/mode/${ type }");
  editor.setHighlightActiveLine(false);
  editor.setShowPrintMargin(false);
  editor.commands.addCommand({
    name: 'save',
    bindKey: {win: 'Ctrl-S',  mac: 'Command-S'},
    exec: submit
  });

  $(".modes a").on("click", function (e){
    e = $(e.target);
    editor.getSession().setMode("ace/mode/" + e.data("mode"));
    $(".modes-msg").html($("a[data-mode='" + e.data("mode") + "']").html())
  });

  $(".modes-msg").html($("a[data-mode='${ type }']").html())

  $(".themes a").on("click", function (e){
    e = $(e.target);
    editor.setTheme("ace/theme/" + e.data("theme"));
    localStorage.setItem("cmsThemeEditorTheme", e.data("theme"));
    $(".theme-msg").html($("a[data-theme='" + e.data("theme") + "']").html())
  });

  $(".btn-fullscreen").on("click",function(){
    if($(".btn-fullscreen").hasClass("active")){
      $(".editor-content").removeClass("fullscreen");
      $(".btn-fullscreen").removeClass("active")
    } else {
      $(".editor-content").addClass("fullscreen");
      $(".btn-fullscreen").addClass("active")
    }
  })

  $('#saveBtn').on('click', submit);
</script>

<style type="text/css">
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

  #editor {
    height:512px;
  }
</style>
