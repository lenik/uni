export interface IJsonForm {

    get wantObjectContext(): boolean

    /**
     * @return the context if wantObjectContext.     
     *         otherwise, context is null and return the converted JSON object/value.
    */
    jsonOut(context: any): any;

    jsonIn(jv: any): void;

}

export default IJsonForm;
