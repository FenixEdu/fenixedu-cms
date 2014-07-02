    /* http://github.com/mindmup/bootstrap-wysiwyg */
    /*global jQuery, $, FileReader*/
    /*jslint browser:true*/
(function ($) {
    'use strict';
    var readFileIntoDataUrl = function (fileInfo) {
        var loader = $.Deferred(),
            fReader = new FileReader();
        fReader.onload = function (e) {
            loader.resolve(e.target.result);
        };
        fReader.onerror = loader.reject;
        fReader.onprogress = loader.notify;
        fReader.readAsDataURL(fileInfo);
        return loader.promise();
    };
    $.fn.cleanHtml = function () {
        var html = $(this).html();
        return html && html.replace(/(<br>|\s|<div><br><\/div>|&nbsp;)*$/, '');
    };
    $.fn.wysiwyg = function (userOptions) {
        var editor = this,
            selectedRange,
            options,
            toolbarBtnSelector,
            updateToolbar = function () {
                if (options.activeToolbarClass) {
                    $(options.toolbarSelector).find(toolbarBtnSelector).each(function () {
                        var command = $(this).data(options.commandRole);
                        if (document.queryCommandState(command)) {
                            $(this).addClass(options.activeToolbarClass);
                        } else {
                            $(this).removeClass(options.activeToolbarClass);
                        }
                    });
                }
            },
            execCommand = function (commandWithArgs, valueArg) {
                var commandArr = commandWithArgs.split(' '),
                    command = commandArr.shift(),
                    args = commandArr.join(' ') + (valueArg || '');
                document.execCommand(command, 0, args);
                updateToolbar();
            },
            bindHotkeys = function (hotKeys) {
                $.each(hotKeys, function (hotkey, command) {
                    editor.keydown(hotkey, function (e) {
                        if (editor.attr('contenteditable') && editor.is(':visible')) {
                            e.preventDefault();
                            e.stopPropagation();
                            execCommand(command);
                        }
                    }).keyup(hotkey, function (e) {
                        if (editor.attr('contenteditable') && editor.is(':visible')) {
                            e.preventDefault();
                            e.stopPropagation();
                        }
                    });
                });
            },
            getCurrentRange = function () {
                var sel = window.getSelection();
                if (sel.getRangeAt && sel.rangeCount) {
                    return sel.getRangeAt(0);
                }
            },
            saveSelection = function () {
                selectedRange = getCurrentRange();
            },
            restoreSelection = function () {
                var selection = window.getSelection();
                if (selectedRange) {
                    try {
                        selection.removeAllRanges();
                    } catch (ex) {
                        document.body.createTextRange().select();
                        document.selection.empty();
                    }

                    selection.addRange(selectedRange);
                }
            },
            insertFiles = function (files) {
                editor.focus();
                $.each(files, function (idx, fileInfo) {
                    if (/^image\//.test(fileInfo.type)) {
                        $.when(readFileIntoDataUrl(fileInfo)).done(function (dataUrl) {
                            execCommand('insertimage', dataUrl);
                        }).fail(function (e) {
                            options.fileUploadError("file-reader", e);
                        });
                    } else {
                        options.fileUploadError("unsupported-file-type", fileInfo.type);
                    }
                });
            },
            markSelection = function (input, color) {
                restoreSelection();
                if (document.queryCommandSupported('hiliteColor')) {
                    document.execCommand('hiliteColor', 0, color || 'transparent');
                }
                saveSelection();
                input.data(options.selectionMarker, color);
            },
            bindToolbar = function (toolbar, options) {
                toolbar.find(toolbarBtnSelector).click(function () {
                    restoreSelection();
                    editor.focus();
                    execCommand($(this).data(options.commandRole));
                    saveSelection();
                });
                toolbar.find('[data-toggle=dropdown]').click(restoreSelection);

                toolbar.find('input[type=text][data-' + options.commandRole + ']').on('webkitspeechchange change', function () {
                    var newValue = this.value; /* ugly but prevents fake double-calls due to selection restoration */
                    this.value = '';
                    restoreSelection();
                    if (newValue) {
                        editor.focus();
                        execCommand($(this).data(options.commandRole), newValue);
                    }
                    saveSelection();
                }).on('focus', function () {
                    var input = $(this);
                    if (!input.data(options.selectionMarker)) {
                        markSelection(input, options.selectionColor);
                        input.focus();
                    }
                }).on('blur', function () {
                    var input = $(this);
                    if (input.data(options.selectionMarker)) {
                        markSelection(input, false);
                    }
                });
                toolbar.find('input[type=file][data-' + options.commandRole + ']').change(function () {
                    restoreSelection();
                    if (this.type === 'file' && this.files && this.files.length > 0) {
                        insertFiles(this.files);
                    }
                    saveSelection();
                    this.value = '';
                });
            },
            initFileDrops = function () {
                editor.on('dragenter dragover', false)
                    .on('drop', function (e) {
                        var dataTransfer = e.originalEvent.dataTransfer;
                        e.stopPropagation();
                        e.preventDefault();
                        if (dataTransfer && dataTransfer.files && dataTransfer.files.length > 0) {
                            insertFiles(dataTransfer.files);
                        }
                    });
            };
        options = $.extend({}, $.fn.wysiwyg.defaults, userOptions);
        toolbarBtnSelector = 'a[data-' + options.commandRole + '],button[data-' + options.commandRole + '],input[type=button][data-' + options.commandRole + ']';
        bindHotkeys(options.hotKeys);
        if (options.dragAndDropImages) {
            initFileDrops();
        }
        bindToolbar($(options.toolbarSelector), options);
        editor.attr('contenteditable', true)
            .on('mouseup keyup mouseout', function () {
                saveSelection();
                updateToolbar();
            });
        $(window).bind('touchend', function (e) {
            var isInside = (editor.is(e.target) || editor.has(e.target).length > 0),
                currentRange = getCurrentRange(),
                clear = currentRange && (currentRange.startContainer === currentRange.endContainer && currentRange.startOffset === currentRange.endOffset);
            if (!clear || isInside) {
                saveSelection();
                updateToolbar();
            }
        });
        return this;
    };
    $.fn.wysiwyg.defaults = {
        hotKeys: {
            'ctrl+b meta+b': 'bold',
            'ctrl+i meta+i': 'italic',
            'ctrl+u meta+u': 'underline',
            'ctrl+z meta+z': 'undo',
            'ctrl+y meta+y meta+shift+z': 'redo',
            'ctrl+l meta+l': 'justifyleft',
            'ctrl+r meta+r': 'justifyright',
            'ctrl+e meta+e': 'justifycenter',
            'ctrl+j meta+j': 'justifyfull',
            'shift+tab': 'outdent',
            'tab': 'indent'
        },
        toolbarSelector: '[data-role=editor-toolbar]',
        commandRole: 'edit',
        activeToolbarClass: 'btn-primary',
        selectionMarker: 'edit-focus-marker',
        selectionColor: 'darkgrey',
        dragAndDropImages: true,
        fileUploadError: function (reason, detail) { console.log("File upload error", reason, detail); }
    };
}(window.jQuery));

