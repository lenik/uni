#include "TLibrary.hh"

#include FT_XFREE86_H
#define FT_Get_Font_Format FT_Get_X11_Font_Format

#include "fn.hh"

#include <vector>
#include <bas/log.h>

#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;

FT_Int32 toLoadTarget(bool antialias, int lcdMode) {
    FT_Int32 target = FT_LOAD_TARGET_NORMAL;
    if (antialias)
        switch (lcdMode) {
        case LCD_MODE_LIGHT:
            target = FT_LOAD_TARGET_LIGHT;
            break;
        case LCD_MODE_RGB:
        case LCD_MODE_BGR:
            target = FT_LOAD_TARGET_LCD;
            break;
        case LCD_MODE_VRGB:
        case LCD_MODE_VBGR:
            target = FT_LOAD_TARGET_LCD_V;
            break;
        }
    else
        target = FT_LOAD_TARGET_MONO;
    return target;
}

FT_Render_Mode toRenderMode(bool antialias, int lcdMode) {
    FT_Render_Mode mode = FT_RENDER_MODE_MONO;
    if (antialias) {
        switch (lcdMode) {
        case 0:
            mode = FT_RENDER_MODE_NORMAL; break;
        case 1:
            mode = FT_RENDER_MODE_LIGHT; break;
        case 2:
        case 3:
            mode = FT_RENDER_MODE_LCD; break;
        default:
            mode = FT_RENDER_MODE_LCD_V;
        }
    }
    return mode;
}

static FT_Error
defaultFaceRequester(FTC_FaceID faceId,
                     FT_Library lib,
                     FT_Pointer requestData,
                     FT_Face *aface) {
    FT_UNUSED(requestData);
    TFont *font = (TFont *) faceId;
    FT_Error error;

    if (font->buffer)
        error = FT_New_Memory_Face(lib,
                                   (const FT_Byte *) font->buffer,
                                   (FT_Long) font->fileSize,
                                   font->faceIndex,
                                   aface);
    else
        error = FT_New_Face(lib,
                            font->path,
                            font->faceIndex,
                            aface);

    if (error) {
        if (error == FT_Err_Unknown_File_Format)
            log_err("The font file could be opened and read, but it appears"
                    " that its font format is unsupported.");
        else
            log_err("The font file could not be opened or read, or it is broken.");
        return error;
    }

    const char *format = FT_Get_Font_Format(*aface);
    if (strcmp(format, "Type 1") == 0) {
        int len = strlen(font->path);
        char *alt = (char *) malloc(len + 5);
        strcpy(alt, font->path);

        char *suffix = strrchr(alt, '.');
        int hasExtension = suffix &&
            (strcasecmp(suffix, ".pfa") ==0 ||
             strcasecmp(suffix, ".pfb") == 0);
        if (! hasExtension)
            suffix = alt + len;

        strcpy(suffix, ".afm");
        if (FT_Attach_File(*aface, alt)) {
            strcpy(suffix, ".pfm");
            FT_Attach_File(*aface, alt);
        }

        free(alt);
    }

    if ((*aface)->charmaps)
        (*aface)->charmap = (*aface)->charmaps[font->cmapIndex];

    return error;
}

TLibrary::TLibrary() {
    init();
}

TLibrary::~TLibrary() {
    for (TFont *font: fonts) {
        if (font->path)
            free(font->path);
        free(font);
    }
    fonts.clear();

    for (TGlyph *glyph: glyphs) {
        if (glyph->image)
            FT_Done_Glyph(glyph->image);
    }
    glyphs.clear();

    FT_Stroker_Done(stroker);
    FT_Bitmap_Done(library, &bitmap);
    FTC_Manager_Done(cacheManager);
    FT_Done_FreeType(library);
}

bool TLibrary::init() {
    memset(this, 0, sizeof(TLibrary));

    error = FT_Init_FreeType(&library);
    if (error) {
        log_err("Failed to init freetype2.");
        return false;
    }

    error = FTC_Manager_New(library, 0, 0, 0,
                            defaultFaceRequester, 0,
                            &cacheManager);
    if (error) {
        log_err("Failed to init cache manager.");
        return false;
    }

    error = FTC_SBitCache_New(cacheManager, &sbitsCache);
    if (error) {
        log_err("Failed to init small bitmaps cache.");
        return false;
    }

    error = FTC_ImageCache_New(cacheManager, &imageCache);
    if (error) {
        log_err("Failed to init glyph image cache.");
        return false;
    }

    error = FTC_CMapCache_New(cacheManager, &cmapCache);
    if (error) {
        log_err("Failed to init charmap cache.");
        return false;
    }

    FT_Bitmap_New(&bitmap);
    FT_Stroker_New(library, &stroker);

    encoding = FT_ENCODING_NONE;

    hinted = true;
    antialias = true;
    useSbits = true;
    autohint = false;
    lcdMode = 0;                        /* LCD_MODE_AA */
    color = true;

    useSbitsCache = true;
}

