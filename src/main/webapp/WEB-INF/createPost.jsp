<h1>Creating new Post</h1>
<p class="small">Site: <strong>${site.name.content}</strong>  </p>
<form class="form-horizontal" action="" method="post" role="form">
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">Name</label>
    <div class="col-sm-10">
      <input type="text" name="name" class="form-control" id="inputEmail3" placeholder="Name">
    </div>
  </div>

  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">Body</label>
    <div class="col-sm-10">
      <textarea name="body" class="form-control" rows="3"></textarea>
    </div>
  </div>

  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default btn-primary">Create</button>
    </div>
  </div>
</form>