import TypeInfo from "./TypeInfo";
import Predef from "./Predef";

export class PredefType<T extends Predef<K>, K> extends TypeInfo<T> {

    _class: any

    _loaded = false;
    _keyMap: any = {};
    _nameMap: any = {};
    _fieldMap: any = {};

    override get name() { return 'Predef' }

    constructor(_class: any) {
        super();
        this._class = _class;
    }

    get itemClass() { return this._class; }

    get keyMap() { return this.load()._keyMap; }
    get nameMap() { return this.load()._nameMap; }
    get fieldMap() { return this.load()._fieldMap; }

    load() {
        if (!this._loaded) {
            for (let k in this._class) {
                let val = this._class[k];
                if (val instanceof this._class) {
                    val._fieldName = k;
                    this._keyMap[val.key] = val;
                    this._nameMap[val.name] = val;
                    this._fieldMap[k] = val;
                }
            }
            this._loaded = true;
        }
        return this;
    }

    override parse(s: string): T {
        let val = this.nameMap[s];
        return val;
    }

    override format(val: T): string {
        return val.name;
    }

}

export default PredefType;
