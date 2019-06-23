#include "TFont.h"

TFont::TFont(FT_Face face, int encoding) {
    cmapIndex = 0;
    if (face->charmap)
        cmapIndex = FT_Get_Charmap_Index(face->charmap);

    nIndex = face->num_glyphs;

    int nIndex;
    switch (encoding) {
    case FT_ENCODING_NONE:
        break;
    case FT_ENCODING_UNICODE:
        nIndex = 0x110000L;
        break;

        /* some fonts use range 00-100, others have F000-F0FF. */
    case FT_ENCODING_ADOBE_LATIN_1:
    case FT_ENCODING_ADOBE_STANDARD:
    case FT_ENCODING_ADOBE_CUSTOM:
    case FT_ENCODING_APPLE_ROMAN:
        nIndex = 0x100L;
        break;

    case FT_ENCODING_MS_SYMBOL:
    default:
        nIndex = 0x10000L;
    }
}
