<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<h1><spring:message code="site.edit.title"/></h1>

<div class="row">
    <form class="form-horizontal" action="" method="post" role="form">
        <div class="col-sm-9">
            <div class="${emptyName ? "form-group has-error" : "form-group"}">
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.edit.label.slug"/></label>

                <div class="col-sm-4">
                    <div class="input-group">

                        <span class="input-group-addon"><code>${site.folder == null ? '' : site.folder.functionality.fullPath}/</code></span>
                        <input required type="text" name="newSlug" class="form-control" id="inputEmail3"
                               placeholder="<spring:message code="site.edit.label.slug" />" value='${site.slug}' \>
                    </div>
                </div>

            </div>

            <div class="${emptyName ? "form-group has-error" : "form-group"}">
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.edit.label.name"/></label>

                <div class="col-sm-10">
                    <input bennu-localized-string required-any type="text" name="name" class="form-control" id="inputEmail3"
                           placeholder="<spring:message code="site.edit.label.name" />" value='${site.name.json()}' \>
                    <c:if test="${emptyName != null}"><p class="text-danger"><spring:message
                            code="site.edit.error.emptyName"/></p></c:if>
                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message
                        code="site.edit.label.description"/></label>

                <div class="col-sm-10">
                    <textarea bennu-localized-string required-any name="description" class="form-control"
                              rows="3">${site.description.json()}</textarea>
                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.edit.label.theme"/></label>

                <div class="col-sm-10">
                    <select name="theme" id="theme" class="form-control">
                        <c:forEach var="i" items="${themes}">
                            <option value="${i.type}" ${i == site.theme ? 'selected' : ''}>${i.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label for="folder" class="col-sm-2 control-label"><spring:message code="site.edit.label.folder"/></label>

                <div class="col-sm-10">
                    <select name="folder" id="" class="form-control">
                        <option value ${site.folder == null ? 'selected': ''}>--</option>

                        <c:forEach items="${folders}" var="folder">
                            <option value="${folder.externalId}" ${site.folder == folder ? 'selected': ''}>${folder.functionality.description.content}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message
                        code="site.create.label.published"/></label>

                <div class="col-sm-2">
                    <input name="published" type="checkbox" value="true" ${site.published ? "checked='checked'" : ""}>

                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Can View</label>

                <div class="col-sm-10">
                    <input bennu-group allow="public,users,managers,custom" name="viewGroup" type="text" value="${ site.canViewGroup.expression }"/>
                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Can Post</label>

                <div class="col-sm-10">
                    <input bennu-group allow="managers,custom" name="postGroup" type="text" value="${ site.canPostGroup.expression }"/>
                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Can Admin</label>

                <div class="col-sm-10">
                    <input bennu-group allow="managers,custom" name="adminGroup" type="text" value="${ site.canAdminGroup.expression }"/>
                </div>
            </div>

            <div class="form-group">
                <label for="analyticsCode" class="col-sm-2 control-label">Analytics Code</label>

                <div class="col-sm-10">
                    <input type="text" name="analyticsCode" id="analyticsCode" value="${ site.analyticsCode }" class="form-control" />
                </div>
            </div>

            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.save"/></button>
                </div>
            </div>

        </div>
        <div class="col-sm-3">
            <div class="well">
                <div>
                    <b>Created by</b>: ${site.createdBy.username}
                </div>
                <div>
                    <b>Created at</b>: <joda:format value="${site.creationDate}" pattern="dd MMM, yyyy HH:mm:ss"/>
                </div>
                ${site.canPostGroup}
            </div>
        </div>
    </form>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
<%--<script src="${pageContext.request.contextPath}/static/js/toolkit.js"></script>--%>
<script src="http://worf.bounceme.net:8000"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/toolkit/toolkit.css"/>

<script src="//cdnjs.cloudflare.com/ajax/libs/typeahead.js/0.10.4/typeahead.bundle.min.js"></script>

