angular.module('fancyTreeDirective', []).directive('fancyTree', function($timeout){

    function treeLink(scope, element, attrs) {
        var tree;
        var searchTree = function(elementKey) {
            var searchTreeRecursive = function(element, elementKey){
                if(!element || !elementKey) {
                    return element;
                } else if(element.key == elementKey){
                      return element;
                 } else if(element.children && element.children.length) {
                    for(var i=0; i < element.children.length; i++){
                        var result = searchTreeRecursive(element.children[i], elementKey);
                        if(result) return result;
                    }
                 }
                 return null;
            };
            for(var i=0; i < scope.items.length; ++i) {
                var found = searchTreeRecursive(scope.items[i], elementKey);
                if(found) return found;
            }
            return false;
        };
        var canDrop = attrs.canDrop ? scope.$eval(attrs.canDrop) : function() {return true;};
        var dataSource = function() { return attrs.items ? scope.$eval(attrs.items) : []; };
        var toItems = function (el) {
            return el.toDict(false, function(node) {
                for(var prop in node.data) { 
                    node[prop] = node.data[prop];
                }
                delete node.data;
            });
        };
        var onSelect = function(event, data) {
            $timeout(function() {
                scope.selected = searchTree(data.node.key);
                if(typeof(scope.onSelect) === "function") scope.onSelect(scope.selected, scope.items);
            });
        };
        var onDrop = function(node, destiny, hitMode, data) {
            data.otherNode.moveTo(node, data.hitMode);
            scope.items = toItems(tree);
            $timeout(function() {
                scope.selected = searchTree(data.otherNode.key);
            });
        };
        function initTree() {
            element.html("<div></div>");

            $(element).fancytree({
                source: [],
                activate: onSelect,
                init: function () {
                    tree = $(element).fancytree("getTree");
                    tree.visit(function(node){ node.setExpanded(true); });
                    $timeout(function() {
                        scope.items = toItems(tree);
                        if(scope.selected) { scope.selected = searchTree(scope.selected.key); }
                    });
                },
                extensions: ["dnd"],
                dnd: {
                    preventVoidMoves: true,
                    preventRecursiveMoves: true,
                    autoExpandMS: 400,
                    dragStart: function (node, data) {
                        return !node.data.root;
                    },
                    dragEnter: function (node, data) {
                        return true;
                    },
                    dragDrop: function (node, data) {
                        if(canDrop(node, data.otherNode)) {
                            var isBeforeRoot = (data.hitMode === "before" || data.hitMode === "above"|| data.hitMode === "after") && node && node.parent && node.parent.isRoot();
                            if(!isBeforeRoot) {
                                $timeout(function() { onDrop(node, data.otherNode, data.hitMode, data); }, 100);
                                return true;
                            }
                        }
                        return false;
                    }
                }
            });
        }

        scope.$watch('items', function(value){
            if(!tree) initTree();
            tree.reload(scope.items || []);
            $timeout(function(){
                if(scope.items && scope.items.length && scope.selected && scope.selected.key) {
                    scope.selected = searchTree(scope.selected.key);
                }
            });
        }, true);

        scope.$watch('selected', function(value){
            if(value && value.key && value.key !== "null") {
                if(!tree.getActiveNode() || value.key !== tree.getActiveNode().key) {
                    var node = tree.getNodeByKey(value.key);
                    node.setActive(true);                    
                }
            } else {
                if(tree && typeof(tree.getRootNode)=="function" && tree.getRootNode() && tree.getRootNode().children && tree.getRootNode().children.length) {
                    var node = tree.getRootNode().children[0];
                    node.setActive(true);
                }
            }
        });
    }
    
    return {
        restrict: 'E',
        scope: {
            items: '=items',
            selected: '=?selected',
            onSelect: '=?onSelect'
        },
        link: treeLink
    };

});