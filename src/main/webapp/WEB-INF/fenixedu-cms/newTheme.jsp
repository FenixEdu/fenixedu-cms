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
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<h1><spring:message code="site.manage.label.newTheme"/></h1>

<form class="form-horizontal" enctype="multipart/form-data" action="" method="post" role="form">
    <div class="form-group">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="theme.new.label.type"/>:</label>
        <div class="col-sm-10">
            <input type="text" name="type" class="form-control" placeholder="Type">
        </div>
    </div>

    <div class="form-group">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="theme.new.label.name"/>:</label>
        <div class="col-sm-10">
            <input type="text" name="name" class="form-control" placeholder="Name">
        </div>
    </div>

    <div class="form-group">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="theme.new.label.description"/>:</label>
        <div class="col-sm-10">
            <textarea name="description" class="form-control" placeholder="Description">

            </textarea>
        </div>
    </div>

    <div class="form-group">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="theme.new.label.extends"/>:</label>
        <div class="col-sm-10">
            <select class="form-control" name="extends" id="">
                <option value="">-</option>
                <c:forEach var="theme" items="${themes}">
                    <option value="${theme.type}">${theme.name}</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.add"/></button>
        </div>
    </div>
</form>