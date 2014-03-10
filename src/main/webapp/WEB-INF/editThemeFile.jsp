<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<script src="${pageContext.request.contextPath}/static/js/ace/ace.js" type="text/javascript" charset="utf-8"></script>
<style>
#editor { 
  height:512px;
}
</style>
<h3>Edit File</h3>


<p>
  <b>Path:</b> <code>${ file.displayName }</code>
</p>

<p>
<div class="btn-toolbar" role="toolbar">
  <div class="btn-group">
    <a href="<c:url value="${ linkBack }" />" type="button" class="btn btn-sm btn-default"><span class="glyphicon glyphicon-chevron-left"></span> Back</a>
  </div>
  <div class="btn-group">
    <button type="button" class="btn btn-sm btn-success">Save <span class="glyphicon glyphicon glyphicon-save"></span></button>
    <button type="button" class="btn btn-sm btn-default">Move File</button>
  </div>
  <div class="btn-group">
    <button type="button" class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown">Highlight <span class="caret"></span></button>
    <ul class="dropdown-menu" role="menu">
        <li><a href="#">Text</a></li>
        <li><a href="#">HTML</a></li>
        <li><a href="#">CSS</a></li>
        <li><a href="#">JavaScript</a></li>
      </ul>
  </div>
</div>
</p>

<pre id="editor"><c:out value="${content}"/></pre>

<script>
    var editor = ace.edit("editor");
    editor.setFontSize(14)
    editor.setTheme("ace/theme/solarized_dark");
    editor.getSession().setMode("ace/mode/${ type }");
</script>
