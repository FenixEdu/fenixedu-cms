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
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="${pageContext.request.contextPath}/static/js/moment.js" type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/static/js/jsdiff.js" type="text/javascript" charset="utf-8"></script>
<c:set var="previewUrl" value="${page != null ? page.getAddress() : post != null ? post.getAddress() : '#'}" />

${portal.toolkit()}

<c:set var="isPageVersion" value="${page != null}" />
<c:set var="backUrl" value="${page != null ? page.getEditUrl() : post != null ? post.getEditUrl() : '#'}" />

<div class="page-header">
    <h1>${site.name.content}</h1>

    <h2><small><a href="${backUrl}">Edit - ${post.name.content}</small></a></h2>

    <div class="row">
        <div class="col-sm-12">
            <button type="button" class="btn btn-primary" id="update-btn">
                <span class="glyphicon glyphicon-floppy-disk"></span> Update
            </button>
            <a href="${backUrl}" class="btn btn-default">
                <span class="glyphicon glyphicon-edit"></span> Edit
            </a>
            <a href="${pageContext.request.contextPath}/cms/versions/${site.slug}/${post.slug}" class="btn btn-default">
                <span class="glyphicon glyphicon-time"></span> Versions
            </a>
            <a href="#" class="btn btn-default disabled">
                <span class="glyphicon glyphicon-time"></span> Metadata
            </a>
            <a href="${previewUrl}" target="_blank" class="btn btn-default">
                <span class="glyphicon glyphicon-link"></span> Link
            </a>
        </div>
    </div>

    <div class="row">

        <div class="col-sm-12">
            <p><pre id="metadata" type="application/json" bennu-code-editor></pre></p>
        </div>
        <form method="post" id="update-metadata-form">
            ${csrf.field()}
            <input name="metadata" type="text" class="hidden" value='<c:out value="${metadata}"></c:out>'>
        </form>
    </div>

    <script type="application/javascript">
        var initialMetadataJson = '${metadata}'

        function updateMetadataAsync() {
            setTimeout(function(){
                var handler = $('#metadata').data('handler');
                if(handler) {
                    updateMetadataSync(handler);
                } else {
                    updateMetadataAsync();
                }
            }, 500);
        }

        function updateMetadataSync(handler) {
            handler.set(JSON.stringify(JSON.parse(initialMetadataJson), null, 2));

            function isValidJson() {
                try {
                    JSON.parse(handler.get());
                    return true;
                } catch(e) {
                    return false;
                }
            }

            $('#update-btn').click(function(){
                $('#update-metadata-form').submit();
            });

            $(window).bind('keydown', function(event) {
                if (isValidJson() && event.ctrlKey || event.metaKey) {
                    switch (String.fromCharCode(event.which).toLowerCase()) {
                    case 's':
                        event.preventDefault();
                        $('#update-metadata-form').submit();
                        break;
                    }
                }
            });

            setInterval(function(){
                if(isValidJson()) {
                    $('#update-btn').removeClass('disabled');
                    $('#update-metadata-form [name="metadata"]').val(handler.get());
                } else {
                    $('#update-btn').addClass('disabled');
                }
            }, 200);
        }

        $(document).ready(function(){
            updateMetadataAsync();
        });
    </script>

</div>