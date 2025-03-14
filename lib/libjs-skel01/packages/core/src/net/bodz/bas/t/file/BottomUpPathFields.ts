import type { int } from '../../../../../lang/basetype';
import type { IJsonForm } from '../../../../../lang/ITypeInfo';
import type { IPathFields } from './IPathFields';
import Strings from '../../c/string/Strings';
import StringStat from '../../c/string/StringStat';
import Split from '../../t/tuple/Split';

export class BottomUpPathFields implements IPathFields, IJsonForm {

    _dirName?: string;
    _name: string;          // without extension
    _extension?: string;

    __pathCache?: string;
    __fileNameCache: string;

    constructor();
    constructor(o?: BottomUpPathFields);
    constructor(o?: IPathFields);
    constructor(v?: any) {
        if (v != null) {
            if (v instanceof BottomUpPathFields) {
                let o = v as BottomUpPathFields;
                this._dirName = o._dirName;
                this._name = o._name;
                this._extension = o._extension;
                this.__pathCache = o.__pathCache;
                this.__fileNameCache = o.__fileNameCache;
            } else {
                this.path = v.path;
            }
        } else {
            this._name = '';
            this.__fileNameCache = '';
        }
    }

    getField(index: int): string | undefined {
        let range = Strings.selectToken(this.__pathCache!, index, '/');
        if (range == null)
            return undefined;
        let token = this.__pathCache!.substring(range.begin, range.end);
        return token;
    }

    setField(index: int, field?: string): void {
        let range = Strings.selectToken(this.__pathCache!, index, '/');
        let left_ = this.__pathCache!.substring(0, range.begin);
        let _right = this.__pathCache!.substring(range.end);
        let rename = left_ + field + _right;
        this.__pathCache = rename;
    }

    get length(): int {
        let slashCount = StringStat.count(this.__pathCache!, '/');
        return slashCount;
    }

    get path(): string | undefined {
        return this.__pathCache;
    }
    set path(val: string | undefined) {
        this.setPath(val!);
    }

    setPath(path: string): void;
    setPath(dirName: string, fileName: string): void;
    setPath(dirName: string, name: string, extension?: string): void;
    setPath(o: IPathFields): void;
    setPath(o: BottomUpPathFields): void;

    setPath(o_path: IPathFields | BottomUpPathFields | string, name_file?: string, extension?: string): void {
        if (o_path instanceof BottomUpPathFields) {
            let o = o_path as BottomUpPathFields;
            this._dirName = o._dirName;
            this._name = o._name;
            this._extension = o._extension;
            this.__pathCache = o.__pathCache;
            this.__fileNameCache = o.__fileNameCache;
        } else if (typeof o_path == 'object') {
            let o = o_path as IPathFields;
            this._dirName = o.dirName;
            this._name = o.name;
            this._extension = o.extension;
            this.__pathCache = o.path;
            this.__fileNameCache = o.fileName;
        } else {
            if (name_file == null) {
                let path = o_path as string;
                if (this.__pathCache != path) {
                    if (path == null) {
                        this._dirName = this._extension = undefined;
                        this._name = '';
                        this.__pathCache = undefined;
                        this.__fileNameCache = '';
                    } else {
                        let dirBase = Split.dirBase(path);
                        this._dirName = dirBase.a;
                        this._fileName(dirBase.b);
                        this.__pathCache = path;
                    }
                }
            }
            else if (extension == null) {
                let dirName = o_path as string;
                let fileName = name_file;
                if (this._dirName != dirName
                    || this.__fileNameCache != fileName) {
                    this._dirName = dirName;
                    this._fileName(fileName);
                    this.updatePath();
                }
            }
            else {
                this._dirName = o_path;
                this._name = name_file;
                this._extension = extension;
                this.updateFileName();
            }
        }
    }

    updatePath(): void {
        let path = this.__fileNameCache;
        if (this._dirName != null)
            path = this._dirName + "/" + this.__fileNameCache;
        this.__pathCache = path;
    }

    toArray(): string[] {
        if (this.__pathCache == null)
            return [];
        let array = this.__pathCache.split("/");
        return array;
    }

    get dirName(): string | undefined {
        return this._dirName;
    }
    set dirName(val: string | undefined) {
        if (this._dirName != val) {
            this._dirName = val;
            this.updatePath();
        }
    }

    get fileName(): string {
        return this.__fileNameCache;
    }
    set fileName(val: string) {
        this._fileName(val!);
        this.updatePath();
    }

    _fileName(fileName: string): void {
        if (this.__fileNameCache == fileName)
            return;
        let nameExt = Split.nameExtension(fileName);
        this._name = nameExt.a;
        this._extension = nameExt.b;
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

    get name(): string {
        return this._name;
    }
    set name(val: string) {
        if (this._name != val) {
            this._name = val;
            this.updateFileName();
        }
    }

    get extension(): string | undefined {
        return this._extension;
    }
    set extension(val: string | undefined) {
        if (this._extension != val) {
            this._extension = val;
            this.updateFileName();
        }
    }

    toString(): string {
        return this.__pathCache || '';
    }

    get wantObjectContext(): boolean {
        return true;
    }

    toJson(): any {
        let a = {} as any;
        this.jsonOut(a);
        return a;
    }

    jsonIn(o: any): void {
        if (o.path != null) {
            this.setPath(o.path);
        } else {
            if (o.fileName != null) {
                this.setPath(o.dirName, o.fileName);
            } else {
                this.setPath(o.dirName, o.name, o.extension);
            }
        }
    }

    jsonOut(context: any) {
        context.dirName = this._dirName;
        context.name = this._name;
        context.extension = this._extension;
    }

}

export default BottomUpPathFields;
