import { int } from 'skel01-core/src/lang/basetype';

export class PosRange {

    begin: int;
    end: int;

    constructor();
    constructor(begin: int, end: int);

    constructor(begin?: int, end?: int) {
        this.begin = begin || 0;
        this.end = end || 0;
    }

    size(): int {
        return this.end - this.begin;
    }

    run(s: string) {
        return s.substring(this.begin, this.end);
    }
    toString(): string {
        return `[${this.begin}, ${this.end})`;
    }

}

export default PosRange;