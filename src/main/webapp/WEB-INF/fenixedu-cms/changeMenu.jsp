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

<h2 class="page-header" style="margin-top: 0">
  <spring:message code="menu.edit.title" />
  <small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a> </small>
</h2>

${portal.toolkit()}

<style>

    .fancytree-container {
        outline: none;
    }
</style>
<form id="deleteForm" method="post" action="">
</form>
<div class="container">
    <script src="${pageContext.request.contextPath}/bennu-admin/fancytree/jquery-ui.min.js"></script>
    <link href="${pageContext.request.contextPath}/webjars/fenixedu-canvas/fancytree/skin-fenixedu/ui.fancytree.css" rel="stylesheet" type="text/css">
    <script src="${pageContext.request.contextPath}/webjars/fenixedu-canvas/fancytree/js/jquery.fancytree-all.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/static/jquery.js" type="text/javascript"></script>
    <div class="row" style="min-height:400px">
        <div class="col-md-4" style="min-height:400px">
            <div style="outline:none;" id="tree"></div>
        </div>
        <div id="options" class="col-md-6">
            <form action="changeItem" id="modal" method="post">
                <input type="hidden" name="menuItemOid" value="null"/>
                <input type="hidden" name="menuItemOidParent" value="null"/>
                <input type="hidden" name="position" value=""/>

                <div class="form-group">
                    <button onclick="showModal();return false" class="btn btn-default"><spring:message
                            code="menu.edit.label.createSubitem"/></button>
                </div>

                <div class="form-group">
                    <label class="control-label" for="inputSuccess1"><spring:message code="menu.edit.label.menuLabel"/></label>
                    <input type="text" bennu-localized-string name="name" class="form-control"
                           placeholder="<spring:message code="menu.edit.label.name"/>">
                </div>

                <div id="menuitem-options">

                    <div class="radio">
                        <label>
                            <input type="radio" name="use" class="useurl" value="url" checked>
                            <spring:message code="menu.edit.label.linkToUrl"/>
                        </label>

                        <div class="form-group">
                            <input type="text" name="url" class="url-select form-control"
                                   placeholder="<spring:message code="menu.edit.label.url"/>">
                        </div>
                    </div>


                    <div class="radio">
                        <label>
                            <input type="radio" name="use" class="usepage" value="page">
                            <spring:message code="menu.edit.label.linkToPage"/>
                        </label>

                        <div class="form-group">
                            <select name="slugPage" class="page-select form-control">
                                <option value="null">-</option>
                                <c:forEach var="p" items="${site.sortedPages}">
                                    <option value="${ p.slug }">${ p.name.content }</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="radio">
                        <label>
                            <input type="radio" name="use" class="useFolder" value="folder">
                            <spring:message code="menu.edit.label.folder"/>
                        </label>
                    </div>

                </div>

                <div class="form-group">
                    <button type="submit" class="btn btn-primary"><spring:message code="action.save"/></button>
                    <button onclick='$("#deleteForm").submit(); return false' class="btn btn-danger delete-item delete"><spring:message code="action.delete"/></button>
                </div>
            </form>
        </div>
    </div>

    <div class="modal fade">
        <form action="createItem" id="modal" method="post">
            <input type="hidden" name="menuItemOid" value="null"/>

            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title"><spring:message
                            code="menu.edit.label.createSubitem"/></h4>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label class="control-label" for="inputSuccess1"><spring:message
                                    code="menu.edit.label.menuLabel"/></label>
                            <input type="text" bennu-localized-string name="name" value="{}" class="form-control"
                                   placeholder="<spring:message code="menu.edit.label.name"/>">
                        </div>

                        <div id="menuitem-options">

                            <div class="radio">
                                <label>
                                    <input type="radio" name="use" class="useurl" value="url" checked>
                                    <spring:message code="menu.edit.label.linkToUrl"/>
                                </label>

                                <div class="form-group">
                                    <input type="text" name="url" class="url-select form-control"
                                           placeholder="<spring:message code="menu.edit.label.url"/>">
                                </div>
                            </div>


                            <div class="radio">
                                <label>
                                    <input type="radio" name="use" class="usepage" value="page">
                                    <spring:message code="menu.edit.label.linkToPage"/>
                                </label>

                                <div class="form-group">
                                    <select name="slugPage" class="page-select form-control">
                                        <option value="null">-</option>
                                        <c:forEach var="p" items="${site.sortedPages}">
                                            <option value="${ p.slug }">${ p.name.content }</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="radio">
                                <label>
                                    <input type="radio" name="use" class="useFolder" value="folder">
                                    <spring:message code="menu.edit.label.folder"/>
                                </label>
                                <p><spring:message code="menu.edit.label.folderDescription"/></p>
                            </div>

                        </div>

                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message
                                code="action.close"/></button>
                        <button type="submit" class="btn btn-primary"><spring:message code="action.create"/></button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </form>
    </div>
    <!-- /.modal -->
</div>

