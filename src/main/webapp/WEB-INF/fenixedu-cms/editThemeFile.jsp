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
<style>
#editor { 
  height:512px;
}
</style>
<h3><spring:message code="file.edit.title" /></h3>

<p>
  <b><spring:message code="file.edit.label.path" />:</b> <code>${ file.fullPath }</code>
</p>

<p>
<input type="hidden" name="content"/>
<div class="btn-toolbar" role="toolbar">
  <div class="btn-group">
    <a href="<c:url value="${ linkBack }" />" type="button" class="btn btn-sm btn-default"><span class="glyphicon glyphicon-chevron-left"></span> <spring:message code="action.back" /></a>
  </div>
  <div class="btn-group">
    <button type="button" id="saveBtn" class="btn btn-sm btn-success">
      <span id="txt"><spring:message code="action.save" /></span> <span class="glyphicon glyphicon glyphicon-save"></span>
    </button>
    <!-- <button type="button" class="btn btn-sm btn-default"><spring:message code="action.move.file" /></button> -->
  </div>
  <div class="btn-group">
    <!-- <button type="button" class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown"><spring:message code="file.edit.label.highlight" /> <span class="caret"></span></button>
    <ul class="dropdown-menu" role="menu">
        <li><a href="#"><spring:message code="file.edit.label.text" /></a></li>
        <li><a href="#"><spring:message code="file.edit.label.html" /></a></li>
        <li><a href="#"><spring:message code="file.edit.label.css" /></a></li>
        <li><a href="#"><spring:message code="file.edit.label.javascript" /></a></li>
      </ul> -->
  </div>
</div>
</p>

<pre id="editor"><c:out value="${content}"/></pre>

<script>
    var editor = ace.edit("editor");

    var submit = function() {
      $("#saveBtn").attr('disabled', true);
      $("#saveBtn #txt").html('<spring:message code="action.saving"/>');
      $.ajax('${pageContext.request.contextPath}/cms/themes/${theme.type}/editFile/${file.fullPath}', 
             { data: editor.getValue(), type: 'PUT' }).done(function () {
                $("#saveBtn").attr('disabled', false); $("#saveBtn #txt").html('<spring:message code="action.save" />');
             });
    }

    editor.setFontSize(13);
    editor.setTheme("ace/theme/clouds");
    editor.getSession().setMode("ace/mode/${ type }");
    editor.setHighlightActiveLine(false);
    editor.setShowPrintMargin(false);
    editor.commands.addCommand({
        name: 'save',
        bindKey: {win: 'Ctrl-S',  mac: 'Command-S'},
        exec: submit
    });


    $('#saveBtn').on('click', submit);
</script>
