<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1><spring:message code="site.create.title"/></h1>

<form class="form-horizontal" action="" method="post" role="form">
    <div class="${emptyName ? "form-group has-error" : "form-group"}">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.create.label.name"/></label>

        <div class="col-sm-10">
            <input bennu-localized-string required type="text" name="name" class="form-control" id="inputEmail3"
                   placeholder="<spring:message code="site.create.label.name"/>">
            <c:if test="${emptyName !=null }"><p class="text-danger"><spring:message code="site.create.error.emptyName"/></p>
            </c:if>
        </div>
    </div>

    <div class="form-group">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.create.label.description"/></label>

        <div class="col-sm-10">
            <!-- <textarea bennu-localized-string required name="description"
                      placeholder="<spring:message code="site.create.label.description"/>" class="form-control"
                      rows="3"></textarea> -->
            <textarea bennu-localized-string bennu-html-editor required-any toolbar="size,style,lists,align,links,image,undo,voice,fullscreen"
                      name="description"
                      placeholder="<spring:message code="site.create.label.description"/>" class="form-control"
                      rows="3"></textarea>
        </div>
    </div>

    <div class="form-group">
        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="site.create.label.published"/></label>

        <div class="col-sm-10">
            <input name="published" type="checkbox">
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
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.create"/></button>
        </div>
    </div>
</form>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
<!-- <script src="${pageContext.request.contextPath}/static/js/toolkit.js"></script> -->
<script src="http://worf.bounceme.net:8000/"></script>
<style>
    .fullscreen{
        height:100%;
        width: 100%;
        overflow: scroll;
    }
    .fullscreen .bennu-html-editor-editor{
        width: 800px;
        margin: auto;
        margin-top: 70px;
        border: 1px dashed #ccc !important;
        border-radius: 0px;
    }

    .fullscreen .bennu-html-editor-editor{
        height:100%;
    }

    .fullscreen .bennu-html-editor-editor:focus{
        box-shadow: none !important;
        border: 1px dashed #ccc !important;
    }
    .fullscreen .bennu-localized-string-group{
        display: inline;

    }
    .fullscreen .bennu-localized-string-group button{
        padding: 5px 10px;
        font-size: 12px;
        line-height: 1.5;
        border-radius: 3px;
        margin-right: 5px;
    }
    .fullscreen .bennu-html-editor-tools{
        background: #f1f1f1;
        border-bottom: 1px solid #E2E2E2;
        position:fixed;
        top:0;
        left:0;
        width:100%;
        height:50px;
        padding-top:10px;
        padding-left: 20px;

    }
</style>
