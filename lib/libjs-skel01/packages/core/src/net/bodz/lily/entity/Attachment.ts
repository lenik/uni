import type { int } from "../../../../lang/basetype";
import type { IJsonForm } from "../../../../lang/ITypeInfo";
import { isImageName, isVideoName } from "../../../../util/mime";
import type { IPathFields } from "../../bas/t/file/IPathFields";
import BottomUpBackedFile from "../../bas/t/file/BottomUpPathFields";
import AttachmentTypeInfo from "./AttachmentTypeInfo";

export interface $Attachment {
    path?: string;          // [redundant]
    dirName?: string;
    fileName?: string;      // [redundant]
    name: string;           // orig. filename
    extension?: string;     // without dot
    sha1: string;           // real filename [+.ext]

    label?: string;         // AI title extraction + user
    description?: string;   // AI text extraction + user
    tags?: string[];        // AI classification + user
}

export function getItemImage(item: $Attachment, id: number | string): string;
export function getItemImage(item: IAttachment, id: number | string): string;
export function getItemImage(item: any, id: number | string): string | undefined {
    if (item == null)
        return undefined;
    if (item instanceof Attachment)
        return `${id}/attachment/${item.fileName}`;
    let path = item.path;
    if (path == null) {
        let fileName = item.fileName;
        if (fileName == null) {
            let name = item.name;
            if (name == null) {
                if (item.sha1 == null)
                    throw new Error("null sha1");
                name = item.sha1;
            }
            fileName = item.name;
            if (item.extension != null)
                fileName += '.' + item.extension;
        }
        path = id + "/attachment/" + item.fileName;
    }
    return path;
}

export interface IAttachment extends IPathFields {
    get sha1(): string | undefined;

    get label(): string | undefined;
    set label(value: string | undefined);

    get description(): string | undefined;
    set description(value: string | undefined);

    get tags(): string[] | undefined;
    set tags(value: string[] | undefined);

    getView(type: string): any;
    setView(type: string, view: any): void;

}

export class Attachment extends BottomUpBackedFile implements IAttachment, IJsonForm {

    static readonly TYPE = AttachmentTypeInfo.INSTANCE;

    _sha1?: string;

    _label?: string;
    _description?: string;
    _tags?: string[];

    _views: any = {};

    constructor(jv?: any) {
        super();
        if (jv != null)
            this.jsonIn(jv);
    }

    get sha1() { return this._sha1; }

    get label() { return this._label; }
    set label(value: string | undefined) { this._label = value; }

    get description() { return this._description; }
    set description(value: string | undefined) { this._description = value; }

    get tags() { return this._tags; }
    set tags(value: string[] | undefined) { this._tags = value; }

    getView(type: string) { return this._views[type]; }
    setView(type: string, view: any) { this._views[type] = view; }

    idHref(id: number | string): string {
        return getItemImage(this, id)!;
    }

    get wantObjectContext(): boolean {
        return true;
    }

    override jsonIn(jv: $Attachment): void {
        super.jsonIn(jv);
        this._sha1 = jv.sha1;
        this._label = jv.label;
        this._description = jv.description;
        this._tags = jv.tags
    }

    override jsonOut(context: any) {
        super.jsonOut(context);
        context.sha1 = this._sha1;
        context.label = this._label;
        context.description = this._description;
        context.tags = this._tags;
    }

}

export const TYPE_IMAGE = (f: Attachment) => isImageName(f.fileName);
export const TYPE_VIDEO = (f: Attachment) => isVideoName(f.fileName);

export default Attachment;
