<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<h1><spring:message code="page.edit.title" /></h1>
<h2>${page.name.content}</h2>
<p class="small"><spring:message code="page.edit.label.site" />: <strong>${site.name.content}</strong>  </p>
<form class="form-horizontal" action="" method="post" role="form">
  
  <div class="${emptyName ? "form-group has-error" : "form-group"}">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="page.edit.label.slug"/></label>

        <div class="col-sm-5">
            <div class="input-group">

                <span class="input-group-addon"><code>/${site.baseUrl}/</code></span>
                <input required type="text" name="slug" class="form-control" id="inputEmail3"
                       placeholder="<spring:message code="page.edit.label.slug" />" value='${page.slug}' \>
            </div>
        </div>

    </div>

  <div class="${emptyName ? "form-group has-error" : "form-group"}">
    <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="page.edit.label.name" /></label>
    <div class="col-sm-10">
      <input bennu-localized-string type="text" name="name" id="inputEmail3" placeholder="<spring:message code="page.edit.label.name" />" value='${page.name.json()}' \>
      <c:if test="${emptyName != null}"><p class="text-danger"><spring:message code="page.edit.error.emptyName" /></p></c:if>
    </div>
  </div>

  

  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="page.edit.label.template" /></label>
    <div class="col-sm-10">
      <select name="template" class="form-control" id="tempate">
        <option value="null">-</option>
        <c:forEach var="i" items="${site.theme.templatesSet}">
          <option value="${i.type}" ${i == page.template ? 'selected' : ''}>${i.name}</option>
        </c:forEach>
      </select>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.save" /></button>
    </div>
  </div>
</form>
<hr>
<h3><spring:message code="page.edit.label.pageComponents" />:</h3>
<p>
<div class="btn-group">
    <button type="button" class="btn btn-default dropdown-toggle btn-sm" data-toggle="dropdown">
      <spring:message code="page.edit.label.addComponent" />
      <span class="caret"></span>
    </button>
    <ul class="dropdown-menu">
      <li>
        <form action="${pageContext.request.contextPath}/cms/components/${site.slug}/${page.slug}/createComponent" method="post">
          <input type="hidden" name="componentType" value="viewPost" />
        </form>
        <a onclick="$(this).prev().submit()" href="#"><spring:message code="page.edit.label.viewPost" /></a>
      </li>
      <li>
        <form action="${pageContext.request.contextPath}/cms/components/${site.slug}/${page.slug}/createComponent" method="post">
          <input type="hidden" name="componentType" value="listCategories" />
        </form>
        <a onclick="$(this).prev().submit()" href="#"><spring:message code="page.edit.label.listOfCategories" /></a>
      <li>
        <form action="${pageContext.request.contextPath}/cms/components/${site.slug}/${page.slug}/createComponent" method="post">
          <input type="hidden" name="componentType" value="listPost" />
        </form>
        <a onclick="$(this).prev().submit()" href="#"><spring:message code="page.edit.label.listOfPosts" /></a>
      </li>
      <li><a data-toggle="modal" data-target="#listCategoryPosts" href="#"><spring:message code="page.edit.label.listOfPostsByCategory" /></a></li>
      <li><a data-toggle="modal" data-target="#staticPost" href="#"><spring:message code="page.edit.label.staticPost" /></a></li>
      <li><a data-toggle="modal" data-target="#menu" href="#"><spring:message code="page.edit.label.menu" /></a></li>
    </ul>
  </div>
</p>

<c:choose>
      <c:when test="${page.componentsSet.size() == 0}">
      <p><spring:message code="page.edit.label.emtpySiteMenus" /></p>
      </c:when>

      <c:otherwise>
        <table class="table table-striped table-bordered">
          <thead>
            <tr>
              <th><spring:message code="page.edit.label.name" /></th>
              <th><spring:message code="page.edit.label.createdBy" /></th>
              <th><spring:message code="page.edit.label.creationDate" /></th>
              <th><spring:message code="page.edit.label.operations" /></th>
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
                <a href="#" class="btn btn-danger btn-sm" onclick="document.getElementById('deleteComponentForm').submit();"><spring:message code="action.delete" /></a>
               	<form id="deleteComponentForm" action="${pageContext.request.contextPath}/cms/components/${site.slug}/${page.slug}/deleteComponent/${m.getExternalId()}" method="POST"></form>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
</c:choose>

<div class="modal fade" id="listCategoryPosts" tabindex="-1" role="dialog" aria-labelledby="listCategoryPosts" aria-hidden="true">
  <div class="modal-dialog">
    <form action="${pageContext.request.contextPath}/cms/components/${site.slug}/${page.slug}/createComponent" method="post">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="listCategoryPostsLabel"><spring:message code="page.edit.label.listOfPostsByCategory" /></h4>
      </div>
      <div class="modal-body">
          <input type="hidden" name="componentType" value="listCategoryPosts" />

          <div class="form-group">
            <label class="control-label" for="inputSuccess1"><spring:message code="page.edit.label.category" /></label>
            <select name="catSlug">
              <option value="null">&lt; <spring:message code="page.edit.label.dynamic" /> &gt;</option>
              <c:forEach var="c" items="${site.categoriesSet}">
                <option value="${ c.slug }">${ c.name.content }</option>
              </c:forEach>
            </select>
          </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="action.close" /></button>
        <button type="submit" class="btn btn-primary"><spring:message code="action.save" /></button>
      </div>
      </form>
    </div>
  </div>
</div>

<div class="modal fade" id="menu" tabindex="-1" role="dialog" aria-labelledby="menu" aria-hidden="true">
  <div class="modal-dialog">
    <form action="${pageContext.request.contextPath}/cms/components/${site.slug}/${page.slug}/createComponent" method="post">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="menuLabel"><spring:message code="action.add.menu" /></h4>
      </div>
      <div class="modal-body">
          <input type="hidden" name="componentType" value="menu" />

          <div class="form-group">
            <label class="control-label" for="inputSuccess1"><spring:message code="page.edit.label.menu" /></label>
            <select name="menuOid">
              <c:forEach var="m" items="${site.menusSet}">
                <option value="${ m.oid }">${ m.name.content }</option>
              </c:forEach>
            </select>
          </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="action.close" /></button>
        <button type="submit" class="btn btn-primary"><spring:message code="action.save" /></button>
      </div>
      </form>
    </div>
  </div>
</div>

<div class="modal fade" id="staticPost" tabindex="-1" role="dialog" aria-labelledby="staticPost" aria-hidden="true">
  <div class="modal-dialog">
    <form action="${pageContext.request.contextPath}/cms/components/${site.slug}/${page.slug}/createComponent" method="post">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="staticPostLabel"><spring:message code="page.edit.label.addStaticPost" /></h4>
      </div>
      <div class="modal-body">
          <input type="hidden" name="componentType" value="staticPost" />

          <div class="form-group">
            <label class="control-label" for="inputSuccess1"><spring:message code="page.edit.label.post" /></label>
            <select name="postSlug">
              <c:forEach var="m" items="${site.postSet}">
                <option value="${ m.slug }">${ m.name.content }</option>
              </c:forEach>
            </select>
          </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="action.close" /></button>
        <button type="submit" class="btn btn-primary"><spring:message code="action.save" /></button>
      </div>
      </form>
    </div>
  </div>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
<script src="${pageContext.request.contextPath}/static/js/toolkit.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/toolkit/toolkit.css"/>
