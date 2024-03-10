import MomentWrapper from "./MomentWapper";
import MomentWrapperType from "./MomentWapperType";

export class InstantType extends MomentWrapperType<Instant>{

    get name() { return "Instant"; }
    get icon() { return "far-clock"; }
    get description() { return "an instant"; }

    override create(): Instant {
        return Instant.now();
    }

}

export class Instant extends MomentWrapper {

    static readonly TYPE = new InstantType();

    static UNIX_MS_TIMESTAMP = 'x';
    static UNIX_TIMESTAMP = 'X';
    static DEFAULT_FORMAT = Instant.UNIX_MS_TIMESTAMP;

    constructor(format: string, inp?: moment.MomentInput) {
        super(format, inp);
    }

    static now() {
        return new Instant(Instant.DEFAULT_FORMAT);
    }

    static ofEpochMilli(epochMilli: number) {
        return new Instant(Instant.UNIX_MS_TIMESTAMP, epochMilli);
    }

    static ofEpochSecond(epochSecond: number) {
        return new Instant(Instant.UNIX_TIMESTAMP, epochSecond);
    }

}

export default Instant;
