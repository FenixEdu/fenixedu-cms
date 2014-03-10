<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h1>Creating new Site</h1>

<form class="form-horizontal" action="" method="post" role="form">
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">Name</label>
    <div class="col-sm-10">
      <input type="text" name="name" class="form-control" id="inputEmail3" placeholder="Name">
    </div>
  </div>
  
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">Description</label>
    <div class="col-sm-10">
      <textarea name="description" placeholder="Description" class="form-control" rows="3"></textarea>
    </div>
  </div>
  
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">Use template</label>
    <div class="col-sm-10">
      <select name="template" id="">
        <option value="null">&lt; Empty Site &gt;</option>

          <c:forEach items="${templates}" var="template">
            <option value="${template.key}">${template.value}</option>
          </c:forEach>
      </select>
    </div>
  </div>

  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default btn-primary">Create</button>
    </div>
  </div>
</form>