import type { int } from "skel01-core/src/lang/basetype";
import LocalDate from "skel01-core/src/lang/time/LocalDate";
import LocalTime from "skel01-core/src/lang/time/LocalTime";

export class Diary {
    date: LocalDate;
    logs: LogEntry[] = [];

    constructor(date: LocalDate) {
        this.date = date;
    }
}

export interface LogEntry {
    seq?: number;
    date?: LocalDate;
    time: LocalTime;
    head: string;
    lines: string[];
    // structText: any;
}

export async function loadDiaries(baseDir: string, startDate: LocalDate, dayCount: int): Promise<Diary[]> {
    let start = startDate.epochDay;
    let end = start + dayCount - 1;
    let data: Diary[] = [];
    for (let epochDay = start; epochDay <= end; epochDay++) {
        let date = LocalDate.ofEpochDay(epochDay);
        let dateEx = date.format("Y/Y-MM/Y-MM-DD");
        let dateDir = `${baseDir}/by-date/${dateEx}`;
        if (!await io.isDirectory(dateDir)) continue;
        let diary = new Diary(date);
        await readDiary(diary, dateDir);
        data.push(diary);
    }
    return data;
}

const RE_LOGENTRY = /^(?:@(\d+)\s+)?\s*(\d+-\d+-\d+)?\s+(\d+:\d+(?::\d+)?)\s*(.*?)\s*$/;

export async function readDiary(diary: Diary, dateDir: string) {
    let beatLogFile = dateDir + '/beatLog';

    let pro = io.isFile(beatLogFile);
    if (! await io.isFile(beatLogFile))
        return undefined;

    let lines = await io.readLines(beatLogFile, {
        encoding: 'utf-8',
    });

    let entry: LogEntry | undefined = undefined;
    for (let line of lines) {
        let array = RE_LOGENTRY.exec(line);
        if (array != undefined) {
            let [_, seq, date, time, head] = array;
            entry = {
                time: LocalTime.parse(time),
                head: head,
                lines: [],
            };
            if (seq != null)
                entry.seq = Number.parseInt(seq);
            if (date != null)
                entry.date = LocalDate.parse(date);

            diary.logs.push(entry);
        }

        else {
            if (entry == null) // not yet start, skip file header..
                continue;

            entry.lines.push(line);
        }
    }

    for (let entry of diary.logs) {
        let lastLine = undefined;
        for (let line of entry.lines) {

        }

    }
    return diary;
}

const tabSize = 4;

function parse(lines: string[], parent: HTMLElement): HTMLElement {
    let doc = parent.ownerDocument;
    let indents: number[] = [];
    for (let line of lines) {
        let indent = indentSize(line, tabSize);
        let level = indent / tabSize;
        if (indents.length == 0) {
            indents.push(indent);
        } else {
            for (let i = indents.length - 1; i >= 0; i--)
                if (indent >= indents[i])
                    ;
        }
        // doc.createTextNode(data);
        lastLine = line;
        lastIndent = indent;
        lastLevel = level;
    }
    return parent;
}

function insertPoint(sorted: number[], num: number) {
    let l = 0, r = sorted.length;
    if (r == 0) return 0;
    while (r - l > 1) {
        let mid = Math.floor((l + r) / 2);
        let v = sorted[mid];
        if (num < sorted[l])
            return l;
        if (num == sorted[l])
            return l + 1;
        if (num >= sorted[r])
            return r + 1;

    }
}

function indentSize(s: string, tabSize = 4, offset = 0): number {
    let ar = /^(\s*)/.exec(s);
    if (ar == undefined) return 0;
    let [ws] = ar;
    let n = ws.length;
    let width = 0;
    for (let i = 0; i < n; i++) {
        let ch = ws[i];
        switch (ch) {
            case ' ':
                width++;
                offset++;
                break;
            case '\t':
                let pad = tabSize - offset % tabSize;
                width += pad;
                offset += pad;
                break;
            case '\n':
            case '\r':
                offset = 0;
                break;
        }
    }
    return offset;
}
