#pragma once

#include <ft2build.h>
#include FT_FREETYPE_H

struct TGlyph {
    FT_UInt index;                      /* glyph_index */
    FT_Glyph image;                     /* the glyph image */

    FT_Pos delta;                       /* delta caused by hinting */
    FT_Vector vvector;                  /* vert origin => hori. origin */
    FT_Vector vadvance;                 /* vertical advance */
};
