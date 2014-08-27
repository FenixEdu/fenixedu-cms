<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1><spring:message code="page.manage.title"/></h1>

<p class="small"><spring:message code="page.manage.label.site"/>: <a href="../"><strong>${site.name.content}</strong></a></p>

<p>
    <a href="${pageContext.request.contextPath}/cms/pages/${site.slug}/create" class="btn btn-default btn-primary"><spring:message
            code="page.manage.label.createPage"/></a>
    <a href="#" data-toggle="modal" data-target="#defaultModal" class="btn btn-default"><spring:message
            code="page.manage.label.change.default.page"/></a>
</p>

<c:choose>
    <c:when test="${pages.size() == 0}">
        <p><spring:message code="page.manage.label.emptyPages"/></p>
    </c:when>

    <c:otherwise>
        <table class="table table-striped table-bordered">
            <thead>
            <tr>
                <th><spring:message code="page.manage.label.name"/></th>
                <th><spring:message code="page.manage.label.createdBy"/></th>
                <th><spring:message code="page.manage.label.creationDate"/></th>
                <th><spring:message code="page.manage.label.operations"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="p" items="${pages}">
                <tr>
                    <td>
                        <h5><a target="_blank"
                               href="${pageContext.request.contextPath}/${p.site.slug}/${p.slug}">${p.getName().getContent()}</a>
                            <c:if test="${p.site.initialPage == p}">
                                <span class="label label-success"><spring:message code="site.manage.label.default"/></span>
                            </c:if>
                        </h5>

                        <div>
                            <small><spring:message code="page.manage.label.url"/>:
                                <c:choose>
                                    <c:when test="${p.slug != ''}">
                                        <code>${p.getSlug()}</code>
                                    </c:when>
                                    <c:otherwise>
                                        <i>Empty</i>
                                    </c:otherwise>
                                </c:choose>
                            </small>
                        </div>
                    </td>
                    <td>${p.createdBy.username}</td>
                    <td><joda:format value="${p.getCreationDate()}" pattern="MMM dd, yyyy"/></td>
                    <td>
                        <div class="btn-group">
                            <c:choose>
                                <c:when test="${p.slug != ''}">
                                    <a href="${pageContext.request.contextPath}/cms/pages/${p.site.slug}/${p.slug}/edit"
                                       class="btn btn-sm btn-default"><spring:message code="action.edit"/></a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/cms/pages/${p.site.slug}/--**--/edit"
                                       class="btn btn-sm btn-default"><spring:message code="action.edit"/></a>
                                </c:otherwise>
                            </c:choose>
                            <a href="${pageContext.request.contextPath}/${p.site.slug}/${p.slug}" class="btn btn-sm btn-default"
                               target="_blank"><spring:message code="action.link"/></a>
                            <a href="#" class="btn btn-danger btn-sm"
                               onclick="document.getElementById('deletePageForm').submit();"><spring:message
                                    code="action.delete"/></a>

                            <form id="deletePageForm"
                                  action="${pageContext.request.contextPath}/cms/pages/${p.site.slug}/${p.slug}/delete"
                                  method="POST"></form>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<div class="modal fade" id="defaultModal" tabindex="-1" role="dialog" aria-hidden="true">
    <form action="${site.slug}/defaultPage" class="form-horizontal" method="post">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                            class="sr-only">Close</span></button>
                    <h4><spring:message code="action.change.default"/></h4>
                </div>
                <div class="modal-body">

                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="label.page"/>:</label>

                        <div class="col-sm-10">
                            <select name="page" class="form-control">
                                <option value="---null---">-</option>
                                <c:forEach var="p" items="${pages}">
                                    <option value="${p.slug}">${p.name.content}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><spring:message code="label.save"/></button>
                </div>
            </div>
        </div>
    </form>
</div>