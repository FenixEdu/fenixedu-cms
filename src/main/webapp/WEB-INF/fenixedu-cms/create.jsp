<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1><spring:message code="site.create.title"/></h1>

<form class="form-horizontal" action="" method="post" role="form">
    <div class="${emptyName ? "form-group has-error" : "form-group"}">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.create.label.name"/></label>

        <div class="col-sm-10">
            <input bennu-localized-string required-any type="text" name="name" class="form-control" id="inputEmail3"
                   placeholder="<spring:message code="site.create.label.name"/>">
            <c:if test="${emptyName !=null }"><p class="text-danger"><spring:message code="site.create.error.emptyName"/></p>
            </c:if>
        </div>
    </div>

    <div class="form-group">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.create.label.description"/></label>

        <div class="col-sm-10">
            <textarea bennu-localized-string required-any name="description"
                      placeholder="<spring:message code="site.create.label.description"/>" class="form-control"
                      rows="3"></textarea>
        </div>
    </div>

    <div class="form-group">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.create.label.useTemplate"/></label>

        <div class="col-sm-10">
            <select name="template" id="" class="form-control">
                <option value="null">&lt; <spring:message code="site.create.label.emptySite"/> &gt;</option>

                <c:forEach items="${templates}" var="template">
                    <option value="${template.key}">${template.value}</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="form-group">
        <label for="folder" class="col-sm-2 control-label"><spring:message code="site.create.label.folder"/></label>

        <div class="col-sm-10">
            <select name="folder" id="" class="form-control">
                <option value>--</option>

                <c:forEach items="${folders}" var="folder">
                    <option value="${folder.externalId}">${folder.functionality.description.content}</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="form-group">
        <label for="embedded" class="col-sm-2 control-label"><spring:message code="label.embedded"/></label>
        <div class="col-sm-2">
            <input type="checkbox" name="embedded" value="true" />
        </div>
    </div>


    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.create"/></button>
        </div>
    </div>
</form>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
<script src="${pageContext.request.contextPath}/static/js/toolkit.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/toolkit/toolkit.css"/>

