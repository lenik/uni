import { ValidateResult } from "@skeljs/core/src/ui/types"
import { Moment } from "moment-timezone";
import { Person } from "./Person"

export function validate_birthday(val: Moment) {
}

export function validate_father(val: Person) {
    if (val.gender != null)
        if (val.gender != 'm')
            throw "father should be male.";
}

export function validate_mother(val: Person) {
    if (val.gender != null)
        if (val.gender != 'f')
            throw "mother should be female.";
}

export function validate_gender(val: string) {
}

export function validate_homeland(val: string) {
    lengthGE3(val);
}

export function validate_passport(val: string) {
    lengthGE3(val);
}

export function validate_ssn(val: string) {
    lengthGE3(val);
}

export function validate_dln(val: string) {
    lengthGE3(val);
}

function lengthGE3(val: string) {
    if (val.length < 3)
        throw "length is too short, at least 3 chars."
}
