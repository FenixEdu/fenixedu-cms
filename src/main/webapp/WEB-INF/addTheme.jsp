<h1>Add Theme</h1>

<div class="alert alert-info">
  <strong>New Theme:</strong> Please refer to the documentation when adding new themes.
</div>

<form class="form-horizontal" enctype="multipart/form-data" action="" method="post" role="form">
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">File:</label>
    <div class="col-sm-10">
      <input type="file" name="uploadedFile" class="form-control" id="inputEmail3" placeholder="Name">
    </div>
  </div>

    <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">Is a Default Theme:</label>
    <div class="col-sm-10">
      <input type="checkbox" name="isDefault" class="form-control" id="inputEmail3" value="true"/>
      <input type="hidden" name="isDefault" class="hide" id="inputEmail3" value="false"/>
    </div>
  </div>

  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default btn-primary">Add</button>
    </div>
  </div>
</form>