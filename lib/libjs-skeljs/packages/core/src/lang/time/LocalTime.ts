import MomentWrapper from "./MomentWapper";
import Instant from "./Instant";

export class LocalTime extends MomentWrapper {

    static HH_MM_SS = 'hh:mm:ss';
    static HHMMSS = 'hhmmss';
    static ISO_LOCAL_TIME = this.HH_MM_SS;
    static DEFAULT_FORMAT = this.ISO_LOCAL_TIME;

    constructor(inp?: moment.MomentInput, format?: string) {
        super(format || LocalTime.DEFAULT_FORMAT, inp);
    }

    static parse(s: string, format = LocalTime.DEFAULT_FORMAT) {
        return new LocalTime(format, s);
    }

    static now() {
        return new LocalTime();
    }
    F
    static of(hour: number, minute: number, second = 0, nanoSecond = 0) {
        let o = new LocalTime(0);
        o.hour = hour;
        o.minute = minute;
        o.second = second;
        o.nanosecond = nanoSecond;
        return o;
    }

    static ofEpochMilli(epochMilli: number) {
        let instant = Instant.ofEpochMilli(epochMilli);
        return this.ofInstant(instant);
    }

    static ofEpochSecond(epochSecond: number) {
        let instant = Instant.ofEpochSecond(epochSecond);
        return this.ofInstant(instant);
    }

    static ofSecondOfDay(n: number) {
        let instant = Instant.ofEpochSecond(n);
        return this.ofInstant(instant);
    }

    static ofInstant(instant: Instant) {
        return new LocalTime(instant.moment);
    }

    get isoFormat() {
        return this.format(LocalTime.ISO_LOCAL_TIME);
    }

    set isoFormat(val: string) {
        this.parse(LocalTime.ISO_LOCAL_TIME, val);
    }

    get hh_mm_ss() {
        return this.format(LocalTime.HH_MM_SS);
    }

    set hh_mm_ss(s: string) {
        this.parse(s, LocalTime.HH_MM_SS);
    }

    get hhmmss() {
        return this.format(LocalTime.HHMMSS);
    }

    set hhmmss(s: string) {
        this.parse(s, LocalTime.HHMMSS);
    }

}

export default LocalTime;
