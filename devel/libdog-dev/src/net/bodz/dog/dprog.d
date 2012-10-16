module net.bodz.dog.dprog;

import std.string;
import std.getopt;

struct RcsId {
    string file;                        /* The VCS managed file */
    string ver;                         /* The last commit version */
    string date;                        /* The last commit date */
    string time;                        /* The last commit time */
    string mode;                        /* Experimental mode */

    this(string id) {
        assert(id.startsWith("$Id: ") && id.endsWith(" $"));
        id = id[4..$-1];

        int begin, end = 0;
        int index = 0;
    L:  while (end < id.length - 1) {
            /* move begin -> the next non-space */
            begin = end;
            while (id[begin] == ' ') {
                begin++;
                if (begin == id.length - 1)
                    break L;
            }

            /* move end -> the next space or eol */
            end = begin + 1;
            while (end < id.length && id[end] != ' ')
                end++;

            string part = id[begin..end];
            final switch (index++) {
            case 0:
                file = part;
                break;
            case 1:
                ver = part;
                break;
            case 2:
                date = part;
                break;
            case 3:
                time = part;
                break;
            case 4:
                mode = part;
                break;
            }
        } /* while end < eol... */
    }
}

string progname;
