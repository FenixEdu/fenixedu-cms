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

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/jquery.jsonview.css"/>
<script src="${pageContext.request.contextPath}/static/js/jquery.jsonview.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/jquery.jsonview.css"/>
<script src="${pageContext.request.contextPath}/static/js/jquery.jsonview.js"></script>

${portal.toolkit()}

<script src="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/jquery-ui.min.js"></script>
<link href="${pageContext.request.contextPath}/webjars/fenixedu-canvas/fancytree/skin-fenixedu/ui.fancytree.css" rel="stylesheet" type="text/css">
<script src="${pageContext.request.contextPath}/webjars/fenixedu-canvas/fancytree/js/jquery.fancytree-all.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/jquery.js" type="text/javascript"></script>

<form class="form" action="" method="post" role="form" id="postForm">
    <div class="page-header">
        <h1>${site.name.content}</h1>
        <h2>
            <small>
                <a href="${pageContext.request.contextPath}/cms/pages/${site.slug}">
                    <spring:message code="page.edit.title"/> - ${page.name.content}
                </a>
            </small>
        </h2>

        <div class="row">
            <div class="col-sm-12">
                <button type="submit" class="btn btn-primary">Update</button>
                <a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/${post.slug}/versions" class="btn btn-default">Versions</a>
                <a href="#" class="btn btn-default" data-toggle="modal" data-target="#viewMetadata">Metadata</a>
                <a href="${pageContext.request.contextPath}/cms/pages/advanced/${site.slug}/${page.slug}/edit" class="btn btn-default">Advanced</a>
                <c:if test="${page.site.published && page.published && post.visible}">
                    <a href="${page.address}" target="_blank" class="btn btn-default">Link</a>
                </c:if>
            </div>
        </div>
    </div>

    <div class="form-group">
        <input bennu-localized-string required-any name="name" placeholder='<spring:message code="post.edit.label.name" />' value='<c:out value="${page.name.json()}"/>' />
        <c:if test="${emptyName != null}"><p class="text-danger"><spring:message code="post.edit.error.emptyName"/></p></c:if>
    </div>

    <p>
        <div>Permalink:
            <samp>${site.baseUrl}/${page.slug}</samp>
            <input required type="hidden" name="newSlug" class="form-control" placeholder="<spring:message code="site.edit.label.slug" />" value='${page.slug}' \>
            <button class="btn btn-default btn-xs">Edit</button>
            <a href="${page.address}" target="_blank" class="btn btn-default btn-xs">View Page</a>
        </div>
    </p>

    <div class="form-group">
            <textarea id="htmlEditor" bennu-html-editor bennu-localized-string name="body" rows="3"><c:out value="${post.body.json()}"/></textarea>
    </div>

    <div class="panel panel-default">
        <div class="panel-heading">Publish</div>
        <div class="panel-body">
            <dl class="dl-horizontal">
                <dt>Published</dt>
                <dd><input type="checkbox" value="true" ${post.active ? 'checked="checked"' : ''} name="active" /></dd>
                <dt>Author</dt>
                <dd><input name="createdBy" bennu-user-autocomplete class="form-control" type="text" value="${post.createdBy.username}"></dd>
                <dt>Access Control</dt>
                <dd><input bennu-group allow="public,users,managers,custom" name="viewGroup" type="text" value="${ post.canViewGroup.expression }"/></dd>
            </dl>
        </div>
    </div>

    <div class="panel panel-default">
        <div class="panel-heading"><spring:message code="site.manage.label.categories"/></div>
        <div class="panel-body">
            <p>
                <a class="btn btn-default btn-xs" data-toggle="modal" data-target="#addCategory">Add New Category</a>
            </p>

            <c:choose>
                <c:when test="${!site.categoriesSet.isEmpty()}">
                    <div class="row">
                        <c:forEach var="c" items="${site.categoriesSet}" varStatus="loop">
                            <div class="col-sm-4">
                                <div class="checkbox">
                                    <label><input type="checkbox" name="categories" value="${c.slug}" ${post.categoriesSet.contains(c) ? 'checked="checked"' : ''} /> ${c.name.content}</label>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>

                <c:otherwise>
                    <i>Não existem categorias associadas.</i>
                </c:otherwise>
            </c:choose>

        </div>
    </div>

    <div class="panel panel-default">
        <div class="panel-heading">Files</div>
        <div class="panel-body">

            <p><a class="btn btn-default btn-xs" data-toggle="modal" data-target="#addAttachment">Add Attachment</a></p>

            <c:choose>
                <c:when test="${post.filesSorted.size() > 0}">
                    <table class="table table-striped table-bordered">
                        <thead>
                        <tr>
                            <th class="center">#</th>
                            <th class="col-md-6"><spring:message code="theme.view.label.name"/></th>
                            <th>Presentation</th>
                            <th><spring:message code="theme.view.label.type"/></th>
                            <th>&nbsp;</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="postFile" items="${post.filesSorted}" varStatus="loop">
                            <c:set var="file" value="${postFile.files}"></c:set>
                            <tr>
                                <td class="center">
                                    <h5>${loop.index + 1}</h5>
                                </td>

                                <td>
                                    <a href="${cms.downloadUrl(file)}" target="_blank"><h5>${file.displayName}</h5></a>
                                </td>

                                <td>
                                    <center>
                                        <c:choose>
                                            <c:when test="${postFile.isEmbedded}">
                                                Embedded
                                            </c:when>
                                            <c:otherwise>
                                                Attachment
                                            </c:otherwise>
                                        </c:choose>
                                    </center>
                                </td>

                                <td><code>${file.contentType}</code></td>

                                <td>
                                    <button class="btn btn-danger btn-sm" data-toggle="modal"
                                            data-target="#attachmentDeleteModal"
                                            type="button" data-file="${file.displayName}"
                                            data-file-index="${loop.index}">
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </button>
                                    <a href="${cms.downloadUrl(file)}" target="_blank" class="btn btn-default btn-sm">Link</a>

                                    <div class="btn-group">

                                        <c:choose>
                                            <c:when test="${loop.index != 0}">
                                                <button class="btn btn-default btn-sm" data-origin="${loop.index}"
                                                        data-destiny="${loop.index - 1}" type="button"><span
                                                        class="glyphicon glyphicon-chevron-up"></span></button>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="btn btn-default disabled btn-sm" type="button"><span
                                                        class="glyphicon glyphicon-chevron-up"></span></button>
                                            </c:otherwise>
                                        </c:choose>

                                        <c:choose>
                                            <c:when test="${loop.index != post.filesSorted.size() -1}">
                                                <button class="btn btn-default btn-sm" data-origin="${loop.index}"
                                                        data-destiny="${loop.index + 1}" type="button"><span
                                                        class="glyphicon glyphicon-chevron-down"></span></button>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="btn btn-default disabled btn-sm" data-toggle="modal" type="button">
                                                    <span class="glyphicon glyphicon-chevron-down"></span></button>
                                            </c:otherwise>
                                        </c:choose>

                                    </div>

                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <i>Post has no attachments</i>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <br />
    <div class="panel panel-default">
        <div class="panel-heading">Menu</div>
        
        <div class="panel-body">
            <div class="form-group">
                <label class="col-sm-2 control-label">Menu: </label>
                <div class="col-sm-10">
                    <select id="menu-select" class="form-control">
                        <option ${menu == null ? 'selected' : ''} value="${null}">None</option>
                        <c:forEach var="siteMenu" items="${site.getMenusSet()}">
                            <option ${menu == siteMenu ? 'selected' : ''} value="${ siteMenu.slug }" data-menu-oid="${siteMenu.externalId}">${ siteMenu.name.content }</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12">
                    <div style="outline:none;" id="tree"></div>
                </div>
            </div>
        </div>

        <input type="hidden" id="menu" name="menu" value="${menu!=null ? menu.externalId : null}">
        <input type="hidden" id="menuItem" name="menuItem" value="${menuItem!=null ? menuItem.externalId : null}">
        <input type="hidden" id="menuItemParent" name="menuItemParent" value="${menuItem!=null && menuItem.parent!=null ? menuItem.parent.externalId : null}">
        <input type="hidden" id="menuItemName" name="menuItemName" value="${menuItem!=null && menuItem.name!=null ? menuItem.name.content : page.name.content}">
        <input type="hidden" id="menuItemPosition" name="menuItemPosition" value="${menuItem!=null && menuItem.position!=null ? menuItem.position : 0}">
    </div>
