
interface Properties {
    [key: string]: any
    images?: Attachment[]
}

interface Entity {
    id: any
    properties: Properties;
}

interface Attachment {
    path?: string
    fileName?: string
    name?: string
    extension?: string
    sha1: string
}

interface DataRow {
    [index: number]: any
}

export function getItemImage(item: Attachment, id: any) {
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

export function getRowImage(row: DataRow, indexOfPropsColumn = 1, fallback = ""): string | null {
    var id: any;
    var props;
    id = row[0];
    props = row[indexOfPropsColumn];
    return getFirstImage(id, props, fallback);
}

export function getFirstImage(id: any, props?: Properties, fallback = ""): string | null {
    if (props == null) return fallback;
    var images = props.images;
     if (images == null || images.length == 0) return fallback;
    var first = images[0];
    var href = getItemImage(first, id);
    return href;
}

export function getObjectImage(obj: Entity, fallback?: string) {
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
