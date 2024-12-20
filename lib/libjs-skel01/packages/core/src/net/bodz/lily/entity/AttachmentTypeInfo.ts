import TypeInfo from "../../../../lang/TypeInfo";
import Attachment from "./Attachment";

export class AttachmentTypeInfo extends TypeInfo<Attachment> {

    get name(): string { return 'IAttachment'; }
    get icon(): string { return 'far-image'; }
    get label(): string { return 'Entity Attachment'; }

    parse(s: string): Attachment {
        let jv = JSON.parse(s);
        return this.fromJson(jv);
    }

    format(val: Attachment): string {
        let jv = this.toJson(val);
        return JSON.stringify(jv);
    }

    fromJson(jv: any): Attachment {
        return new Attachment(jv);
    }

    toJson(val: Attachment) {
        return {
            path: val.path,
            fileName: val.fileName,
            name: val.name,
            extension: val.extension,
            sha1: val.sha1,
        };
    }

    static readonly INSTANCE = new AttachmentTypeInfo();
}

export default AttachmentTypeInfo;