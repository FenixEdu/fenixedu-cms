<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<h1>Add Theme</h1>

<div class="alert alert-info">
	<spring:message code="theme.add.title"/>
</div>

<form class="form-horizontal" enctype="multipart/form-data" action="" method="post" role="form">
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="theme.add.label.file"/>:</label>
    <div class="col-sm-10">
      <input type="file" name="uploadedFile" class="form-control" id="inputEmail3" placeholder="Name">
    </div>
  </div>

    <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="theme.add.label.isDefaultTheme"/>:</label>
    <div class="col-sm-10">
      <input type="checkbox" name="isDefault" class="form-control" id="inputEmail3" value="true"/>
      <input type="hidden" name="isDefault" class="hide" id="inputEmail3" value="false"/>
    </div>
  </div>

  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.add"/></button>
    </div>
  </div>
</form>