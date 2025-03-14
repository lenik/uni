import type { int } from '../../../../../lang/basetype';
import type { IJsonForm } from '../../../../../lang/ITypeInfo';
import type { IPathFields } from './IPathFields';
import Strings from '../../c/string/Strings';

export class TopDownPathFields implements IPathFields, IJsonForm {

    private _path: string | undefined;

    letructor();
    letructor(o: IPathFields);
    letructor(o?: IPathFields) {
        this._path = o?.path || '';
    }

    get path(): string | undefined {
        return this._path;
    }

    set path(path: string | undefined) {
        this._path = path;
    }

    setPath(dirName: string | undefined, fileName: string | undefined): void;
    setPath(dirName: string | undefined, name: string, extension?: string): void;
    setPath(dirName: string | undefined, name_file?: string, extension?: string): void {
        if (name_file == null) {
            this._path = dirName;
        } else if (extension == null) {
            if (name_file === null)
                throw new Error('fileName cannot be null');
            this._path = dirName === null
                ? name_file
                : `${dirName}/${name_file}`;
        } else {
            let fileName = name_file;
            if (extension !== null) {
                fileName += `.${extension}`;
            }
            this.setPath(dirName, fileName);
        }
    }

    toArray(): string[] {
        if (this._path == null)
            return [];
        else
            return this._path.split('/');
    }

    get length(): int {
        if (this._path == null)
            return 0;
        else
            return this._path.split('/').length - 1;
    }

    getField(index: int): string | undefined {
        if (this._path == null) return undefined;
        let range = Strings.selectToken(this._path, index, '/');
        if (!range) return undefined;
        return this._path.substring(range.begin, range.end);
    }

    setField(index: int, field: string): void {
        if (this._path == null)
            throw new Error("path isn't init");
        let range = Strings.selectToken(this._path, index, '/');
        if (range == null)
            throw new Error("index out of bound: " + index);
        let left = this._path.substring(0, range.begin);
        let right = this._path.substring(range.end);
        this._path = `${left}${field}${right}`;
    }

    get dirName(): string | undefined {
        if (this._path == null) return undefined;
        let lastSlash = this._path.lastIndexOf('/');
        return lastSlash === -1 ? undefined : this._path.substring(0, lastSlash);
    }

    set dirName(dirName: string | undefined) {
        let fileName = this.fileName;
        this.setPath(dirName, fileName);
    }

    get fileName(): string {
        if (this._path == null) return '';
        let lastSlash = this._path.lastIndexOf('/');
        return lastSlash == -1 ? this._path : this._path.substring(lastSlash + 1);
    }

    set fileName(fileName: string) {
        let dirName = this.dirName;
        this.setPath(dirName!, fileName);
    }

    get name(): string {
        let fileName = this.fileName;
        let lastDot = fileName.lastIndexOf('.');
        return lastDot === -1 ? fileName : fileName.substring(0, lastDot);
    }

    set name(name: string) {
        let extension = this.extension;
        let fileName = name;
        if (extension != null) fileName += `.${extension}`;
        let dirName = this.dirName;
        this.setPath(dirName!, fileName);
    }

    get extension(): string | undefined {
        let fileName = this.fileName;
        let lastDot = fileName.lastIndexOf('.');
        return lastDot === -1 ? undefined : fileName.substring(lastDot + 1);
    }

    set extension(extension: string) {
        let fileName = this.name;
        let newFileName = fileName;
        if (extension) newFileName += `.${extension}`;
        let dirName = this.dirName;
        this.setPath(dirName!, newFileName);
    }

    toString(): string {
        return this._path || '';
    }

    get wantObjectContext(): boolean {
        return true;
    }

    jsonIn(o: any): void {
        this.path = o.path;
    }

    jsonOut(context: any) {
        context.path = this.path;
    }

}

export default TopDownPathFields;
