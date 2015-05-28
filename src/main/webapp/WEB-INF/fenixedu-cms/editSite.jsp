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
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
${portal.toolkit()}

<div class="page-header">
    <h1>Settings</h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small></h2>
</div>

<form class="form-horizontal" action="" method="post" role="form">
    <div role="tabpanel">
        <p>
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation" class="active"><a href="#general" aria-controls="general" role="tab" data-toggle="tab">General</a></li>
                <li role="presentation"><a href="#permissions" aria-controls="permissions" role="tab" data-toggle="tab">Permissions</a></li>
                <li role="presentation"><a href="#external" aria-controls="external" role="tab" data-toggle="tab">External Applications</a></li>
            </ul>
        </p>
    </div>
    <div class="tab-content">
        <div role="tabpanel" class="tab-pane active form-horizontal" id="general">
            <div class="row">
                
                    <div class="col-sm-8">
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
                        <c:if test="${googleConnection == null && google == null}">
                        <div class="form-group">
                            <label for="inputEmail3" class="col-sm-2 control-label">Analytics Code</label>

                            <div class="col-sm-10">
                                <input type="text" name="analyticsCode" id="analyticsCode" value="${ site.analyticsCode }"
                                       class="form-control"/>
                            </div>
                        </div>
                        </c:if>
                        <c:if test="${googleConnection == null && google != null}">
                            <input type="hidden" name="analyticsCode" id="analyticsCode" value="${ site.analyticsCode }"
                                       class="form-control"/>
                        </c:if>
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
                            <label class="col-sm-2 control-label">Homepage:</label>

                            <div class="col-sm-10">
                                <select name="initialPageSlug" class="form-control">
                                    <option value="---null---">-</option>
                                    <c:forEach var="p" items="${site.pages}">
                                        <option ${p == site.initialPage ? 'selected' : ''} value="${p.slug}">${p.name.content}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                    </div>
                    <div class="col-sm-4">
                        <div class="panel panel-primary">
                            <div class="panel-heading">Information</div>
                            <div class="panel-body">
                                <dl>
                                    <dt><spring:message code="site.create.label.published"/></dt>
                                    <dd> <input name="published" type="checkbox" value="true" ${site.published ? "checked='checked'" : ""}></dd>
                                    <dt>Created by</dt>
                                    <dd>${site.createdBy.username}</dd>
                                    <dt>Created at</dt>
                                    <dd>${site.creationDate.toString('dd MMM, yyyy HH:mm:ss')}</dd>
                                </dl>
                            </div>
                        </div>

                        <div class="panel panel-danger">
                            <div class="panel-heading"><spring:message code="site.edit.danger.zone" /></div>
                            <div class="panel-body">
                                <p class="help-block">Once you delete a site, there is no going back. Please be certain.</p>
                                <a href="#" data-toggle="modal" data-target="#confirmDeleteModal" class="btn btn-danger">Delete this Site </a>
                            </div>
                        </div>

                    </div>
                
            </div>
        </div>
        <div role="tabpanel" class="tab-pane form-horizontal" id="permissions">
                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label">Can View</label>

                        <div class="col-sm-10">
                            <input bennu-group allow="public,users,managers,custom" name="viewGroup" type="text"
                                   value="${ site.canViewGroup.expression }"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label">Can Post</label>

                        <div class="col-sm-10">
                            <input bennu-group allow="managers,custom" name="postGroup" type="text"
                                   value="${ site.canPostGroup.expression }"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label">Can Admin</label>

                        <div class="col-sm-10">
                            <input bennu-group allow="managers,custom" name="adminGroup" type="text"
                                   value="${ site.canAdminGroup.expression }"/>
                        </div>
                    </div>
        </div>
        <div role="tabpanel" class="tab-pane form-horizontal" id="external">

            <c:if test="${google != null}">
                <h3>Google</h3>

                <c:if test="${googleConnection == null}">
                    <p class="help-block">To use <a href="http://www.google.com/analytics/">Analytics</a>, <a href="https://plus.google.com">Plus</a>, <a href="https://maps.google.com">Maps</a> and other integrationss from Google you need to connect your site with your Google account.</p>

                    <a href="${url}" class="btn btn-lg btn-primary">Connect with Google</a>
                </c:if>

                <c:if test="${googleConnection != null}">
                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label">Analytics Property</label>

                        <div class="col-sm-10">
                            <input type="hidden" name="analyticsCode" id="analyticsCode" value="${ site.analyticsCode }"
                                           class="form-control"/>
                            <input type="hidden" name="accountId" id="accountId" value="${ site.googleAPIConnection.accountId}"
                                           class="form-control"/>
                            <span class="property"></span> <a class="btn btn-xs btn-default" data-toggle="modal" href='#select-property'>Select property</a>
                            <p class="help-block">Integrate this site with Google Analytics so you can receive information about the number of visits and correlate them with your posting history. </p>
                        </div>
                    </div>
                        
                    <script type="text/javascript">
                        var accounts = ${accounts};
                        var accountDir = {}
                        accounts.map(function(e){ accountDir[e.accountId] = e});

                        $(function(){
                            $("#ga-accounts").empty().append("<option value=''>-</option> ");

                            accounts.map(function(e){
                                    $("#ga-accounts").append("<option value='" + e.accountId + "'>" + e.name + "</option> ");
                            });

                            function findProperty(id){
                                for (var i = 0; i < accounts.length; i++) {
                                    var acc = accounts[i];

                                    for (var i = 0; i < acc.properties.length; i++) {
                                        var prop = acc.properties[i]
                                        if (prop.id == id){
                                            return prop;
                                        }
                                    };
                                };
                                return null;
                            }

                            $("#ga-accounts").on("change",function(){
                                var val = $("#ga-accounts").val();
                                $("#ga-properties").empty();
                                if (val in accountDir){
                                    $("#ga-properties").removeAttr('disabled').append("<option value=''>-</option> ");
                                    
                                    
                                    accountDir[val].properties.map(function(e){
                                        $("#ga-properties").append("<option value='" + e.id + "'>" + e.name + "</option> ");        
                                    });
                                    // $("#ga-properties").append("<option value='new'>Add new Property...</option> ");
                                }else{
                                    $("#ga-properties").attr('disabled','disabled');
                                }
                            });

                            $("#ga-properties").on("change",function(){
                                var val = $("#ga-properties").val();

                                if (val == "new"){
                                    $(".new-property").show();
                                }else{
                                    $(".new-property").hide();
                                }
                            });
                            $(".new-property").hide();

                            $("#select-property .save").on("click",function(){
                                 var val = $("#ga-properties").val();

                                if (val == "new"){
                                    $.post("createGoogleProperty", {
                                        name:$("#googleAPIName").val(),
                                        url:$("#googleAPIUrl").val(),
                                        account:$("#ga-accounts").val()
                                    }).done(function(e){
                                        $("#analyticsCode").val(e.id);
                                    });
                                }else{
                                    $("#accountId").val($("#ga-accounts").val());
                                    $("#analyticsCode").val(val).trigger("change");
                                }

                                $("#select-property").modal("hide");

                                
                            });


                            $("#analyticsCode").on("change",function(){
                                var id = $("#analyticsCode").val()
                                var property = findProperty($("#analyticsCode").val());

                                if(property){
                                    $(".property").html(property.name + " (<samp>" + property.id + "</samp>)");
                                }else if (id){
                                    $(".property").html("<samp>" + property.id + "</samp>");
                                }else{
                                    $(".property").html("");
                                }
                            });

                            $("#analyticsCode").val("${site.analyticsCode}").trigger("change");
                        });
                    </script>

                    <div class="modal fade" id="select-property">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal"><span class="sr-only">Close</span></button>
                                    <h3 class="modal-title">Select Property</h3>
                                    <small>Choose one of your properties</small>
                                </div>
                                <div class="modal-body">

                                    <div class="form-group">
                                            <label class="col-sm-2 control-label">Property</label>

                                            <div class="col-sm-10">
                                                <div class="row">
                                                    <div class="col-sm-6"><select id="ga-accounts" class="form-control"></select></div>
                                                    <div class="col-sm-6"><select disabled="disabled" id="ga-properties" class="form-control"></select></div>
                                                </div>
                                                <p class="help-block">
                                                    If you need to create a new property for this site, go to <a href="https://www.google.com/analytics">Google Analytics</a> create it there and then selected it here.
                                                </p>
                                            </div>
                                            
                                    </div>

                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                                    <button type="button" class="save btn btn-primary">Save changes</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <script src="https://apis.google.com/js/client.js?onload=authorize"></script>
                </c:if>

            </c:if>
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-12">
            <button type="submit" class="btn btn-default btn-primary"><spring:message code="action.save"/></button>
            <a href="${pageContext.request.contextPath}/cms/sites/${site.slug}" class="btn btn-default"><spring:message
                    code="action.cancel"/></a>
        </div>
    </div>

</form>

<div class="modal fade" id="confirmDeleteModal" tabindex="-1" role="dialog" aria-hidden="true">
    <form method="post" action="${pageContext.request.contextPath}/cms/sites/${site.slug}/delete">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span class="sr-only">Close</span></button>
                    <h3 class="modal-title">Delete Site</h3>
                    <small>Are you sure?</small>
                </div>
                <div class="modal-body">
                    You are about to delete the site '<c:out value="${site.name.content}" />'. You will also be deleting all content, including ${site.postSet.size() } posts. There is no way to rollback this opeartion. Are you sure?
                </div>
                <div class="modal-footer">
                    
                    <button type="button" data-dismiss="modal" class="btn btn-default"><spring:message
                            code="action.cancel"/></button>
                    <button type="submit" class="btn btn-danger"><spring:message code="action.delete"/></button>
                </div>
            </div>
        </div>
    </form>
</div>