</div>
</form>

<div class="modal fade" id="addAttachment" tabindex="-1" role="dialog" aria-hidden="true">
    <form action="addAttachment" enctype="multipart/form-data" class="form-horizontal" method="post">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span>
                        <span class="sr-only">Close</span>
                    </button>
                    <h4><spring:message code="action.new"/></h4>
                </div>
                <div class="modal-body">

                    <div class="form-group">
                        <label class="col-sm-2 control-label"><spring:message code="theme.view.fileName"/>:</label>

                        <div class="col-sm-10"><input required type="text" name="name" class="form-control"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label"><spring:message code="theme.add.label.file"/>:</label>

                        <div class="col-sm-10"><input type="file" name="attachment" class="form-control"></div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><spring:message code="label.make"/></button>
                </div>
            </div>
        </div>
    </form>
</div>

<div class="modal fade" id="addCategory" tabindex="-1" role="dialog" aria-hidden="true">
    <form class="form-horizontal" action="createCategory" method="post" role="form">
        <div class="modal-dialog">
            <div class="modal-content">

                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span>
                        <span class="sr-only">Close</span>
                    </button>
                    <h4><spring:message code="categories.create.title" /></h4>
                </div>
                
                <div class="modal-body">
                    <div class="${emptyName ? "form-group has-error" : "form-group"}">
                        <label class="col-sm-2 control-label"><spring:message code="categories.create.label.name"/></label>
                        <div class="col-sm-10">
                            <input type="text" name="name" bennu-localized-string required-any class="form-control"  placeholder="<spring:message code="categories.create.label.name"/>">
                            <c:if test="${emptyName != null}"><p class="text-danger"><spring:message code="categories.create.error.emptyName"/></p></c:if>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><spring:message code="label.make"/></button>
                </div>
            </div>
        </div>
    </form>
