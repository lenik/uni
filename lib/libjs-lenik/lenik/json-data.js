
function convertTree(list, idProp, parentIdProp) {
    if (idProp == null) idProp = "id";
    if (parentIdProp == null) parentIdProp = "parent.id";

    var map = {};
    var roots = [];

    if (list != null) {
        list.forEach(function (a, i) {
            var id = a[idProp];
            map[id] = a;
            a.children = [];
            a.childmap = {};
            a.xmap = {}; // descendants
        });
        list.forEach(function (a, i) {
            var id = a[idProp];
            var parentId = a[parentIdProp];
            a.parent = map[parentId];
            if (a.parent != null)
                a.root = a.parent.root;
            if (a.parent == null) {
                a.root = a;
                roots.push(a);
            } else {
                a.parent.children.push(a);
                a.root.xmap[id] = a;
            }
        });
    }
    return { map: map, roots: roots };
}