<script>

    function optionsForComponent(event, data) {
        $("#options").show();
        $("#menuitem-options").show();
        if (data.node.data.root) {
            $("#menuitem-options").hide()
            $(".delete-item").hide();
        } else {
            $(".delete-item").show();
            if(data.node.data.isFolder){
                $("#options input.useurl").prop("checked", false);
                $("#options input.usepage").prop("checked", false);
                $("#options input.useFolder").prop("checked", true);
                $("#options input[name='url']").prop('readonly', true);
                $("#options input[name='url']").val("");
                $("#options  select option").filter(function () {
                    //may want to use $.trim in here
                    return $(this).text() == "-";
                }).prop('selected', true);
                $("#options .page-select").prop('readonly', true);
            } else if (data.node.data.url) {
                $("#options input.useurl").prop("checked", true)
                $("#options input.usepage").prop("checked", false)
                $("#options input[name='url']").prop('readonly', false);
                $("#options input[name='url']").val(data.node.data.url)
                $("#options  select option").filter(function () {
                    //may want to use $.trim in here
                    return $(this).text() == "-";
                }).prop('selected', true);
                $("#options .page-select").prop('readonly', true);
            }
            else {
                $("#options input.useurl").prop("checked", false);
                $("#options input.usepage").prop("checked", true);
                $("#options input[name='url']").val("");
                $("#options input[name='url']").prop('readonly', true);
                $("#options  select option").filter(function () {
                    //may want to use $.trim in here
                    return $(this).val() == data.node.data.page;
                }).prop('selected', true);
            }

            $("#deleteForm").attr("action", "delete/" + data.node.key);
        }


        $(".modal [name='menuItemOid']").val(data.node.key);
        $("#options [name='menuItemOid']").val(data.node.key);
        $("#options [name='position']").val(data.node.data.position);
        $("#options [name='menuItemOidParent']").val(data.node.key == "null" ? "null" : data.node.parent.key);
        $("#options h3").html(Bennu.localizedString.getContent(data.node.data.name, Bennu.locale));
        var x = Bennu.localizedString.getContent(data.node.data.name, Bennu.locale);
        if (x) {
            $("#options h3").html(x);
        } else {
            $("#options h3").html("&nbsp;");
        }
        $("#options input[name='name']").val(JSON.stringify(data.node.data.name, Bennu.locale)).on("keyup", function () {
            var x = $("#options input[name='name']").val();
            if (x) {
                $("#options h3").html(x);
            } else {
                $("#options h3").html("&nbsp;");
            }
        }).trigger("change");
    }

    function showModal() {
        $(".modal").modal({});
        $(".modal input[type='text']").val("");
        $(".modal input[name='name']").val("{}");
        $(".modal input[name='name']").trigger("change");
        $(".modal input.useurl").prop("checked", true);

    }

    function setLinks(block) {
        return function () {
            if ($(block + " input.usepage").is(':checked')) {
                $(block + " .url-select").prop('readonly', true);
                $(block + " .url-select").val("");
                $(block + " .page-select").prop('readonly', false);
            } else {
                $(block + " .url-select").prop('readonly', false);
                $(block + " select option").filter(function () {
                    //may want to use $.trim in here
                    return $(this).text() == "-";
                }).prop('selected', true);
                $(block + " .page-select").prop('readonly', true);
            }
        };
    }

    $("#options").hide();

    $(function () {
        $("#options input[name='name']").on("keypress", function () {
            var x = $("#options input[name='name']").val();
            if (x) {
                $("#options h3").html(x);
            } else {
                $("#options h3").html("&nbsp;");
            }
        });

        $("#options input[name='use']").on('click', setLinks("#options"));
        setLinks("#options")();

        $(".modal input[name='use']").on('click', setLinks(".modal"));
        setLinks(".modal")();

        // Create the tree inside the <div id="tree"> element.
        $("#tree").fancytree({
            source: {
                url: "data"
            },
            click: optionsForComponent,
            init: function () {
                $("#tree").fancytree("getRootNode").visit(function (node) {
                    node.setExpanded(true);
                });
            },
            extensions: ["dnd"],
            dnd: {
                preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
                preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
                autoExpandMS: 400,
                dragStart: function (node, data) {
                    optionsForComponent({}, {node: node});
                    return true;
                },
                dragEnter: function (node, data) {
                    return true;
                },
                dragDrop: function (node, data) {
                    if (data.hitMode === "before" || data.hitMode === "after") {
                        if (data.hitMode === "before") {
                            $("#options [name='position']").val(node.data.position);
                        } else {
                            $("#options [name='position']").val(node.data.position + 1);
                        }
                        $("#options [name='menuItemOidParent']").val(node.parent.key);
                    } else if (data.hitMode === "over") {
                        if (node.children) {
                            $("#options [name='position']").val(node.children.length);
                        } else {
                            $("#options [name='position']").val(0);
                        }
                        $("#options [name='menuItemOidParent']").val(node.key);
                    }
                    $("#options form").submit();
                }
            },
        });
    });

    $("select[name=slugPage]").change(function(event) {
        $(event.target).closest('form').find('input[name=use].usepage').prop('checked', true);
    });
</script>
  
