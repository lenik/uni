import TypeInfo from '../../../../../lang/TypeInfo';
import GeoZone from "./GeoZone";

export class GeoZoneTypeInfo extends TypeInfo<GeoZone> {

    override get name() { return 'GeoZone' }
    override get icon() { return 'far-globe' }

    override parse(s: string): GeoZone {
        let [parentId, id, localCode, localeName, enName, zhName] = s.split(',');
        let r = GeoZone.load(id);
        r.localCode = localCode;
        r.localeName = localeName;
        r.enName = enName;
        r.zhName = zhName;
        r.parent = GeoZone.load(parentId);
        return r;
    }

    override format(val: GeoZone): string {
        let { parent, id, localCode, localeName, enName, zhName, } = val;
        let parentId = parent?.id;
        return `${parentId},${id},${localCode},${localeName},${enName},${zhName}`;
    }

    override fromJson(jv: any): GeoZone {
        let node = GeoZone.load(jv.id);
        node.localCode = jv.localCode;
        node.localeName = jv.localeName;
        node.enName = jv.enName;
        node.zhName = jv.zhName;
        node.parent = GeoZone.load(jv.parentId);
        return node;
    }

    override toJson(val: GeoZone) {
        return {
            id: val.id,
            localCode: val.localCode,
            localeName: val.localeName,
            enName: val.enName,
            zhName: val.zhName,
            parentId: val.parent?.id,
        };
    }

}

export default GeoZoneTypeInfo;
