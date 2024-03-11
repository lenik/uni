import AttachmentTypeInfo from "./AttachmentTypeInfo";
import IAttachment, { getItemImage } from "./IAttachment"

// export { IAttachment };

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
