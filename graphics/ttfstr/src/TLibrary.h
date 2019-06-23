#pragma once

#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_CACHE_H
#include FT_CACHE_MANAGER_H
#include FT_GLYPH_H
#include FT_STROKER_H
#include FT_BITMAP_H

#include "TFont.hh"
#include "TGlyph.hh"

#include <vector>
#include <opencv2/core/core.hpp>

enum {
    LCD_MODE_AA = 0,
    LCD_MODE_LIGHT,
    LCD_MODE_RGB,
    LCD_MODE_BGR,
    LCD_MODE_VRGB,
    LCD_MODE_VBGR,
    N_LCD_MODES,
};

FT_Int32 toLoadTarget(bool antialias, int lcdMode);
FT_Render_Mode toRenderMode(bool antialias, int lcdMode);

enum {
    KERNING_DEGREE_NONE = 0,
    KERNING_DEGREE_LIGHT,
    KERNING_DEGREE_MDEIUM,
    KERNING_DEGREE_TIGHT,
    N_KERNING_DEGREES,
};

enum {
    KERNING_MODE_NONE = 0,              /* no kerning */
    KERNING_MODE_NORMAL,                /* `kern' values */
    KERNING_MODE_SMART,                 /* `kern' + side bearing errors */
    N_KERNING_MODES,
};

class TLibrary {
public:
    TLibrary();
    ~TLibrary();

    bool init();
    bool installFont(const char *path, FT_Bool outlineOnly);

    void setFont(TFont *font);
    void setSize(int pixelSize);
    void setCharSize(int charSize, int resolution);

    void setPreload(bool preload);
    void updateCurrentFlags();

    FT_UInt getIndex(FT_UInt32 charcode);
    FT_Error getSize(FT_Size *asize);

    bool glyphToImage(FT_Glyph glyph,
                      cv::Mat *image,
                      int *x0, int *y0, int *dx, int *dy,
                      FT_Glyph *aglyph);

    /** free pixmap and image after use. */
    bool convertBitmap(FT_Bitmap *bitmap,
                       FT_Bitmap *pixmap,
                       cv::Mat *image);

    bool indexToImage(FT_ULong index,
                      cv::Mat *image,
                      int *x0, int *y0, int *dx, int *dy,
                      FT_Glyph *aglyph);

public:
    FT_Library library;
    FT_Error error;

    FTC_Manager cacheManager;
    FTC_ImageCache imageCache;
    FTC_SBitCache sbitsCache;           /* the glyph small bitmaps cache */
    FTC_CMapCache cmapCache;

    std::vector<TFont *> fonts;

    int useSbitsCache;                  /* toggle sbits cache */

    TFont *currentFont;                 /* selected font */
    FTC_ScalerRec scaler;
    FT_Int32 loadFlags;

    bool hinted;                        /* is glyph hinting active? */
    bool antialias;
    bool useSbits;
    bool autohint;                      /* force auto-hinting */
    int lcdMode;
    bool preload;                       /* force font file preloading */
    bool color;                         /* load color bitmaps */

    std::vector<TGlyph *> glyphs;
    bool reloadGlyphs;

    FT_Encoding encoding;
    FT_Stroker stroker;
    FT_Bitmap bitmap;
};
