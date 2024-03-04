import { MomentWrapper, MomentWrapperType } from "./MomentWapper";
import Instant from "./Instant";
import LocalDate from "./LocalDate";

export class LocalDateTimeType extends MomentWrapperType<LocalDateTime>{

    get name() { return "LocalDateTime"; }
    get icon() { return "far-clock"; }
    get description() { return "a local date time."; }

    override create(): LocalDateTime {
        return LocalDateTime.now();
    }

}

export class LocalDateTime extends MomentWrapper {

    static DATE_TIME = 'YYYY-MM-DD hh:mm:ss';
    static ISO_LOCAL_DATE_TIME = 'YYYY-MM-DDThh:mm:ss';
    static DEFAULT_FORMAT = this.ISO_LOCAL_DATE_TIME;

    constructor(inp?: moment.MomentInput, format?: string) {
        super(format || LocalDateTime.DEFAULT_FORMAT, inp);
    }

    static parse(s: string, format = LocalDateTime.DEFAULT_FORMAT) {
        return new LocalDateTime(s, format);
    }

    static now() {
        return new LocalDateTime();
    }

    static of(year: number, month: number, dayOfMonth: number, hour: number, minute: number, second = 0, nanoSecond = 0) {
        let o = new LocalDateTime(0);
        o.year = year;
        o.month = month;
        o.dayOfMonth = dayOfMonth;
        o.hour = hour;
        o.minute = minute;
        o.second = second;
        o.nanosecond = nanoSecond;
        return o;
    }

    static ofEpochDay(epochDay: number) {
        let epochSecond = epochDay * 24 * 60 * 60;
        return this.ofEpochSecond(epochSecond);
    }

    static ofEpochSecond(epochSecond: number) {
        let instant = Instant.ofEpochSecond(epochSecond);
        return this.ofInstant(instant);
    }

    static ofInstant(instant: Instant) {
        return new LocalDateTime(instant.moment);
    }

    get isoFormat() {
        return this.format(LocalDateTime.ISO_LOCAL_DATE_TIME);
    }
    set isoFormat(val: string) {
        this.parse(LocalDateTime.ISO_LOCAL_DATE_TIME, val);
    }

    get date_time() {
        return this.format(LocalDateTime.DATE_TIME);
    }
    set date_time(val: string) {
        this.parse(LocalDateTime.DATE_TIME, val);
    }

}

export default LocalDateTime;
