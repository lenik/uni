#pragma once

#include <ft2build.h>

struct TextDrawParam {
    int kerningMode;
    int kerningDegree;
    FT_Fixed center;                    /* 0..1 */
    bool vertical;                      /* display vertically? */
    FT_Matrix *matrix;                  /* transformation */
};
