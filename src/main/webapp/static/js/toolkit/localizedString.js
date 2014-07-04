(function () {
    var attachCssToHead = function () {
        var newScript = document.createElement('style');
        var content = document.createTextNode('[bennu-localized-string]{ display:none !important; }');
        newScript.appendChild(content);
        var bodyClass = document.getElementsByTagName('head')[0];
        bodyClass.insertBefore(newScript, bodyClass.childNodes[2]);
    }

    var changeData = function (inputObject, locale) {
        var val = $(inputObject.data("related")).val();
        if (val === "") {
            $(inputObject.data("related")).val("{}");
            val = "{}";
        }
        $(".bennu-localized-string-input,.bennu-localized-string-textarea", inputObject).val(JSON.parse(val)[locale.tag]);
        $(".bennu-localized-string-language", inputObject).html(locale.displayName || locale.tag);
        $(".bennu-localized-string-language", inputObject).data("locale", locale);
    }

    var attachViewInput = function () {
        $.get(contextPath + "/api/bennu-portal/data", function (d) {
                $("[bennu-localized-string]").map(function (i, e) {
                    e = $(e)
                    if (!$(e).data("input")) {
                        if (e.prop("tagName") == "INPUT") {
                            var dom = $('<div class="bennu-localized-string-input-group input-group" ><input type="text" class="form-control bennu-localized-string-input"><div class="input-group-btn bennu-localized-string-group"><button type="button" class="btn btn-default dropdown-toggle bennu-localized-string-button" data-toggle="dropdown"><span class="bennu-localized-string-language"></span> <span class="caret"></span></button><ul class="dropdown-menu bennu-localized-string-menu pull-right" role="menu"></ul></div></div>');
                        } else if (e.prop("tagName") == "TEXTAREA") {
                            var dom = $('<div class="bennu-localized-string-textArea"><p><div class="btn-group bennu-localized-string-group"><button type="button" class="btn btn-default dropdown-toggle bennu-localized-string-button" data-toggle="dropdown"><span class="bennu-localized-string-language"></span> <span class="caret"></span></button><ul class="dropdown-menu bennu-localized-string-menu" role="menu"></ul></div></p><p><textarea class="form-control bennu-localized-string-textarea"></textarea>');
                        }

                        dom.data("related", e);
                        for (var i = 0; i < d.locales.length; i++) {
                            var locale = d.locales[i];
                            var menuOption = $("<li><a href='#'>" + (locale.displayName || locale.tag) + "</a>");
                            menuOption.data("locale", locale);
                            menuOption.on("click", function (e) {
                                changeData(dom, $(e.target).data("locale"));
                            });
                            $(".bennu-localized-string-menu", dom).append(menuOption);
                        }

                        changeData(dom, d.locale);

                        $(".bennu-localized-string-input,.bennu-localized-string-textarea", dom).on("input propertychange", function () {
                            var data = JSON.parse($(dom.data("related")).val());
                            data[$(".bennu-localized-string-language", dom).data("locale").tag] = $(".bennu-localized-string-input,.bennu-localized-string-textarea", dom).val();
                            $(dom.data("related")).val(JSON.stringify(data));
                            $(".help-block", dom).remove();
                            dom.removeClass("has-error");
                        });

                        e.data("input", dom);
                        e.after(dom);

                        var form = dom.closest("form");
                        if (!form.data("bennu-localized-string-validator")) {
                            form.on("submit", function (submitEvent) {
                                var val = $("[bennu-localized-string]", form).map(function (i, xx) {
                                    xx = $(xx);

                                    xx.data("input").removeClass("has-error");

                                    if (xx.attr("required")) {
                                        var val = d.locales.map(function (x) {
                                            var val = xx.val();
                                            return val && JSON.parse(val)[x.tag] || false;
                                        }).reduce(function (x, y) {
                                            return x && y;
                                        }, true);

                                        if (!val) {
                                            xx.data("input").addClass("has-error");
                                            if (!$(".help-block", xx.data("input")).length) {
                                                $(".bennu-localized-string-input,.bennu-localized-string-textarea", xx.data("input")).after('<span class="help-block">You need to to insert text in at least one language</span>')
                                            }
                                        }

                                        return val;
                                    } else if (xx.attr("required-any")) {
                                        var value = xx.val();
                                        var val = value && value != "{}";

                                        if (!val) {
                                            xx.data("input").addClass("has-error");
                                            if (!$(".help-block", xx.data("input")).length) {
                                                $(".bennu-localized-string-input,.bennu-localized-string-textarea", xx.data("input")).after('<span class="help-block">You need to to insert text in at least one language</span>')
                                            }
                                        }

                                        return val;
                                    }
                                });

                                if (!Array.prototype.reduce.apply(val, [function (x, y) {
                                    return x && y;
                                }, true])) {
                                    submitEvent.preventDefault();
                                    return false;
                                }
                            });

                            form.data("bennu-localized-string-validator", true);
                        }
                    }
                });
            }
        )
        ;
    }

    $(function () {
        attachCssToHead();
        attachViewInput();
        var target = document.querySelector('body');
        var observer = new MutationObserver(function (mutations) {
            if ($.grep(mutations, function (e) {
                return $.grep(e.addedNodes, function (e) {
                    var contains = $("[bennu-localized-string]", $(e)).length !== 0;
                    var is = $(e).attr("bennu-localized-string") === "";
                    return is || contains;
                }).length !== 0;
            }).length !== 0) {
                attachViewInput();
            }
            $.map(mutations, function (e) {
                return $.map(e.removedNodes, function (e) {
                    if ($(e).attr("bennu-localized-string") === ""){
                        $(e).data("input").remove();
                    }
                });
            });
        });

        var config = { attributes: true, subtree:true, childList: true, characterData: true };
        observer.observe(target, config);
    });
})
();