(function(){
    var attachCssToHead = function () {
        var newScript = document.createElement('style');
        var content = document.createTextNode('[bennu-html-editor]{ display:none !important; }');
        newScript.appendChild(content);
        var bodyClass = document.getElementsByTagName('head')[0];
        bodyClass.insertBefore(newScript, bodyClass.childNodes[2]);
    }


    $(function () {
        attachCssToHead();
        $("[bennu-html-editor]").map(function (i, e) {
            e = $(e)
            var dom = $('<div><div class="btn-toolbar" data-role="editor-toolbar" data-target="#editor"></div><div id="editor" class="form-control" style="margin-top:30px;" contenteditable="true"></div></div>');
            var toolbarReqs = e.attr("toolbar")
            if (toolbarReqs === "" || toolbarReqs === undefined || toolbarReqs === null){
                toolbarReqs = "size,style,lists,align,add,undo,fullscreen";
            }
            toolbarReqs = toolbarReqs.split(",");
            for (var i = 0; i < toolbarReqs.length; i++) {
                var c = toolbarReqs[i];
                if(c === "site"){
                    $(".btn-toolbar", e).append(
                    '<div class="btn-group"><a class="btn btn-default dropdown-toggle" data-toggle="dropdown" title="" data-original-title="Font Size"><span class="glyphicon glyphicon-text-height"></span>&nbsp;<b class="caret"></b></a><ul class="dropdown-menu"><li><a data-edit="fontSize 5"><font size="5">Huge</font></a></li><li><a data-edit="fontSize 3"><font size="3">Normal</font></a></li><li><a data-edit="fontSize 1"><font size="1">Small</font></a></li></ul></div>'
                    );
                }
            };
            $(e).after(dom);
        });
    });
})();