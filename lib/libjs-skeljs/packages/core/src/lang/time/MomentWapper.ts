import moment, { Moment } from "moment-timezone";

export type ZoneId = ZoneOffset | ZoneRegion;
export type ZoneOffset = number;
export type ZoneRegion = string;

export class MomentWrapper {
    private _moment: Moment
    version = 0
    defaultFormat: string

    constructor(defaultFormat: string, inp?: moment.MomentInput) {
        this.defaultFormat = defaultFormat;
        if (inp instanceof moment) {
            this._moment = inp as Moment;
        } else if (typeof inp == 'string') {
            this._moment = moment();
            this.parse(inp as string);
        } else {
            this._moment = moment(inp);
        }
    }

    get moment(): Moment {
        this.version == 0;
        return this._moment;
    }
    set moment(val: Moment) {
        this._moment = val;
        this.version++;
    }

    get momentConst(): Moment {
        this.version == 0;
        return this._moment;
    }
    get momentMutable(): Moment {
        this.version++;
        return this._moment;
    }

    format(fmt?: string) {
        console.log('fmt');
        return this.momentConst.format(fmt || this.defaultFormat);
    }

    parse(s: string, fmt?: string) {
        this.moment = moment(s, fmt || this.defaultFormat);
        this.version++;
    }

    parseSmart(s: string, ...formats: string[]) {
        let m = MomentWrapper.parseMoment(s, ...formats);
        if (m == null) return false;
        this.moment = m;
        return true;
    }

    static parseMoment(s: string, ...formats: string[]) {
        for (let i = 0; i < formats.length; i++) {
            let fmt = formats[i];
            let m = moment(s, fmt);
            if (m != null) {
                return m;
            }
        }
        return null;
    }

    toString() {
        return this.format(this.defaultFormat);
    }

    get formatted() {
        return this.format();
    }
    set formatted(val: string) {
        this.parse(val);
    }

    get inDate() {
        let tz = moment.tz.guess();
        let m = this.momentConst.clone().tz(tz);
        return m.toDate();
    }
    set inDate(val: Date) {
        let tz = moment.tz.guess();
        this.moment = moment.tz(val, tz);
    }

    get year() { return this.momentConst.year() }
    set year(val: number) { this.momentMutable.year(val) }

    get quarter() { return this.momentConst.quarter() }
    set quarter(val: number) { this.momentMutable.quarter(val) }

    get quarters() { return this.momentConst.quarters() }
    set quarters(val: number) { this.momentMutable.quarters(val) }

    get month() { return this.momentConst.month() }
    set month(val: number) { this.momentMutable.month(val) }

    get month1() { return this.momentConst.month() + 1 }
    set month1(val: number) { this.momentMutable.month(val - 1) }

    get monthName() { return this.momentConst.month + "" }
    set monthName(val: string) { this.momentMutable.month(val) }

    get day() { return this.momentConst.day() }
    set day(val: number) { this.momentMutable.day(val) }

    // get days() { return this.moment_read.days() }
    // set days(val: number) { this.moment_write.days(val) }

    // @deprecate
    get date() { return this.version * 0 + this.momentConst.date() }
    // @deprecate
    set date(val: number) { this.momentMutable.date(val); }

    get dayOfMonth() { return this.date }
    set dayOfMonth(val: number) { this.date = val }

    get daysInMonth() { return this.momentConst.daysInMonth() }

    get hour() { return this.momentConst.hour() }
    set hour(val: number) { this.momentMutable.hour(val) }

    // get hours() { return this.moment_read.hours() }
    // set hours(val: number) { this.moment_write.hours(val) }

    get minute() { return this.momentConst.minute() }
    set minute(val: number) { this.momentMutable.minute(val) }

    // get minutes() { return this.moment_read.minutes() }
    // set minutes(val: number) { this.moment_write.minutes(val) }

    get second() { return this.momentConst.second() }
    set second(val: number) { this.momentMutable.second(val) }

    // get seconds() { return this.moment_read.seconds() }
    // set seconds(val: number) { this.moment_write.seconds(val) }

    get millisecond() { return this.momentConst.millisecond() }
    set millisecond(val: number) { this.momentMutable.millisecond(val) }

    // get milliseconds() { return this.moment_read.milliseconds() }
    // set milliseconds(val: number) { this.moment_write.milliseconds(val) }

    get nanosecond() { return this.momentConst.millisecond() * 1000000 }
    set nanosecond(val: number) { this.momentMutable.millisecond(val / 1000000) }

    get weekday() { return this.momentConst.weekday() }
    set weekday(val: number) { this.momentMutable.weekday(val) }

    get isoWeekday() { return this.momentConst.isoWeekday() }
    set isoWeekday(val: number) { this.momentMutable.isoWeekday(val) }

    get isoWeekdayName() { return this.momentConst.isoWeekday() + '' }
    set isoWeekdayName(val: string) { this.momentMutable.isoWeekday(val) }

