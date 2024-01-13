
export type EventHandler = (event?: Event) => void | Promise<void>;
export type Href = string;

export interface Command {

    position?: number

    icon?: string
    label: string
    description?: string
    tooltip?: string

    className?: string

    href?: string
    action?: 'close' | 'maximize' | 'toggle'
    run?: EventHandler

    sync?: boolean

}

export interface ValidateResult {
    error: boolean
    errorCode?: number
    type?: string
    message?: string
    help?: string
    target?: any
}

export type Validator
    = (val: any) => ValidateResult;
