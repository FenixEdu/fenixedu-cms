<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h1>Creating new Category</h1>
<p class="small">Site: <strong>${site.name.content}</strong>  </p>
<form class="form-horizontal" action="" method="post" role="form">
  <div class="${emptyName ? "form-group has-error" : "form-group"}">
    <label for="inputEmail3" class="col-sm-2 control-label">Name</label>
    <div class="col-sm-10">
      <input type="text" name="name" class="form-control" id="inputEmail3" placeholder="Name">
      <c:if test="${emptyName}"><p class="text-danger">Please enter a Category name.</p></c:if>
    </div>
  </div>
  
  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default btn-primary">Create</button>
    </div>
  </div>
</form>