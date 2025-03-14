import { int } from "../../../../../../../../../../../top/job/index/csb24/csb24-web/dir-skel01/packages/core/src/lang/basetype";
import AttachmentTypeInfo from "./AttachmentTypeInfo";
import IAttachment, { getItemImage } from "./IAttachment"

interface IFiles {
    [category: string]: IAttachment[];
}

interface IAttachmentGroup {
    [index: number]: Attachment
}

class AttachmentListing {

    groupMap: IAttachmentListing = {};

    constructor(jv: IAttachmentListing) {
        for (let category in jv) {
            let files = jv[category] as IAttachment[];
            let attachments = files.map(f => new Attachment(f));
            this.groupMap[category] = attachments;
        }
    }

    get groupKeys() {
        return Object.keys(this.groupMap);
    }

    get flatten() {
        let result: Attachment[] = [];
        for (let category in this.groupMap) {
            let group = this.groupMap[category];
            result = result.concat(group);
        }
        return result;
    }

    get(index: int) {
        let pos = 0;
        for (let attachments of Object.values(this.groupMap)) {
            let end = pos + attachments.length;
            if (index >= pos && index < end) {
                return attachments[index - pos];
                pos = end;
            }
            return undefined;
        }

    }

export class Attachment implements IAttachment {

    static readonly TYPE = AttachmentTypeInfo.INSTANCE;

    path?: string
    fileName?: string
    name?: string
    extension?: string
    sha1: string

    constructor(jv?: any) {
        if (jv != null) {
            this.path = jv.path;
            this.fileName = jv.fileName;
            this.name = jv.name;
            this.extension = jv.extension;
            this.sha1 = jv.sha1;
        }
    }

    idHref(id: number | string): string {
        return getItemImage(this, id)!;
    }

}

export default Attachment;
