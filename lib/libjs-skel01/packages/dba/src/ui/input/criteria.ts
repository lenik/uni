
type Criterion = Junction | Disjunction | Not | FieldCriterion;

type Value = number | string | boolean | null;

type Junction =
    { 'and': Criterion[] }      // normalized form
    | Criterion[]               // default  form
    | { [fieldName: string]: Value | FieldCriterion }
    ;

type Disjunction = { 'or': Criterion[] };

type Not = { 'not': Criterion | Criterion[] };

type FieldCriterion =
    FieldCompare
    | FieldLike
    | FieldNotLike
    | FieldBetween
    | FieldNotBetween
    | FieldIn
    | FieldNotIn
    ;

type FieldCompare =
    FieldEquals
    | FieldNotEquals
    | FieldLessThan
    | FieldLessOrEquals
    | FieldGreaterThan
    | FieldGreaterOrEquals
    ;

type FieldEquals = { 'eq': any } | { '~notEq': any };
type FieldNotEquals = { 'notEq': any } | { '~eq': any };
type FieldLessThan = { 'lessThan': any } | { '~greaterOrEquals': any };
type FieldLessOrEquals = { 'lessOrEquals': any } | { '~greaterThan': any };
type FieldGreaterThan = { 'greaterThan': any } | { '~lessOrEquals': any };
type FieldGreaterOrEquals = { 'greaterOrEquals': any } | { '~lessThan': any };

type FieldLike = { 'like': string };
type FieldNotLike = { '~like': string };

type FieldBetween = { 'between': [any, any] }
type FieldNotBetween = { '~between': [any, any] }

type FieldIn = { 'in': any[] };
type FieldNotIn = { '~in': any[] };

let sample: Criterion = {
    'or': [
        {
            'age-eq': 123,
            'age-less': { 'lessThan': 16 },
        },
        [],
    ]
};
