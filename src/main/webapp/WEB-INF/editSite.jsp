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

                <div class="col-sm-2">
                    <div class="input-group">

                        <span class="input-group-addon"><code>/</code></span>
                        <input required type="text" name="newSlug" class="form-control" id="inputEmail3"
                               placeholder="<spring:message code="site.edit.label.slug" />" value='${site.slug}' \>
                    </div>
                </div>

            </div>

            <div class="${emptyName ? "form-group has-error" : "form-group"}">
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.edit.label.name"/></label>

                <div class="col-sm-10">
                    <input bennu-localized-string required type="text" name="name" class="form-control" id="inputEmail3"
                           placeholder="<spring:message code="site.edit.label.name" />" value='${site.name.json()}' \>
                    <c:if test="${emptyName != null}"><p class="text-danger"><spring:message
                            code="site.edit.error.emptyName"/></p></c:if>
                </div>
            </div>

            <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message
                        code="site.edit.label.description"/></label>

                <div class="col-sm-10">
                    <textarea bennu-localized-string required name="description" class="form-control"
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
                <label for="inputEmail3" class="col-sm-2 control-label"><spring:message
                        code="site.create.label.published"/></label>

                <div class="col-sm-2">
                    <input name="published" type="checkbox" value="true" ${site.published ? "checked='checked'" : ""}>

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

            </div>
        </div>
    </form>
</div>

<script src="${pageContext.request.contextPath}/static/js/toolkit.js"></script>

