import TypeInfo from "../TypeInfo";
import MomentWrapper from "./MomentWapper";

export abstract class MomentWrapperType<T extends MomentWrapper> extends TypeInfo<T> {

    abstract create(): T

    override parse(s: string): T {
        let instance = this.create();
        instance.parse(s);
        return instance;
    }

    override format(val: T): string {
        return val.toString();
    }

    override fromJson(jv: any): T {
        let str = jv as string;
        return this.parse(str);
    }

    override toJson(val: T) {
        let jv = this.format(val);
        return jv;
    }

}

export default MomentWrapperType;