</div>

<div class="modal fade" id="attachmentDeleteModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                </button>
                <h4><spring:message code="action.delete"/></h4>
            </div>
            <div class="modal-body">
                <spring:message code="theme.view.label.delete.confirmation"/> <b id="fileName"></b>?
            </div>
            <div class="modal-footer">
                <form action="deleteAttachment" id="deleteAttachment" method="POST">
                    <input type="hidden" name="file"/>
                    <button type="submit" class="btn btn-danger"><spring:message code="label.yes"/></button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message
                            code="label.no"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="viewMetadata" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                </button>
                <h4>Metadata</h4>
            </div>
            <div class="modal-body">
                <div class="clearfix">
                    <div class="form-group">
                        <div class="col-sm-12">
                            <div class="json-data"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    var originalMenu = $('#menu').val();
    var originalMenuItem = $('#menuItem').val() || "new_menu_it";
    var originalMenuItemParent = $('#menuItemParent').val();
    var menuItemName = $('#menuItemName').val();
    var originalMenuItemPosition = $('#menuItemPosition').val();

    var tree;

    $('#menu-select').change(function(e){
        setTimeout(function(){
            var selectedMenuOid = $('#menu-select :selected').data('menu-oid');
            loadMenu(selectedMenuOid);
        })
    });

    loadMenu(originalMenu);

    function loadMenu(menuOid) {

        function dataUrl() {
            return "${pageContext.request.contextPath}/cms/menus/${site.slug}/" + menuOid + "/data";
        }

        function updateMenuItemInfo(menu, menuItem, menuItemParent, menuItemPosition) {
            $('#menu').val(menu);
            $('#menuItem').val(menuItem);
            $('#menuItemParent').val(menuItemParent);
            $('#menuItemPosition').val(menuItemPosition);
        }

        function loadTree() {
            if(!menuOid){
                updateMenuItemInfo(null, null, null, 0);
                $('#tree').hide();
                return false;
            } else {
                $('#tree').show();
                $('#tree').fancytree({
                    source: {
                        url: dataUrl()
                    },
                    click: function(e, data){ 
                        var currentNodeKey = data.tree.getActiveNode().key; 
                        setTimeout(function(){
                            data.tree.activateKey(currentNodeKey);
                        });
                    },
                    extensions: ["dnd"],
                    dnd: {
                        preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
                        preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
                        autoExpandMS: 400,
                        dragStart: function (node, data) {
                            return node.key == originalMenuItem;
                        },
                        dragEnter: function (node, data) {
                            return true;
                        },
                        dragDrop: function (node, data) {
                            if (data.hitMode === "before") {
                                updateMenuItemInfo(menuOid, data.otherNode.key, node.key, node.data.position)
                            } if(data.hitMode === "after") {
                                updateMenuItemInfo(menuOid, data.otherNode.key, node.key, node.data.position + 1)
                            } else if (data.hitMode === "over") {
                                updateMenuItemInfo(menuOid, data.otherNode.key, node.key, (node.children ? node.children.length : 0));
                            }
                            data.otherNode.moveTo(node, data.hitMode);
                            node.setExpanded(true);
                            return true;
                        }
                    },
                    init: function (e, data) {

                        if(menuOid != originalMenu) {
                            var position = data.tree.rootNode.children[0] && data.tree.rootNode.children[0].children ? data.tree.rootNode.children[0].children.lenght : 0;
                            updateMenuItemInfo(menuOid, originalMenuItem, data.tree.rootNode.children[0].key, position);
                            data.tree.rootNode.children[0].addChildren({key: originalMenuItem, title: menuItemName, active: true, data: {position: position}});
                        } else {
                            updateMenuItemInfo(originalMenu, originalMenuItem, originalMenuItemParent, originalMenuItemPosition);
                            data.tree.activateKey(originalMenuItem);
                        }

                        data.tree.visit(function (node) {
                            node.setExpanded(true);
                        });
/*
                        setTimeout(function(){
                            debugger;
                            data.tree.activateKey(originalMenuItem);
                        }, 300);
*/
                    }
                });
            }
        }

        loadTree();
    }
