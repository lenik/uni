import GeoZoneTypeInfo from './GeoZoneTypeInfo';

export class GeoZone {

    static readonly TYPE = new GeoZoneTypeInfo();

    readonly id: string;

    localCode: string;
    localeName: string;

    enName: string;
    zhName: string;

    parent: GeoZone | undefined;
    readonly children: any = {}; // { id => child-node }

    constructor(id: string) {
        this.id = id;
    }

    static registry: any = {};
    static load(id: string) {
        if (id == null || id == '')
            return undefined;
        let node = this.registry[id];
        if (node == null) {
            node = new GeoZone(id);
            this.registry[id] = node;
        }
        return node;
    }

    bottomUp(): GeoZone[] {
        let list: GeoZone[] = [];
        let node: GeoZone | undefined = this;
        while (node != null) {
            list.push(node);
            node = node.parent;
        }
        return list;
    }

    topDown(): GeoZone[] {
        let list = this.bottomUp();
        list.reverse();
        return list;
    }

    get fullCode() {
        return this.buildCode();
    }

    buildCode() {
        let sb = '';
        for (let r of this.topDown())
            sb += r.localCode;
        return sb;
    }


    toString() {
        return this.join(", ");
    }

    join(delimit: string) {
        let sb = '';
        let r: GeoZone | undefined = this;
        while (true) {
            sb += (r.enName);
            r = r.parent;
            if (r != null)
                sb += (delimit);
            else
                break;
        }
        return sb;
    }

    toZhString(delimit = ' ') {
        let array = [];
        this.dumpZhNames(array);
        return array.join(delimit);
    }

    private dumpZhNames(array: string[]) {
        if (this.parent != null) {
            this.parent.dumpZhNames(array);
        }
        array.push(this.zhName);
    }

}

export default GeoZone;
