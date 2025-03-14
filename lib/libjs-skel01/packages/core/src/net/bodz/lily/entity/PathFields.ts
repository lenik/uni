import { int } from "../../../../lang/basetype";

export interface IPathFields {

    get path(): string | undefined;
    set path(path: string | undefined);

    setPath(dirName?: string, fileName?: string): void;
    setPath(dirName?: string, name?: string, extension?: string): void;

    toArray(): string[];

    get length(): int;

    getField(index: int): string | undefined;
    setField(index: int, field: string | undefined): void;

    get dirName(): string | undefined;
    set dirName(dirName: string | undefined);

    get fileName(): string | undefined;
    set fileName(fileName: string | undefined);

    get name(): string | undefined;
    set name(name: string | undefined);

    get extension(): string | undefined;
    set extension(extension: string | undefined);
    getExtension(fallback: string): string;

}

export abstract class AbstractPathFields implements IPathFields {

    abstract get path(): string | undefined;
    abstract set path(path: string | undefined);

    abstract setPath(dirName?: string, fileName?: string): void;
    abstract setPath(dirName?: string, name?: string, extension?: string): void;

    abstract toArray(): string[];

    abstract get length(): int;

    abstract getField(index: int): string | undefined;
    abstract setField(index: int, field: string | undefined): void;

    abstract get dirName(): string | undefined;
    abstract set dirName(dirName: string | undefined);

    abstract get fileName(): string | undefined;
    abstract set fileName(fileName: string | undefined);

    abstract get name(): string | undefined;
    abstract set name(name: string | undefined);

    abstract get extension(): string | undefined;
    abstract set extension(extension: string | undefined);
    abstract getExtension(fallback: string): string;

}

abstract class BottomUpPathFields
    extends AbstractPathFields
    implements IPathFields {

    _dirName?: string;
    _name?: string; // without extension
    _extension?: string;

    __pathCache?: string;
    __fileNameCache?: string;

    constructor();
    constructor(o: IPathFields);
    constructor(o: BottomUpPathFields);
    constructor(o?: any) {
        super();
        if (o instanceof BottomUpPathFields) {
            this._dirName = o._dirName;
            this._name = o._name;
            this._extension = o._extension;
            this.__pathCache = o.__pathCache;
            this.__fileNameCache = o.__fileNameCache;
        } else if (o != null) {
            this._dirName = o.dirName;
            this._name = o.name;
            this._extension = o.extension;
            this.__pathCache = o.path;
            this.__fileNameCache = o.fileName;
        } else {
        }
    }

    override     get path(): string | undefined {
        return this.__pathCache;
    }
    override   set path(path: string | undefined) {
        if (this.__pathCache != path) {
            if (path == null) {
                this._dirName = this._name = this._extension = undefined;
                this.__pathCache = this.__fileNameCache = undefined;
            } else {
                let lastSlash = path.lastIndexOf("/");
                this._dirName = lastSlash == -1 ? undefined : path.substring(0, lastSlash);
                let base = lastSlash == -1 ? path : path.substring(lastSlash + 1);
                this._fileName(base);
                this.__pathCache = path;
            }
        }
    }

    override   setPath(dirName?: string, name?: string, extension?: string): void {
        if (extension == null) {
            if (name == null)
                throw new Error('null name');
            let fileName = name;
            if (this._dirName != dirName || this.__fileNameCache != fileName) {
                this._dirName = dirName;
                this._fileName(fileName);
                this.updatePath();
            }
        } else {
            this._dirName = dirName;
            this._name = name;
            this._extension = extension;
            this.updateFileName();
        }
    }

    private updatePath() {
        let path = this.__fileNameCache;
        if (this._dirName != null)
            path = this._dirName + "/" + this.__fileNameCache;
        this.__pathCache = path;
    }

    override  toArray(): string[] {
        if (this.__pathCache == null)
            return [];
        let array = this.__pathCache.split("/");
        return array;
    }

    override  get length(): int {
        let slashCount = StringStat.count(this.__pathCache, '/');
        return slashCount;
    }

    override   getField(index: int): string | undefined {
        PosRange range = Strings.selectToken(this.__pathCache, '/', index);
        if (range == null)
            return null;
        let token = this.__pathCache.substring(range.begin, range.end);
        return token;
    }
    override    setField(index: int, field: string): void {
        let range = Strings.selectToken(this.__pathCache, '/', index);
        let left_ = this.__pathCache.substring(0, range.begin);
        let _right = this.__pathCache.substring(range.end);
        let rename = left_ + field + _right;
        this.__pathCache = rename;
    }

    override   get dirName(): string | undefined {
        return this._dirName;
    }
    override   set dirName(dirName: string | undefined) {
        if (this._dirName != dirName) {
            this._dirName = dirName;
            this.updatePath();
        }
    }

    override     get fileName(): string | undefined {
        return this.__fileNameCache;
    }
    override   set fileName(fileName: string | undefined) {
        if (fileName == null)
            throw new Error('null fileName');
        this._fileName(fileName);
        this.updatePath();
    }

    private _fileName(fileName: string) {
        if (this.__fileNameCache == fileName)
            return;
        let lastDot = fileName.lastIndexOf(".");
        this._name = lastDot == -1 ? fileName : fileName.substring(0, lastDot);
        this._extension = lastDot == -1 ? undefined : fileName.substring(lastDot + 1);
        this.__fileNameCache = fileName;
    }

    updateFileName(): void {
        let fileName = this._name;
        if (this._extension != null) {
            if (this._name == null)
                fileName = "." + this._extension;
            else
                fileName = this._name + "." + this._extension;
        }
        this.__fileNameCache = fileName;
        this.updatePath();
    }

    override   get name(): string | undefined {
        return this._name;
    }
    override  set name(name: string | undefined) {
        if (this._name != name) {
            this._name = name;
            this.updateFileName();
        }
    }

    override  get extension(): string | undefined {
        return this._extension;
    }
    override  set extension(extension: string | undefined) {
        if (this._extension != extension) {
            this._extension = extension;
            this.updateFileName();
        }
    }
    override    getExtension(fallback: string): string {
        return this.extension || fallback;
    }

    override   toString(): string {
        return this.__pathCache || '(undefined)';
    }

}
