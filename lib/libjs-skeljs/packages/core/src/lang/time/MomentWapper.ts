import moment, { Moment } from "moment-timezone";

export type ZoneId = ZoneOffset | ZoneRegion;
export type ZoneOffset = string | number;
export type ZoneRegion = string;

export class MomentWrapper {
    moment: Moment
    version = 0
    defaultFormat: string

    constructor(defaultFormat: string, inp?: moment.MomentInput) {
        this.defaultFormat = defaultFormat;
        if (inp instanceof moment) {
            this.moment = inp as Moment;
        } else {
            this.moment = moment(inp);
        }
    }

    format(fmt?: string) {
        return this.moment_read.format(fmt || this.defaultFormat);
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
        let m = this.moment.clone().tz(tz);
        return m.toDate();
    }
    set inDate(val: Date) {
        let tz = moment.tz.guess();
        this.moment = moment.tz(val, tz);
    }

    get moment_read(): Moment {
        this.version == 0;
        return this.moment;
    }
    get moment_write(): Moment {
        this.version++;
        return this.moment;
    }

    get year() { return this.moment_read.year() }
    set year(val: number) { this.moment_write.year(val) }

    get quarter() { return this.moment_read.quarter() }
    set quarter(val: number) { this.moment_write.quarter(val) }

    get quarters() { return this.moment_read.quarters() }
    set quarters(val: number) { this.moment_write.quarters(val) }

    get month() { return this.moment_read.month() }
    set month(val: number) { this.moment_write.month(val) }

    get month1() { return this.moment_read.month() + 1 }
    set month1(val: number) { this.moment_write.month(val - 1) }

    get monthName() { return this.moment_read.month + "" }
    set monthName(val: string) { this.moment_write.month(val) }

    get day() { return this.moment_read.day() }
    set day(val: number) { this.moment_write.day(val) }

    // get days() { return this.moment_read.days() }
    // set days(val: number) { this.moment_write.days(val) }

    // @deprecate
    get date() { return this.version * 0 + this.moment_read.date() }
    // @deprecate
    set date(val: number) { this.moment_write.date(val); }

    get dayOfMonth() { return this.date }
    set dayOfMonth(val: number) { this.date = val }

    get daysInMonth() { return this.moment_read.daysInMonth() }

    get hour() { return this.moment_read.hour() }
    set hour(val: number) { this.moment_write.hour(val) }

    // get hours() { return this.moment_read.hours() }
    // set hours(val: number) { this.moment_write.hours(val) }

    get minute() { return this.moment_read.minute() }
    set minute(val: number) { this.moment_write.minute(val) }

    // get minutes() { return this.moment_read.minutes() }
    // set minutes(val: number) { this.moment_write.minutes(val) }

    get second() { return this.moment_read.second() }
    set second(val: number) { this.moment_write.second(val) }

    // get seconds() { return this.moment_read.seconds() }
    // set seconds(val: number) { this.moment_write.seconds(val) }

    get millisecond() { return this.moment_read.millisecond() }
    set millisecond(val: number) { this.moment_write.millisecond(val) }

    // get milliseconds() { return this.moment_read.milliseconds() }
    // set milliseconds(val: number) { this.moment_write.milliseconds(val) }

    get nanosecond() { return this.moment_read.millisecond() * 1000000 }
    set nanosecond(val: number) { this.moment_write.millisecond(val / 1000000) }

    get weekday() { return this.moment_read.weekday() }
    set weekday(val: number) { this.moment_write.weekday(val) }

    get isoWeekday() { return this.moment_read.isoWeekday() }
    set isoWeekday(val: number) { this.moment_write.isoWeekday(val) }

    get isoWeekdayName() { return this.moment_read.isoWeekday() + '' }
    set isoWeekdayName(val: string) { this.moment_write.isoWeekday(val) }

    get weekYear() { return this.moment_read.weekYear() }
    set weekYear(val: number) { this.moment_write.weekYear(val) }

    get isoWeekYear() { return this.moment_read.isoWeekYear() }
    set isoWeekYear(val: number) { this.moment_write.isoWeekYear(val) }

    get week() { return this.moment_read.week() }
    set week(val: number) { this.moment_write.week(val) }

    get weeks() { return this.moment_read.weeks() }
    set weeks(val: number) { this.moment_write.weeks(val) }

    get isoWeek() { return this.moment_read.isoWeek() }
    set isoWeek(val: number) { this.moment_write.isoWeek(val) }

    get isoWeeks() { return this.moment_read.isoWeeks() }
    set isoWeeks(val: number) { this.moment_write.isoWeeks(val) }

    get weeksInYear() { return this.moment_read.weeksInYear() }

    get isoWeeksInYear() { return this.moment_read.isoWeeksInYear() }

    get isoWeeksInIsoWeekYear() { return this.moment_read.isoWeeksInISOWeekYear() }

    get dayOfYear() { return this.moment_read.dayOfYear() }
    set dayOfYear(val: number) { this.moment_write.dayOfYear(val) }

    get epochMilli() { return this.moment_read.valueOf() }
    set epochMilli(val: number) {
        this.moment = moment(val);
        this.version++;
    }

    get epochSecond() { return this.epochMilli / 1000 }
    set epochSecond(val: number) { this.epochMilli = val * 1000 }

    get epochDay() { return this.epochMilli / 1000 / 86400 }
    set epochDay(val: number) { this.epochMilli = val * 1000 * 86400 }

    get isLeapYear() { return this.moment_read.isLeapYear() }
    get isLeapMonth() { return this.moment_read.isLeapYear() && this.month1 == 2 }

    get tz() { return this.moment_read.tz() }
    set tz(val: ZoneRegion | undefined) { if (val != null) this.moment_write.tz(val) }

    get tzKeepLocal() { return this.moment_read.tz() }
    set tzKeepLocal(val: ZoneRegion | undefined) { if (val != null) this.moment_write.tz(val, true) }

    get utcOffset() { return this.moment_read.utcOffset() }
    set utcOffset(val: number | ZoneOffset) { this.moment_write.utcOffset(val); }

    get utcOffsetKeepLocal() { return this.moment_read.utcOffset() }
    set utcOffsetKeepLocal(val: number | ZoneOffset) { this.moment_write.utcOffset(val, true); }

    get isUtcOffset() { return this.moment_read.isUtcOffset() }
    get isDST() { return this.moment_read.isDST() }
    get zoneAbbr(): ZoneRegion { return this.moment_read.zoneAbbr() }
    get zoneName(): ZoneRegion { return this.moment_read.zoneName() }

}

export default MomentWrapper;
