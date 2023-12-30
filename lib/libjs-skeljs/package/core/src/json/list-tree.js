
export function convertTree(list, idProp, parentIdProp) {
    if (idProp == null) idProp = "id";
    if (parentIdProp == null) parentIdProp = "parent.id";

    var map = {};
    var roots = [];

    if (list != null) {
        list.forEach(function (item, i) {
            var id = item[idProp];
            map[id] = item;
            item.children = [];
            item.childmap = {};
            item.xmap = {}; // descendants
        });

        list.forEach(function (item, i) {
            var parentId = item[parentIdProp];
            item.parent = map[parentId];
            if (item.parent == null) {
                item.root = item;
                roots.push(item);
            } else {
                item.parent.children.push(item);
            }
        });
        
        list.forEach(function (item, i) {
            var id = item[idProp];
            if (item.parent != null) {
                var p = item.parent;
                while (p.parent != null) p = p.parent;
                item.root = p;
                item.root.xmap[id] = item;
            }
        });
    }
    return { map: map, roots: roots };
}
