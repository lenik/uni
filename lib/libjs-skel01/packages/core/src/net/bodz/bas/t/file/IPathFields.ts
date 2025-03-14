import type { int } from "../../../../../lang/basetype";

export interface IPathFields {

    get path(): string | undefined;
    set path(value: string | undefined);

    setPath(dirName: string | undefined, fileName: string): void;
    setPath(dirName: string | undefined, name: string, extension: string): void;

    get length(): int;
    toArray(): string[];

    getField(index: int): string | undefined;
    setField(index: int, field: string | undefined): void;

    get dirName(): string | undefined;
    set dirName(value: string | undefined);

    get fileName(): string;
    set fileName(value: string);

    get name(): string;
    set name(value: string);

    get extension(): string | undefined;
    set extension(value: string | undefined);

}

// export default IPathFields;
