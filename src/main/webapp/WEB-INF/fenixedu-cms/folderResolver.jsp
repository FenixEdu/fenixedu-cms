<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1>
	Resolver - ${folder.functionality.title.content}
</h1>

<p>
	<a href="${pageContext.request.contextPath}/cms/folders" class="btn btn-default">« <spring:message code="action.back"/></a>
	<button class="btn btn-success" id="saveBtn"><spring:message code="action.save" /></button>
</p>

<p class="alert alert-danger" id="error" style="display: none">
</p>

<p>
	<pre id="editor">${folder.resolver.code}</pre>
</p>

<script src="${pageContext.request.contextPath}/static/js/ace/ace.js" type="text/javascript" charset="utf-8"></script>

<style>
	#editor {
		height: 400px;
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
				$("#error").show(); $("#error").html(data.responseText);
             });
    }

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
