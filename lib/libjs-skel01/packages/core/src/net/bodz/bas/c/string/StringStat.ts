import type { int } from "../../../../../lang/basetype";
import Character from "../../../../../lang/Character";

export class StringStat {

    static count(s: string, literalPattern: string): int {
        let count = 0;
        let index = 0;
        while ((index = s.indexOf(literalPattern, index)) != -1) {
            index += literalPattern.length;
            count++;
        }
        return count;
    }

    // static countByCategory(s: string, charCategory: int): int {
    //     let count = 0;
    //     let index = 0;
    //     while ((index = StringSearch.indexOf(s, charCategory, index)) != -1) {
    //         index++;
    //         count++;
    //     }
    //     return count;
    // }

    static wordCount(data: string): int {
        let count = 0;
        let pos = 0;
        let len = data.length;
        let started = false;
        while (pos < len) {
            let ch = data.charAt(pos++);
            let space = Character.isWhitespace(ch);
            if (space && started)
                count++;
            started = !space;
        }
        if (started)
            count++;
        return count;
    }

}

export default StringStat;
