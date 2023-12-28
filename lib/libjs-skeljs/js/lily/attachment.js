
class Item {
    constructor() {
    }
    image(id) {
        return getItemImage(this, id);
    }
}

export function getItemImage(item, id) {
    if (item == null)
        return null;
    if (item.path == null) {
        if (item.fileName == null) {
            if (item.name == null)
                item.name = item.sha1;
            item.fileName = item.name;
            if (item.extension != null)
                item.fileName += '.' + item.extension;
        }
        item.path = id + "/attachment/" + item.fileName;
    }
    return item.path;
}

export function getRowImage(rowOrId, keyOrProps, fallback) {
    var id, props;
    if (typeof(rowOrId) == "object") {
        id = rowOrId[0];
        if (keyOrProps == null) keyOrProps = 1;
        props = rowOrId[keyOrProps];
    } else {
        id = rowOrId;
        props = keyOrProps;
    }
    if (fallback == null) fallback = "";
    
    if (props == null) return fallback;
    var images = props.images;
     if (images == null || images.length == 0) return fallback;
    var first = images[0];
    var href = getItemImage(first, id);
    return href;
}

export function getObjectImage(obj, fallback) {
    if (fallback == null) fallback = "";
    var props = obj.properties;
    if (props == null) return fallback;
    var images = props.images;
     if (images == null || images.length == 0) return fallback;
    var first = images[0];
    var id = obj.id;
    var href = getItemImage(first, id);
    return href;
}
