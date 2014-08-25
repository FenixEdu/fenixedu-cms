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
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.add"/></button>
        </div>
    </div>
</form>