bool TLibrary::installFont(const char *path,
                           FT_Bool outlineOnly) {
    const char *name = strrchr(path, '/');
    if (! name) name = path;

    FT_Face face;
    error = FT_New_Face(library, path, -1, &face);
    if (error) {
        log_err("Failed to count faces of %s.", name);
        return false;
    }

    long nface = face->num_faces;
    FT_Done_Face(face);

    for (int i = 0; i < nface; i++) {
        TFont *font;
        error = FT_New_Face(library, path, -(i + 1), &face);
        if (error) {
            log_warn("Couldn't count named instances of %s[%d].", name, i);
            continue;
        }
        long count = face->style_flags >> 16; /* named instance count */
        FT_Done_Face(face);

        /* j: relavant to GX variation fonts, specifying the named instance
           index for the current face index. */
        for (int j = 0; j < count + 1; j++) {
            error = FT_New_Face(library, path,
                                (j << 16) + i,
                                &face);
            if (error) {
                log_warn("Couldn't open face %s[%d:%d].", name, i, j);
                continue;
            }

            if (outlineOnly && ! FT_IS_SCALABLE(face)) {
                FT_Done_Face(face);
                continue;
            }

            if (encoding != FT_ENCODING_NONE) {
                error = FT_Select_Charmap(face, encoding);
                if (error) {
                    log_warn("Couldn't select charmap %d for font %s.",
                             encoding, name);
                    FT_Done_Face(face);
                    continue;
                }
            }

            font = new TFont(face, encoding);
            font->path = strdup(path);
            font->faceIndex = (j << 16) + i;

            if (preload) {
                pair<void *, size_t> pair = fload(path);
                if (! pair.first) {
                    log_perr("Failed to load file %s: ", path);
                    delete font;
                    return false;
                }
                font->buffer = pair.first;
                font->fileSize = pair.second;
            } else {
                font->buffer = NULL;
                font->fileSize = 0;
            }

            FT_Done_Face(face);
            face = NULL;

            fonts.push_back(font);
        }
    }
    return FT_Err_Ok;
}

void TLibrary::setFont(TFont *font) {
    currentFont = font;
    scaler.face_id = (FTC_FaceID) font;
    reloadGlyphs = true;
}

void TLibrary::setSize(int pixelSize) {
    if (pixelSize > 0xFFFF)
        pixelSize = 0xFFFF;

    scaler.width = (FT_UInt) pixelSize;
    scaler.height = (FT_UInt) pixelSize;
    scaler.pixel = 1;                   /* activate integer format */
    scaler.x_res = 0;
    scaler.y_res = 0;
    reloadGlyphs = true;
}

void TLibrary::setCharSize(int charSize, int resolution) {
    /* in 26.6 format, corresponding to (almost) 0x4000ppem */
    if (charSize > 0xFFFFF)
        charSize = 0xFFFFF;

    scaler.width = (FT_UInt) charSize;
    scaler.height = (FT_UInt) charSize;
    scaler.pixel = 0;
    scaler.x_res = (FT_UInt) resolution;
    scaler.y_res = (FT_UInt) resolution;
    reloadGlyphs = true;
}

void TLibrary::setPreload(bool preload) {
    this->preload = preload;
}

void TLibrary::updateCurrentFlags() {
    FT_Int32 flags, target;
    flags = FT_LOAD_DEFAULT;            /* 0 */
    if (autohint)
        flags |= FT_LOAD_FORCE_AUTOHINT;
    if (! useSbits)
        flags |= FT_LOAD_NO_BITMAP;
    if (hinted) {
        target = toLoadTarget(antialias, lcdMode);
        flags |= target;
    } else {
        flags |= FT_LOAD_NO_HINTING;
        if (! antialias)
            flags |= FT_LOAD_MONOCHROME;
    }
    if (color)
        flags |= FT_LOAD_COLOR;

    loadFlags = flags;
    reloadGlyphs = true;
}

FT_UInt TLibrary::getIndex(FT_UInt32 charcode) {
    return FTC_CMapCache_Lookup(cmapCache,
                                scaler.face_id,
                                currentFont->cmapIndex,
                                charcode);
}

FT_Error TLibrary::getSize(FT_Size *asize) {
    FT_Size size;
    error = FTC_Manager_LookupSize(cacheManager,
                                   &scaler,
                                   &size);
    if (! error)
        *asize = size;
    return error;
}