</script>

<style type="text/css">
    .json-data {
        height: 400px;
        overflow: scroll;
        border: 1px solid #ddd;
        padding: 20px;
        margin-bottom: 20px;
        border-radius: 3px;
    }
    #tree .fancytree-container{
        min-height: 400px;
    }
</style>

<form action="moveAttachment" id="moveAttachment" class="hidden" method="post">
    <input type="hidden" name="origin" value="${loop.index}"/>
    <input type="hidden" name="destiny" value="${loop.index + 1}"/>
</form>

<script type="application/javascript">
    (function(){    
        <c:choose>
            <c:when test="${post.metadata != null}">
                $(".json-data").JSONView(${post.metadata}, {collapsed: true});
            </c:when>
            <c:otherwise>
                $(".json-data").JSONView({}, {collapsed: true});
            </c:otherwise>
        </c:choose>

        $("[data-target='#attachmentDeleteModal']").on('click', function (event) {
            var index = $(event.target).closest("[data-file-index]").attr('data-file-index');
            var filename = $(event.target).closest("[data-file]").attr('data-file');
            $('#fileName').html(filename);
            $('#deleteAttachment')[0].file.value = index;
        });

        $("[data-origin]").on("click", function (e) {
            debugger;
            var form = $("#moveAttachment")[0];
            $(form.origin).val($(e.currentTarget).data("origin"));
            $(form.destiny).val($(e.currentTarget).data("destiny"));
            form.submit();
        });
        $("#htmlEditor").data("fileHandler", submitFiles);

    })();

    function submitFiles(files, cb) {
        
        function transferComplete(event) {
            var objs = JSON.parse(event.currentTarget.response);
            cb(objs.map(function (x) {
                return x.url
            }));
        }

        function updateProgress(event) {
            if (event.lengthComputable) {
                var complete = (event.loaded / event.total * 100 | 0);
                //progress.value = progress.innerHTML = complete;
                console.log(complete);
            }
        }

        var formData = new FormData();
        
        for (var i = 0; i < files.length; i++) {
            formData.append('attachment', files[i]);
        }

        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'addFile.json');
        xhr.addEventListener("progress", updateProgress, false);
        xhr.addEventListener("load", transferComplete, false);

        xhr.send(formData);
    }
</script>

