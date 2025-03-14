
import type { Moment } from "moment-timezone";
import moment from "moment-timezone";

export type LogLevel =
    'trace'
    | 'debug'
    | 'log'
    | 'info'
    | 'warn'
    | 'error'
    | 'fatal'
    ;

export interface Exception {
    message: string
    type?: string
    cause?: Exception
    stackTrace?: StackTraceElement[]
    expanded?: boolean
}

export interface StackTraceElement {
    module?: string
    className: string
    methodName: string
    file: string
    line: number
}

export interface LogEntry {
    level: LogLevel
    time: Moment
    message: string
    exception?: Exception
    expanded?: boolean
}

export function levelIcon(level: LogLevel) {
    switch (level) {
        case 'trace': return 'far-question-circle';
        case 'debug': return 'far-bug';
        case 'log': return 'far-circle';
        case 'info': return 'far-check-circle';
        case 'warn': return 'far-bolt';
        case 'error': return 'far-times-circle';
        case 'fatal': return 'far-skull-crossbones';
    }
}

export function simpleName(type?: string) {
    if (type == null) return null;
    let lastDot = type.lastIndexOf('.');
    return lastDot == -1 ? type : type.substring(lastDot + 1);
}

export function typeLabel(exception: Exception) {
    let name = simpleName(exception.type || 'Error')!;
    let m = name.match(/[A-Z][a-z]+$/);
    if (m != null)
        return m[0];
    else
        return name;
}

export function causes(e: Exception): Exception[] {
    let v: Exception[] = [];
    let node: Exception | undefined = e;
    while (node != null) {
        v.push(node);
        node = node.cause;
    }
    return v;
}

export function parseException(e: any): Exception | undefined {
    if (typeof e == 'object')
        return e as Exception;
    if (e == null) return undefined;

    let s = e.toString();
    let lines = s.split('\n');
    let ex: any = {};
    let start = ex;
    let header = true;

    for (let i = 0; i < lines.length; i++) {
        let line: string = lines[i];
        if (line.startsWith("Caused by: ")) {
            let sub = {};
            ex.cause = sub;
            ex = sub;
            line = line.substring("Caused by: ".length);
            header = true;
        }
        if (header) {
            let _inthread = "Exception in thread ";
            if (line.startsWith(_inthread)) {
                line = line.substring(_inthread.length);
                if (line.startsWith('"')) {
                    line = line.substring(1);
                    let nextQ = line.indexOf('"');
                    if (nextQ == -1) throw "Parse error: unbalanced quote: ---> " + line;
                    line = line.substring(nextQ);
                }
            }
            line = line.trim();
            let colon = line.indexOf(": ");
            if (colon == -1) throw "Parse error: invalid exception head line: " + line;
            ex.type = line.substring(0, colon);
            ex.message = line.substring(colon + 2);
            header = false;
        } else {
            line = line.trim();
            if (!line.startsWith("at ")) continue; // ... ## more
            line = line.substring(3);
            let module: string | undefined = undefined;
            let slash = line.indexOf('/');
            if (slash != -1) {
                module = line.substring(0, slash);
                line = line.substring(slash + 1);
            }
            let m = line.match(/^(.*)\.(.*?)\((.*):(\d+)\)$/);
            if (m != null) {
                let [_, className, methodName, file, lineNum] = m;
                let ste: StackTraceElement = {
                    module, className, methodName, file, line: parseInt(lineNum)
                };
                ex.stackTrace ||= [];
                ex.stackTrace.push(ste);
            }
        }
    } // for line
    return start;
}

export class Logger {

    name: string
    logs: LogEntry[]

    constructor(name: string) {
        this.name = name;
    }

    add(level: LogLevel, message: string, exception?: string | Exception) {
        let time = moment();
        let entry: LogEntry = {
            level, time, message,
        };
        if (exception != null)
            entry.exception = parseException(exception);
    }
    info(s: string) {
        this.add("info", s);
    }
}

export function showError(s: string) {
    console.error(s);
}

export const logsExample: LogEntry[] = [
    { level: "trace", time: moment(), message: "a deep trace..." },
    { level: "debug", time: moment(), message: "-- check out this code line" },
    { level: "log", time: moment(), message: "a normal operation have been done" },
    { level: "info", time: moment(), message: "Hello, world!" },
    { level: "warn", time: moment(), message: "invalid operation is ignored!" },
    {
        level: "error", time: moment(), message: "Oh, damn god, what happened?",
        exception: {
            message: 'something very strange',
            type: 'my.company.VeryStrangeException',
            cause: {
                message: 'I/O error when doing wonderful things',
                type: 'java.io.IOException',
                stackTrace: [
                    { className: 'my.inner.Class1', methodName: 'innerMethod1', file: 'Inner1.Java', line: 123 },
                    { className: 'my.inner.Class2', methodName: 'innerMethod2', file: 'Inner2.Java', line: 456 },
                ]
            },
            stackTrace: [
                { className: 'my.demo.Class1', methodName: 'method1', file: 'Class1.Java', line: 138 },
                { className: 'my.demo.Class2', methodName: 'method2', file: 'Class2.Java', line: 69 },
            ],
        }
    },
    { level: "fatal", time: moment(), message: "system broken!" },
];

export function _throw(exception: any) {
    console.log('exception thrown: ' + exception);
    throw new Error(exception);
}