bool TLibrary::glyphToImage(FT_Glyph glyph,
                            cv::Mat *image,
                            int *x0, int *y0, int *dx, int *dy,
                            FT_Glyph *aglyph) {
    FT_BitmapGlyph bglyph;
    FT_Bitmap *bitmap;
    *aglyph = NULL;
    error = FT_Err_Ok;

    if (glyph->format == FT_GLYPH_FORMAT_OUTLINE) {
        FT_Render_Mode mode = toRenderMode(antialias, lcdMode);
        /* render the glyph to a bitmap, don't destroy orig. */
        error = FT_Glyph_To_Bitmap(&glyph, mode, NULL, 0);
        if (error) {
            log_err("Failed to convert glyph to bitmap.");
            return false;
        }
        *aglyph = glyph;
    }

    if (glyph->format != FT_GLYPH_FORMAT_BITMAP) {
        log_err("Invalid glyph format returned!");
        return false;
    }

    bglyph = (FT_BitmapGlyph) glyph;
    bitmap = &bglyph->bitmap;

    FT_Bitmap pixmap;
    if (! convertBitmap(bitmap, &pixmap, image)) {
        log_err("Failed to convert bitmap to pixmap.");
        return false;
    }

    *x0 = bitmap->left;
    *y0 = bitmap->top;
    *dx = (glyph->advance.x + 0x8000) >> 16;
    *dy = (glyph->advance.y + 0x8000) >> 16;
}

bool TLibrary::convertBitmap(FT_Bitmap *bitmap,
                             FT_Bitmap *pixmap,
                             cv::Mat *image) {
    memset(pixmap, 0, sizeof(FT_Bitmap));
    error = FT_Bitmap_Convert(library, bitmap, pixmap, 4);
    if (error) {
        log_err("Failed to convert bitmap to pixmap.");
        return false;
    }

    if (pixmap->pixel_mode != FT_PIXEL_MODE_GRAY) {
        log_err("Unexpected converted pixel mode %d.", pixmap->pixel_mode);
        FT_Bitmap_Done(pixmap);
        return false;
    }

    *image = cv::Mat(pixmap->rows,
                     pixmap->cols,
                     CV_8UC1,
                     pixmap->buffer,
                     pixmap->pitch);

    return true;
}

bool TLibrary::indexToImage(FT_ULong index,
                            cv::Mat *image,
                            int *x0, int *y0, int *dx, int *dy,
                            FT_Glyph *aglyph) {
    unsigned width, height;

    *aglyph = NULL;
    *dx = 0;

    // use the SBits cache to store small glyph bitmaps.
    // it's a lot more memory-efficient.
    width = scaler.width;
    height = scaler.height;
    if (userSbitsCache) {
        if (! scaler.pixel) {
            int xdpi = 72;
            int ydpi = 72;
            width = ((width * scaler.x_res + 36) / xdpi) >> 6;
            height = ((height * scaler.y_res + 36) / ydpi) >> 6;
        }
        if (width < 48 && height < 48) {
            FTC_SBit sbit;
            FT_Bitmap source;
            error = FTC_SBitCache_LookupScaler
                (sbitsCache,
                 &scaler,
                 (FT_ULong) loadFlags,
                 index,
                 &sbit,
                 NULL);
            if (error) {
                log_err("Failed to lookup scaler in sbit-cache.");
                goto _exit;
            }
            if (sbit->buffer) {
                source.rows = sbit->height;
                source.width = sbit->height;
                source.pitch = sbit->pitch;
                source.buffer = sbit->buffer;
                source.pixel_mode = sbit->format;
                FT_Bitmap_Convert(library, &source, &bitmap, 1);

                int grays = sbit->max_grays + 1;
                image = cv::Mat(sbit->height, sbit->width,
                                CV_8UC1, sbit->buffer, sbit->pitch);
                // norm...?
            }
            goto _exit;
        }
    } // if useSbitsCache

    /* otherwise, use an image cache to store glyph outlines. */
    /* and render them on demand. to support very large sizes. */
    {
        FT_Glyph glyph;
        error = FTC_ImageCache_LookupScaler
            (imageCache,
             &scaler,
             (FT_ULong)loadFlags,
             index,
             &glyph,
             NULL);
        if (error) {
            log_err("Failed to lookup scaler in image-cache.");
            goto _exit;
        }
        error = glyphToImage(glyph, image, x0, y0, dx, dy, aglyph);
    }
 _exit:
    if (index == 0 && *dx <= 0)
        *dx = 1;
    return error;
}
