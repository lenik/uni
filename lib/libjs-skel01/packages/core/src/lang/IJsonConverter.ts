
export interface IJsonConverter<T> {

    fromJson(jv: any): T

    toJson(val: T): any

}

export default IJsonConverter;
