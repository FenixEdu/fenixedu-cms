<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<h1>Editing</h1>
<h2>${page.name.content}</h2>
<p class="small">Site: <strong>${site.name.content}</strong>  </p>
<form class="form-horizontal" action="" method="post" role="form">
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">Name</label>
    <div class="col-sm-10">
      <input type="text" name="name" class="form-control" id="inputEmail3" placeholder="Name" value="${page.name.content}" \>
    </div>
  </div>
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">Slug</label>
    <div class="col-sm-10">
      <input type="text" name="slug" class="form-control" id="inputEmail3" placeholder="Slug" value="${page.slug}" \>
    </div>
  </div>
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">Template</label>
    <div class="col-sm-10">
      <select name="template" id="tempate">
        <option value="null">-</option>
        <c:forEach var="i" items="${site.theme.templatesSet}">
          <option value="${i.type}" ${i == page.template ? 'selected' : ''}>${i.name}</option>
        </c:forEach>
      </select>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default btn-primary">Save</button>
    </div>
  </div>
</form>

<h3>Components in this page:</h3>
<p>
<div class="btn-group">
    <button type="button" class="btn btn-default dropdown-toggle btn-sm" data-toggle="dropdown">
      Add a new Component
      <span class="caret"></span>
    </button>
    <ul class="dropdown-menu">
      <li>
        <form action="createComponent" method="post">
          <input type="hidden" name="componentType" value="viewPost" />
        </form>
        <a onclick="$(this).prev().submit()" href="#">View Post</a>
      </li>
      <li>
        <form action="createComponent" method="post">
          <input type="hidden" name="componentType" value="listCategories" />
        </form>
        <a onclick="$(this).prev().submit()" href="#">List of Categories</a>
      <li>
        <form action="createComponent" method="post">
          <input type="hidden" name="componentType" value="listPost" />
        </form>
        <a onclick="$(this).prev().submit()" href="#">List of Posts</a>
      </li>
      <li><a data-toggle="modal" data-target="#listCategoryPosts" href="#">List of Posts by Category</a></li>
      <li><a data-toggle="modal" data-target="#staticPost" href="#">Static Post</a></li>
      <li><a data-toggle="modal" data-target="#menu" href="#">Menu</a></li>
    </ul>
  </div>
</p>

<c:choose>
      <c:when test="${page.componentsSet.size() == 0}">
      <p>There are no menus created for this site.</p>
      </c:when>

      <c:otherwise>
        <table class="table table-striped table-bordered">
          <thead>
            <tr>
              <th>Name</th>
              <th>Created By</th>
              <th>Creation Date</th>
              <th>Operations</th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="m" items="${page.componentsSet}">
            <tr>
              <td>
                <h5>${m.name}</h5>
                <div><small>${m.description}</code></small></div>
              </td>
              <td>${m.createdBy.username}</td>
              <td><joda:format value="${m.getCreationDate()}" pattern="MMM dd, yyyy"/></td>
              <td>
                <a href="deleteComponent/${m.getExternalId()}" class="btn btn-danger btn-sm">Delete</a>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>

<div class="modal fade" id="listCategoryPosts" tabindex="-1" role="dialog" aria-labelledby="listCategoryPosts" aria-hidden="true">
  <div class="modal-dialog">
    <form action="createComponent" method="post">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="listCategoryPostsLabel">Add "List of Posts by Category"</h4>
      </div>
      <div class="modal-body">
          <input type="hidden" name="componentType" value="listCategoryPosts" />

          <div class="form-group">
            <label class="control-label" for="inputSuccess1">Category</label>
            <select name="catSlug">
              <option value="null">&lt; Dynamic, use request parameter &gt;</option>
              <c:forEach var="c" items="${site.categoriesSet}">
                <option value="${ c.slug }">${ c.name.content }</option>
              </c:forEach>
            </select>
          </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="submit" class="btn btn-primary">Save changes</button>
      </div>
      </form>
    </div>
  </div>
</div>

<div class="modal fade" id="menu" tabindex="-1" role="dialog" aria-labelledby="menu" aria-hidden="true">
  <div class="modal-dialog">
    <form action="createComponent" method="post">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="menuLabel">Add "Menu"</h4>
      </div>
      <div class="modal-body">
          <input type="hidden" name="componentType" value="menu" />

          <div class="form-group">
            <label class="control-label" for="inputSuccess1">Menu</label>
            <select name="menuOid">
              <c:forEach var="m" items="${site.menusSet}">
                <option value="${ m.oid }">${ m.name.content }</option>
              </c:forEach>
            </select>
          </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="submit" class="btn btn-primary">Save changes</button>
      </div>
      </form>
    </div>
  </div>
</div>

<div class="modal fade" id="staticPost" tabindex="-1" role="dialog" aria-labelledby="staticPost" aria-hidden="true">
  <div class="modal-dialog">
    <form action="createComponent" method="post">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="staticPostLabel">Add "Static Post"</h4>
      </div>
      <div class="modal-body">
          <input type="hidden" name="componentType" value="staticPost" />

          <div class="form-group">
            <label class="control-label" for="inputSuccess1">Post</label>
            <select name="postSlug">
              <c:forEach var="m" items="${site.postSet}">
                <option value="${ m.slug }">${ m.name.content }</option>
              </c:forEach>
            </select>
          </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="submit" class="btn btn-primary">Save changes</button>
      </div>
      </form>
    </div>
  </div>
</div>