<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of FenixEdu CMS.

    FenixEdu CMS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu CMS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://fenixedu.com/cms/permissions" prefix="permissions" %>
<%@ taglib uri="http://fenixedu.org/taglib/intersection" prefix="modular" %>

${portal.toolkit()}

<c:if test="${builder.systemBuilder}">
    <script>
        // autocheck roleTemplate with default role
        $(function(){
            $("#defaultRole").change(function() {
                var selId = $( this ).val();
                $(".roleTemplate").each(function (index, element) {
                    if(selId  == $( this ).val()){
                        $( this ).prop('checked', true);

                        $( this ).attr('disabled',true);
                    } else {
                        $( this ).attr('disabled',false);
                    }
                })
            });

            $("#defaultRole").trigger('change');
        })();
    </script>
</c:if>


<div class="page-header">
    <h1>Site Builders
        <small>
            <ol class="breadcrumb">
                <a href="${pageContext.request.contextPath}/cms/sites">Content Manager</a>
                <a href="${pageContext.request.contextPath}/cms/builders">Site Builders</a>
            </ol>
        </small>
    </h1>
</div>

<div class="panel panel-default">
    <div class="panel-heading">Edit builder</div>
    <div class="panel-body">
            <form class="form-horizontal" action="${pageContext.request.contextPath}/cms/builders/${builder.slug}" method="post" role="form">
                ${csrf.field()}
                <div class="form-group">
                    <label for="slug" class="col-sm-2 control-label"><spring:message code="page.edit.label.slug"/></label>
                    <div class="col-sm-10">
                        <input type="text" name="newSlug" class="form-control" id="slug" value="${builder.slug}">
                        <c:if test="${emptyName !=null }"><p class="text-danger"><spring:message code='site.create.error.emptyName'/></p>
                        </c:if>
                    </div>
                </div>
                <div class="form-group">
                    <label for="theme" class="col-sm-2 control-label">Theme</label>
                    <div class="col-sm-10">
                        <select name="theme" id="theme" class="form-control">
                            <option value="${null}">&lt; None &gt;</option>
                            <c:forEach var="theme" items="${themes}">
                                <option value="${theme.type}" ${theme == builder.theme ? 'selected' : ''}>${theme.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label for="folder" class="col-sm-2 control-label">Tag</label>

                    <div class="col-sm-10">
                        <select name="folder" id="folder" class="form-control">
                            <option value>--</option>

                            <c:forEach items="${folders}" var="f">
                                <option value="${f.externalId}">${f.functionality.description.content}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label for="embedded" class="col-sm-2 control-label"><spring:message code="label.embedded"/></label>
                    <div class="col-sm-2">
                        <div class="switch switch-success">
                            <label for="embedded"><input type="checkbox" id="embedded" value="${bulder.embedded}" ${builder.embedded ? 'checked' : ''} \>Embedded</label>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label for="published" class="col-sm-2 control-label"><spring:message code="label.published"/></label>
                    <div class="col-sm-10">
                        <div class="switch switch-success">
                            <label for="published"><input type="checkbox" id="published" value="${bulder.published}" ${builder.published ? 'checked' : ''} \>Published</label>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label for="viewGroup" class="col-sm-2 control-label"><spring:message code="label.view.group"/></label>
                    <div class="col-sm-10">
                        <div class="switch switch-success">
                            <input bennu-group name="viewGroup" type="text" allow="public,users,managers,custom"  value='${builder.canViewGroup.expression}'/>
                        </div>
                    </div>
                </div>
                <c:if test="${builder.systemBuilder}">
                    <div class="form-group">
                        <label for="defaultRole" class="col-sm-2 control-label"><spring:message code="label.default.role"/></label>
                        <div class="col-sm-10">
                            <select id="defaultRole" name="defaultRole" class="form-control">
                                <c:forEach items="${roles}" var="role">
                                    <c:choose>
                                        <c:when test="${builder.defaultRoleTemplate.externalId eq role.externalId}">
                                            <option value="${role.externalId}" selected>${role.name.content}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${role.externalId}">${role.name.content}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </c:if>

                <div class="form-group">
                    <label for="roles" class="col-sm-2 control-label"><spring:message code="label.roles"/></label>
                    <div class="col-sm-10">
                        <div class="row">
                            <div class="col-lg-8 col-sm-12">
                                <div class="row">
                                    <c:forEach items="${roles}" var="role">
                                        <div class="col-sm-3">
                                            <label for="roles-${role.name.content}">
                                                <input type="checkbox" class="roleTemplate" name="roles" value="${role.externalId}" ${builder.roleTemplate.contains(role) ? 'checked' : ''}>
                                                    ${role.name.content}
                                            </label>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <input class="btn btn-primary" type="submit" value="Save">
                <a href="${pageContext.request.contextPath}/cms/builders" class="btn btn-default">Cancel</a>
            </form>
        </div>
    </div>
</div>

