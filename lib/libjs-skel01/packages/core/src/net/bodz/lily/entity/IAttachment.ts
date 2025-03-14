// import { int } from '../../../../lang/basetype';

export interface AttachmentRecord {
    path?: string
    fileName?: string
    name?: string
    extension?: string
    sha1: string
}

export function getItemImage(item: AttachmentRecord, id: number | string) {
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

export default IAttachment;
