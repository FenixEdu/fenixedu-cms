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

<h1>
	Resolver - ${folder.functionality.title.content}
</h1>

<p>
	<a href="${pageContext.request.contextPath}/cms/folders" class="btn btn-default">« <spring:message code="action.back"/></a>
	<button class="btn btn-success" id="saveBtn"><spring:message code="action.save" /></button>
</p>

<p class="alert alert-danger" id="error" style="display: none"></p>

<p>
    <pre id="editor"><c:out value="${folder.resolver.code}"></c:out></pre>
</p>

<script src="${pageContext.request.contextPath}/static/js/ace/ace.js" type="text/javascript" charset="utf-8"></script>

<style>
	#editor {
		height: 400px;
	}
</style>

<script>
    $.ajaxSetup({headers: {'${csrf.headerName}': '${csrf.token}'}});
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


    $('#saveBtn').on('click', submit);
</script>
