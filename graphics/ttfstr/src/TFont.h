#pragma once

#include <ft2build.h>
#include FT_FREETYPE_H

class TFont {

public:
    TFont(FT_Face face, int encoding);

public:
    char *path;

    int faceIndex;
    int cmapIndex;

    int nIndex;
    void *buffer;                       /* preloaded file */
    size_t fileSize;
};