    get weekYear() { return this.momentConst.weekYear() }
    set weekYear(val: number) { this.momentMutable.weekYear(val) }

    get isoWeekYear() { return this.momentConst.isoWeekYear() }
    set isoWeekYear(val: number) { this.momentMutable.isoWeekYear(val) }

    get week() { return this.momentConst.week() }
    set week(val: number) { this.momentMutable.week(val) }

    get weeks() { return this.momentConst.weeks() }
    set weeks(val: number) { this.momentMutable.weeks(val) }

    get isoWeek() { return this.momentConst.isoWeek() }
    set isoWeek(val: number) { this.momentMutable.isoWeek(val) }

    get isoWeeks() { return this.momentConst.isoWeeks() }
    set isoWeeks(val: number) { this.momentMutable.isoWeeks(val) }

    get weeksInYear() { return this.momentConst.weeksInYear() }

    get isoWeeksInYear() { return this.momentConst.isoWeeksInYear() }

    get isoWeeksInIsoWeekYear() { return this.momentConst.isoWeeksInISOWeekYear() }

    get dayOfYear() { return this.momentConst.dayOfYear() }
    set dayOfYear(val: number) { this.momentMutable.dayOfYear(val) }

    get epochMilli() { return this.momentConst.valueOf() }
    set epochMilli(val: number) {
        this.moment = moment(val);
        this.version++;
    }

    get epochSecond() { return this.epochMilli / 1000 }
    set epochSecond(val: number) { this.epochMilli = val * 1000 }

    get epochDay() { return this.epochMilli / 1000 / 86400 }
    set epochDay(val: number) { this.epochMilli = val * 1000 * 86400 }

    get isLeapYear() { return this.momentConst.isLeapYear() }
    get isLeapMonth() { return this.momentConst.isLeapYear() && this.month1 == 2 }

    get tz() { return this.momentConst.tz() }
    set tz(val: ZoneRegion | undefined) { if (val != null) this.momentMutable.tz(val) }

    get tzKeepLocal() { return this.momentConst.tz() }
    set tzKeepLocal(val: ZoneRegion | undefined) { if (val != null) this.momentMutable.tz(val, true) }

    get utcOffset() { return this.momentConst.utcOffset() }
    set utcOffset(val: ZoneOffset) { this.momentMutable.utcOffset(val); }
    parseUtcOffset(val: ZoneOffset | string) { this.momentMutable.utcOffset(val); }

    get utcOffsetKeepLocal() { return this.momentConst.utcOffset() }
    set utcOffsetKeepLocal(val: ZoneOffset) { this.momentMutable.utcOffset(val, true); }
    parseUtcOffsetKeepLocal(val: ZoneOffset | string) { this.momentMutable.utcOffset(val, true); }

    get isUtcOffset() { return this.momentConst.isUtcOffset() }
    get isDST() { return this.momentConst.isDST() }
    get zoneAbbr(): ZoneRegion { return this.momentConst.zoneAbbr() }
    get zoneName(): ZoneRegion { return this.momentConst.zoneName() }

    get zoneOffset() {
        if (this.utcOffset == null)
            return null;
        let offset = this.utcOffset;
        let negative = offset < 0;
        if (negative) offset = -offset;
        let s = negative ? '-' : '+';
        let hour = Math.floor(this.utcOffset / 60);
        let min = this.utcOffset % 60;
        if (hour < 10)
            s += '0';
        s += hour;
        s += ':';
        if (min < 10)
            s += '0';
        s += min;
        return s;
    }
    set zoneOffset(s: string | null) {
        if (s == null) {
            this.utcOffset = null!;
            return;
        }
        let negative = s.startsWith('-');
        if (negative || s.startsWith('+'))
            s = s.substring(1);
        let colon = s.indexOf(':');
        let hourstr = colon == -1 ? s : s.substring(0, colon);
        let minStr = colon == -1 ? undefined : s.substring(colon + 1);
        let hour = parseInt(hourstr);
        let min = minStr == null ? 0 : parseInt(minStr);
        let offset = hour * 60 + min;
        if (negative) offset = -offset;
        this.utcOffsetKeepLocal = offset;
    }

    get timeZone() {
        if (this.zoneName?.length)
            return this.zoneName;
        let zoneOffset = this.zoneOffset;
        return zoneOffset;
    }

    set timeZone(tz: string | null) {
        if (!(tz?.length)) {
            this.zoneOffset = null;
            return;
        }
        switch (tz.charAt(0)) {
            case '+':
            case '-':
                this.zoneOffset = tz;
                break;
            default:
                this.tzKeepLocal = tz;
        }
    }

    toDate(): Date {
        const DATE_FORMAT = 'YYYY-MM-DDTHH:mm:ssZ';
        let s = this.format(DATE_FORMAT);
        return new Date(s);
    }

}

let _m = moment();
export const defaultUtcOffset = _m.utcOffset();
export const defaultZoneAbbr = _m.zoneAbbr();
export const defaultZoneName = _m.zoneName();

export default MomentWrapper;
