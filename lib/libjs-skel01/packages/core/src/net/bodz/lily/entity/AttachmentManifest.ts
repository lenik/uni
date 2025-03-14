import type { int } from "../../../../lang/basetype";
import type { IJsonForm } from "../../../../lang/ITypeInfo";
import type { IPathFields } from "../../bas/t/file/IPathFields";
import BottomUpBackedFile from "../../bas/t/file/BottomUpPathFields";
import AttachmentManifestTypeInfo from "./AttachmentManifestTypeInfo";
import { Attachment, $Attachment } from "./Attachment";
// import { IExample } from "skel01-core/src/lang/basetype";

export interface $ImageLike {
    width?: int;
    height?: int;
    format?: string;
    quality?: int;
}

export interface $VideoLike extends $ImageLike {
    videoFormat?: string;
    videoBitRate?: int;
    audioFormat?: string;
    audioBitRate?: int;
}

interface $AttThumbnail extends $Attachment, $ImageLike {
}

interface $AttPreview extends $Attachment, $VideoLike {
}

interface $AttScreenshot extends $Attachment {
}

interface $AttScreenpack extends $ImageLike {
    list: $AttScreenshot[];
}

type $AttThumbnailSet = { [key: string]: $AttThumbnail };
type $AttPreviewSet = { [key: string]: $AttPreview };
type $AttScreenpackSet = { [key: string]: $AttScreenpack };

export interface $AttachmentManifest {
    data: $Attachment[];
    thumb?: ($AttThumbnailSet | undefined)[];
    preview?: ($AttPreviewSet | undefined)[];
    screen?: ($AttScreenpackSet | undefined)[];
}

///////

export interface IImageLike {
    get width(): int;
    set width(value: int);
    get height(): int;
    set height(value: int);
    get format(): string | undefined;
    set format(value: string | undefined);
    get quality(): int;
    set quality(value: int);
}

export interface IVideoLike extends IImageLike {
    get videoFormat(): string | undefined;
    set videoFormat(value: string | undefined);
    get videoBitRate(): int;
    set videoBitRate(value: int);
    get audioFormat(): string | undefined;
    set audioFormat(value: string | undefined);
    get audioBitRate(): int;
    set audioBitRate(value: int);
}

class AttImageLike extends BottomUpBackedFile implements IPathFields, IImageLike {

    _width: int;
    _height: int;
    _format?: string;
    _quality: int;

    get width(): int { return this._width; }
    set width(value: int) { this._width = value; }
    get height(): int { return this._height; }
    set height(value: int) { this._height = value; }
    get format(): string | undefined { return this._format; }
    set format(value: string | undefined) { this._format = value; }
    get quality(): int { return this._quality; }
    set quality(value: int) { this._quality = value; }

    override jsonIn(o: $ImageLike): void {
        super.jsonIn(o);
        this._width = o.width || 0;
        this._height = o.height || 0;
        this._format = o.format;
        this._quality = o.quality || 0;
    }

    override jsonOut(context: $ImageLike): void {
        super.jsonOut(context);
        if (this._width != 0) context.width = this._width;
        if (this._height != 0) context.height = this._height;
        if (this._format != null) context.format = this._format;
        if (this._quality != 0) context.quality = this._quality;
    }
}

export class AttThumbnail extends AttImageLike {
}

export class AttPreview extends AttImageLike implements IVideoLike {

    _videoFormat?: string;
    _videoBitRate: int;
    _audioFormat?: string;
    _audioBitRate: int;

    get videoFormat(): string | undefined { return this._videoFormat; }
    set videoFormat(value: string | undefined) { this._videoFormat = value; }
    get videoBitRate(): int { return this._videoBitRate; }
    set videoBitRate(value: int) { this._videoBitRate = value; }
    get audioFormat(): string | undefined { return this._audioFormat; }
    set audioFormat(value: string | undefined) { this._audioFormat = value; }
    get audioBitRate(): int { return this._audioBitRate; }
    set audioBitRate(value: int) { this._audioBitRate = value; }

    override jsonIn(o: $AttPreview): void {
        super.jsonIn(o);
        this._videoFormat = o.videoFormat;
        this._videoBitRate = o.videoBitRate || 0;
        this._audioFormat = o.audioFormat;
        this._audioBitRate = o.audioBitRate || 0;
    }

    override jsonOut(context: $AttPreview): void {
        super.jsonOut(context);
        if (this._videoFormat != null) context.videoFormat = this._videoFormat;
        if (this._videoBitRate != 0) context.videoBitRate = this._videoBitRate;
        if (this._audioFormat != null) context.audioFormat = this._audioFormat;
        if (this._audioBitRate != 0) context.audioBitRate = this._audioBitRate;
    }

}

export class AttScreenshot extends BottomUpBackedFile { }

export class AttScreenpack implements IImageLike, IJsonForm {

    _width: int;
    _height: int;
    _format?: string;
    _quality: int;

    list: AttScreenshot[];

    get width(): int { return this._width; }
    set width(value: int) { this._width = value; }
    get height(): int { return this._height; }
    set height(value: int) { this._height = value; }
    get format(): string | undefined { return this._format; }
    set format(value: string | undefined) { this._format = value; }
    get quality(): int { return this._quality; }
    set quality(value: int) { this._quality = value; }

    get wantObjectContext(): boolean {
        return true;
    }

    toJson() {
        let a = {} as any;
        this.jsonOut(a);
        return a;
    }

    jsonIn(o: $AttScreenpack): void {
        this._width = o.width || 0;
        this._height = o.height || 0;
        this._format = o.format;
        this._quality = o.quality || 0;

        this.list = [];
        for (let el of o.list) {
            let item = new AttScreenshot();
            item.jsonIn(el);
            this.list.push(item);
        }
    }

