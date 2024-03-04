import TypeInfo from './TypeInfo';

export abstract class Predef<K> {

    _key: K
    _name: string
    _label?: string
    _icon?: string
    _description?: string

    _fieldName: string

    constructor(key: K, name: string, label?: string, icon?: string, description?: string) {
        this._key = key;
        this._name = name;
        this._label = label;
        this._icon = icon;
        this._description = description;
    }

    get key() { return this._key; }
    get name() { return this._name; }
    get label() { return this._label; }
    get display() { return this._name || this._label; }
    get icon() { return this._icon; }
    get description() { return this._description; }

    get fieldName() { return this._fieldName; }

    toString() {
        return '[' + this.name + ']';
    }

}

export default Predef;
