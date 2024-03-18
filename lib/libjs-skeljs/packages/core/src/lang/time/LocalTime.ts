import MomentWrapper from "./MomentWapper";
import MomentWrapperType from "./MomentWapperType";
import Instant from "./Instant";
import { HHMMSS, ISO_LOCAL_TIME, UI_TIME } from "./formats";

export class LocalTimeType extends MomentWrapperType<LocalTime>{

    get name() { return "LocalTime"; }
    get icon() { return "far-clock"; }
    get description() { return "a local time."; }

    override create(): LocalTime {
        return LocalTime.now();
    }

}

export class LocalTime extends MomentWrapper {

    static readonly TYPE = new LocalTimeType();
    static readonly DEFAULT_FORMAT = ISO_LOCAL_TIME;

    constructor(inp?: moment.MomentInput, format?: string) {
        super(format || LocalTime.DEFAULT_FORMAT, inp);
    }

    static parse(s: string, format = LocalTime.DEFAULT_FORMAT) {
        return new LocalTime(s, format);
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
        return this.format(ISO_LOCAL_TIME);
    }

    set isoFormat(val: string) {
        this.parse(ISO_LOCAL_TIME, val);
    }

    get uiFormat() {
        return this.format(UI_TIME);
    }

    set uiFormat(s: string) {
        this.parse(s, UI_TIME);
    }

    get hhmmss() {
        return this.format(HHMMSS);
    }

    set hhmmss(s: string) {
        this.parse(s, HHMMSS);
    }

}

export default LocalTime;
