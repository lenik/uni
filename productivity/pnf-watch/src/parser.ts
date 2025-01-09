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
    let lastLine = undefined;
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
            lastLine = undefined;
        }

        else {
            if (entry == null) // not yet start, skip file header..
                continue;

            entry.lines.push(line);
        }
    }
    return diary;
}