    jsonOut(context: $AttScreenpack): void {
        if (this._width != 0) context.width = this._width;
        if (this._height != 0) context.height = this._height;
        if (this._format != null) context.format = this._format;
        if (this._quality != 0) context.quality = this._quality;

        context.list = [];
        for (let el of this.list) {
            context.list.push(el.toJson());
        }
    }
}

export type AttThumbnailSet = { [key: string]: AttThumbnail; };
export type AttPreviewSet = { [key: string]: AttPreview; };
export type AttScreenpackSet = { [key: string]: AttScreenpack; };

export class AttachmentManifest implements IJsonForm {

    static get TYPE() {
        return AttachmentManifestTypeInfo.INSTANCE;
    }

    data: Attachment[] = [];

    get(index: int) {
        return this.data[index]
    }

    filter(fn: (a: Attachment) => boolean): Attachment[];
    filter(fn: (a: Attachment) => boolean, replacement: Attachment[]): Attachment[];
    filter(fn: (a: Attachment) => boolean, replacement?: Attachment[]) {
        if (replacement == null)
            return this.data.filter(fn);

        // let v = this.data.filter(a => !fn(a));
        // v.push(...replacement);

        // preserver the order:
        let newList = [...replacement] as (Attachment | undefined)[]
        let v = [] as Attachment[];
        for (let a of this.data) {
            if (fn(a)) {
                let i = newList.indexOf(a);
                if (i == -1)
                    // not in the new list, remove it.
                    continue;
                else
                    newList[i] = undefined;
            }
            v.push(a);
        }
        for (let a of newList)
            if (a != undefined)
                v.push(a);
        this.data = v;
        return v;
    }

    find(index: int, fn: (a: Attachment) => boolean): Attachment | undefined;
    find(index: int, fn: (a: Attachment) => boolean, replacement: Attachment): int;
    find(index: int, fn: (a: Attachment) => boolean, replacement: Attachment, autoAppend: boolean): int;
    find(index: int, fn: (a: Attachment) => boolean, replacement?: Attachment, autoAppend = false): Attachment | undefined | int {
        let i = 0;
        for (let a of this.data) {
            if (fn(a))
                if (i == index) {
                    if (replacement == undefined) {
                        return a;
                    } else {
                        this.data[i] = replacement;
                        return i;
                    }
                } else {
                    i++;
                }
        }
        if (replacement == undefined)
            return undefined;
        if (autoAppend) {
            this.data.push(replacement);
            return this.data.length - 1;
        }
        return -1;
    }

    get wantObjectContext(): boolean {
        return true;
    }

    jsonIn(jv: $AttachmentManifest): void {
        this.data.length = 0;
        if (Array.isArray(jv.data))
            for (let child of jv.data) {
                let item = new Attachment(child);
                this.data.push(item);
            }

        if (Array.isArray(jv.thumb))
            for (let i = 0; i < jv.thumb.length; i++) {
                let jset = jv.thumb[i];
                if (jset == null) continue;
                let set = {} as AttThumbnailSet;
                for (let key in jset) {
                    let a = new AttThumbnail();
                    a.jsonIn(jset[key]);
                    set[key] = a;
                }
                this.data[i].setView('thumb', set);
            }

        if (Array.isArray(jv.preview))
            for (let i = 0; i < jv.preview.length; i++) {
                let jset = jv.preview[i];
                if (jset == null) continue;
                let set = {} as AttPreviewSet;
                for (let key in jset) {
                    let a = new AttPreview();
                    a.jsonIn(jset[key]);
                    set[key] = a;
                }
                this.data[i].setView('preview', set);
            }

        if (Array.isArray(jv.screen))
            for (let i = 0; i < jv.screen.length; i++) {
                let jset = jv.screen[i];
                if (jset == null) continue;
                let set = {} as AttScreenpackSet;
                for (let key in jset) {
                    let a = new AttScreenpack();
                    a.jsonIn(jset[key]);
                    set[key] = a;
                }
                this.data[i].setView('screen', set);
            }
    }

    jsonOut(context: $AttachmentManifest) {
        if (this.data == null) return;
        context.data = [];
        for (let item of this.data) {
            let child = {} as $Attachment;
            item.jsonOut(child);
            context.data.push(child);
        }

        let thumb = [] as ($AttThumbnailSet | undefined)[];
        for (let i = 0; i < this.data.length; i++) {
            let item = this.data[i];
            let set = item.getView('thumb') as AttThumbnailSet | undefined;
            if (set) {
                let jset = {} as { [key: string]: $AttThumbnail };
                for (let key in set)
                    jset[key] = set[key].toJson();
                thumb.push(jset);
            } else {
                thumb.push(undefined);
            }
        }
        context.thumb = thumb;

        let preview = [] as ($AttPreviewSet | undefined)[];
        for (let i = 0; i < this.data.length; i++) {
            let item = this.data[i];
            let set = item.getView('preview') as AttPreviewSet | undefined;
            if (set) {
                let jset = {} as { [key: string]: $AttPreview };
                for (let key in set)
                    jset[key] = set[key].toJson();
                preview.push(jset);
            } else {
                preview.push(undefined);
            }
        }
        context.preview = preview;

        let screen = [] as ($AttScreenpackSet | undefined)[];
        for (let i = 0; i < this.data.length; i++) {
            let item = this.data[i];
            let set = item.getView('screen') as AttScreenpackSet | undefined;
            if (set) {
                let jset = {} as { [key: string]: $AttScreenpack };
                for (let key in set)
                    jset[key] = set[key].toJson();
                screen.push(jset);
            } else {
                screen.push(undefined);
            }
        }
        context.screen = screen;
    }

}

export default AttachmentManifest;
