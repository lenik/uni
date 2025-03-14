// import { inspect, InspectOptions } from "node:util";
import { int } from '../../lang/basetype';
import { LocalDateTime } from '../../lang/time';

type LineType = 'group' | 'out' | 'in' | 'error' | 'warn' | 'info' | 'log' | 'debug' | 'trace';

interface Line {
    time: LocalDateTime
    type: LineType
    message?: string
    fields: any[]
    indentLevel: int
    table?: boolean
}

class Group {

    lines: Line[] = []

    push(line: Line) {
        this.lines.push(line);
    }

    clear() {
        this.lines = [];
    }

    splice(start: number, deleteCount?: number) {
        return this.lines.splice(start, deleteCount);
    }

}

function flatten(g: Group, all: Line[]) {
    for (let line of g.lines) {
        if (line.type == 'group') {
            for (let field of line.fields) {
                flatten(field, all);
            }
        } else {
            all.push(line);
        }
    }
}

export class BufferedConsole implements Console {

    Console: console.ConsoleConstructor;

    root: Group
    stack: Group[] = []

    counters: any = {}
    timers: any = {}
    profiles: any = {}

    value: any

    constructor() {
        this.root = new Group();
        this.stack.push(this.root);
    }

    get top() {
        let top = this.stack[this.stack.length - 1];
        return top;
    }

    get indentLevel() {
        return this.stack.length - 1;
    }

    get indent() {
        return ' '.repeat(this.indentLevel * 4);
    }

    flatten() {
        let all: Line[] = [];
        flatten(this.root, all);
        return all;
    }

    count(label?: string): int {
        label ||= 'default';
        let n: int;
        if (this.counters[label] == null)
            n = this.counters[label] = 1;
        else
            n = this.counters[label]++;
        this.println(label + ': ' + n);
        return n;
    }

    countReset(label: any): int {
        label ||= 'default';
        let last = this.counters[label];
        delete this.counters[label];
        return last;
    }

    time(label: any): void {
        label ||= 'default';
        let time = process.hrtime.bigint();
        this.timers[label] = time;
    }

    timeLog(label: any, ...data: any[]): void {
        label ||= 'default';
        let time = process.hrtime.bigint();
        let start = this.timers[label];
        let d = start == null ? 0 : (time - start);
        this.println(label + ': ' + d);
    }

    timeEnd(label: any): void {
        label ||= 'default';
        if (this.timers[label] == undefined) {
            throw new Error(`Timer '${label}' does not exist`);
        }
        this.timeLog(label);
        delete this.timers[label];
    }

    timeStamp(label: any): void {
        /* 
            This method does not display anything unless used in the inspector.
            The console.timeStamp() method adds an event with the label 'label'
            to the Timeline panel of the inspector.
         */
    }

    dir(obj: any, options?: /*InspectOptions*/any): void {
        (console as any).inspect(obj, options);
    }

    dirxml(...data: any[]): void {
        this.log(...data);
    }

    group(...label: any[]): void {
        this.println(...label);
        let sub = new Group();
        let container: Line = {
            time: LocalDateTime.now(),
            type: 'group',
            message: '',
            fields: [sub],
            indentLevel: this.indentLevel
        };
        this.top.push(container);
        this.stack.push(sub);
    }

    groupCollapsed(...label: any[]): void {
        this.group();
    }

    groupEnd(): void {
        if (this.stack.length == 0)
            throw new Error('Cannot call groupEnd() without a matching group()');
        // let top = 
        this.stack.pop();
    }

    profile(label?: string): void {
        label ||= 'default';
        this.profiles[label] = 1;
    }
    profileEnd(label?: string): void {
        label ||= 'default';
        /*
            This method does not display anything unless used in the inspector.
            The console.profile() method starts a JavaScript CPU profile with an
            optional label until console.profileEnd() is called. The profile is
            then added to the Profile panel of the inspector.
        */
        delete this.profiles[label];
    }

    addLine(type: LineType, message?: string, ...args: any[]): void {
        let line: Line = {
            time: LocalDateTime.now(),
            type: type,
            message: message,
            fields: args,
            indentLevel: this.indentLevel
        };
        this.top.push(line);
    }

    table(tabularData?: any, properties?: readonly string[]): void {
        let line: Line = {
            time: LocalDateTime.now(),
            type: 'out',
            message: '',
            fields: tabularData,
            table: true,
            indentLevel: this.indentLevel
        };
        this.top.push(line);
    }

    assert(value: any, message?: string, ...optionalParams: any[]): void {
        if (!value) {
            this.error(message, ...optionalParams);
            throw new Error('Assert failure: ' + message);
        }
    }

    error(message?: string, ...optionalParams: any[]): void {
        this.addLine('error', message, ...optionalParams);
    }
    warn(message?: string, ...optionalParams: any[]): void {
        this.addLine('warn', message, ...optionalParams);
    }
    info(message?: string, ...optionalParams: any[]): void {
        this.addLine('info', message, ...optionalParams);
    }
    log(message?: string, ...optionalParams: any[]): void {
        this.addLine('log', message, ...optionalParams);
    }
    debug(message?: string, ...optionalParams: any[]): void {
        this.addLine('debug', message, ...optionalParams);
    }
    trace(message?: string, ...optionalParams: any[]): void {
        this.addLine('trace', message, ...optionalParams);
    }

    println(...args: any[]) {
        this.out(...args);
    }

    out(...args: any[]) {
        this.addLine('out', ...args);
    }

    in(...args: any[]) {
        this.addLine('in', ...args);
    }

    clear() {
        this.top.clear();
    }

    remove(i: int) {
        this.top.splice(i, 1);
    }

    set(value: any) {
        this.value = value;
    }

}

export interface App {
    main(): void;
}

export default BufferedConsole;
