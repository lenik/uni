package net.bodz.bas.std.rfc.mime;

import java.util.HashMap;
import java.util.Map;

public class ContentTypes {

    static Map<String, ContentType> nameMap = new HashMap<String, ContentType>();
    static Map<String, ContentType> extensionMap = new HashMap<String, ContentType>();

    public static ContentType register(String name, String preferredExtension, String... altExtensions) {
        if (name == null)
            throw new NullPointerException("name");
        if (preferredExtension == null)
            throw new NullPointerException("preferredExtension");

        ContentType m = new ContentType(name, preferredExtension);

        nameMap.put(name, m);

        extensionMap.put(preferredExtension, m);
        if (altExtensions != null)
            for (String extension : altExtensions)
                extensionMap.put(extension, m);

        return m;
    }

    /** Atom feeds */
    public static final ContentType application_atom_xml = register("application/atom+xml", "rss");

    /** EDI X12 data; Defined in RFC 1767 */
    public static final ContentType application_EDI_X12 = register("application/EDI-X12", "EDI-X12");

    /** EDI EDIFACT data; Defined in RFC 1767 */
    public static final ContentType application_EDIFACT = register("application/EDIFACT", "EDIFACT");

    /** JavaScript Object Notation JSON; Defined in RFC 4627 */
    public static final ContentType application_json = register("application/json", "json");

    /** JavaScript; Defined in RFC 4329 but not accepted in IE 8 or earlier */
    public static final ContentType application_javascript = register("application/javascript", "js");

    /**
     * Arbitrary binary data.[4] Generally speaking this type identifies files that are not
     * associated with a specific application. Contrary to past assumptions by software packages
     * such as Apache this is not a type that should be applied to unknown files. In such a case, a
     * server or application should not indicate a content type, as it may be incorrect, but rather,
     * should omit the type in order to allow the recipient to guess the type.[5]
     */
    public static final ContentType application_octet_stream = register("application/octet-stream", "bin");

    /** Ogg, a multimedia bitstream container format; Defined in RFC 5334 */
    public static final ContentType application_ogg = register("application/ogg", "ogg");

    /**
     * Portable Document Format, PDF has been in use for document exchange on the Internet since
     * 1993; Defined in RFC 3778
     */
    public static final ContentType application_pdf = register("application/pdf", "pdf");

    /** PostScript; Defined in RFC 2046 */
    public static final ContentType application_postscript = register("application/postscript", "ps");

    /** SOAP; Defined by RFC 3902 */
    public static final ContentType application_soap_xml = register("application/soap+xml", "soap");

    /** XHTML; Defined by RFC 3236 */
    public static final ContentType application_xhtml_xml = register("application/xhtml+xml", "xhtml");

    /** DTD files; Defined by RFC 3023 */
    public static final ContentType application_xml_dtd = register("application/xml-dtd", "dtd");

    /** ZIP archive files; Registered[6] */
    public static final ContentType application_zip = register("application/zip", "zip");

    /** Gzip */
    public static final ContentType application_x_gzip = register("application/x-gzip", "gz");

    /** MP4 audio */
    public static final ContentType audio_mp4 = register("audio/mp4", "mp4");

    /** MP3 or other MPEG audio; Defined in RFC 3003 */
    public static final ContentType audio_mpeg = register("audio/mpeg", "mp3");

    /** Ogg Vorbis, Speex, Flac and other audio; Defined in RFC 5334 */
    public static final ContentType audio_ogg = register("audio/ogg", "ogg");

    /** Vorbis encoded audio; Defined in RFC 5215 */
    public static final ContentType audio_vorbis = register("audio/vorbis", "vorbis");

    /** Windows Media Audio; Documented in Microsoft KB 288102 */
    public static final ContentType audio_x_ms_wma = register("audio/x-ms-wma", "wma");

    /** Windows Media Audio Redirector; Documented in Microsoft help page */
    public static final ContentType audio_x_ms_wax = register("audio/x-ms-wax", "wax");

    /** RealAudio; Documented in RealPlayer Customer Support Answer 2559 */
    public static final ContentType audio_vnd_rn_realaudio = register("audio/vnd.rn-realaudio", "ra");

    /** WAV audio; Defined in RFC 2361 */
    public static final ContentType audio_vnd_wave = register("audio/vnd.wave", "wav");

    /** GIF image; Defined in RFC 2045 and RFC 2046 */
    public static final ContentType image_gif = register("image/gif", "gif");

    /** JPEG JFIF image; Defined in RFC 2045 and RFC 2046 */
    public static final ContentType image_jpeg = register("image/jpeg", "jpg", "jpeg");

    /** Portable Network Graphics; Registered,[7] Defined in RFC 2083 */
    public static final ContentType image_png = register("image/png", "png");

    /** SVG vector image; Defined in SVG Tiny 1.2 Specification Appendix M */
    public static final ContentType image_svg_xml = register("image/svg+xml", "svg");

    /** Tag Image File Format; Defined in RFC 3302 */
    public static final ContentType image_tiff = register("image/tiff", "tif", "tiff");

    /** ICO image; Registered[8] */
    public static final ContentType image_vnd_microsoft_icon = register("image/vnd.microsoft.icon", "ico");

    /**  */
    public static final ContentType message_http = register("message/http", "http");

    /** MIME E-mail; Defined in RFC 2045 and RFC 2046 */
    public static final ContentType multipart_mixed = register("multipart/mixed", "mixed");

    /** MIME E-mail; Defined in RFC 2045 and RFC 2046 */
    public static final ContentType multipart_alternative = register("multipart/alternative", "alternative");

    /** MIME E-mail; Defined in RFC 2387 and used by MHTML (HTML mail) */
    public static final ContentType multipart_related = register("multipart/related", "related");

    /** MIME Webform; Defined in RFC 2388 */
    public static final ContentType multipart_form_data = register("multipart/form-data", "form-data");

    /** Defined in RFC 1847 */
    public static final ContentType multipart_signed = register("multipart/signed", "signed");

    /** Defined in RFC 1847 */
    public static final ContentType multipart_encrypted = register("multipart/encrypted", "encrypted");

    /** commands; subtype resident in Gecko browsers like FireFox 3.5 */
    public static final ContentType text_cmd = register("text/cmd", "cmd");

    /** Cascading Style Sheets; Defined in RFC 2318 */
    public static final ContentType text_css = register("text/css", "css");

    /** Comma-separated values; Defined in RFC 4180 */
    public static final ContentType text_csv = register("text/csv", "csv");

    /** HTML; Defined in RFC 2854 */
    public static final ContentType text_html = register("text/html", "html", "htm");
    public static final ContentType text_xhtml = register("text/xhtml", "xhtml", "xhtm");

    /** Textual data; Defined in RFC 2046 and RFC 3676 */
    public static final ContentType text_plain = register("text/plain", "txt", "text");

    /** Extensible Markup Language; Defined in RFC 3023 */
    public static final ContentType text_xml = register("text/xml", "xml");

    /** MPEG-1 video with multiplexed audio; Defined in RFC 2045 and RFC 2046 */
    public static final ContentType video_mpeg = register("video/mpeg", "mpeg", "mpg");

    /** MP4 video; Defined in RFC 4337 */
    public static final ContentType video_mp4 = register("video/mp4", "mp4");

    /** Ogg Theora or other video (with audio); Defined in RFC 5334 */
    public static final ContentType video_ogg = register("video/ogg", "ogg");

    /** QuickTime video; Registered[9] */
    public static final ContentType video_quicktime = register("video/quicktime", "quicktime");

    /** WebM open media format */
    public static final ContentType video_webm = register("video/webm", "webm");

    /** Windows Media Video; Documented in Microsoft KB 288102 */
    public static final ContentType video_x_ms_wmv = register("video/x-ms-wmv", "wmv");

    /** OpenDocument Text; Registered [10] */
    public static final ContentType application_vnd_oasis_opendocument_text = register(
            "application/vnd.oasis.opendocument.text", "vnd.oasis.opendocument.text");

    /** OpenDocument Spreadsheet; Registered [11] */
    public static final ContentType application_vnd_oasis_opendocument_spreadsheet = register(
            "application/vnd.oasis.opendocument.spreadsheet", "vnd.oasis.opendocument.spreadsheet");

    /** OpenDocument Presentation; Registered [12] */
    public static final ContentType application_vnd_oasis_opendocument_presentation = register(
            "application/vnd.oasis.opendocument.presentation", "vnd.oasis.opendocument.presentation");

    /** OpenDocument Graphics; Registered [13] */
    public static final ContentType application_vnd_oasis_opendocument_graphics = register(
            "application/vnd.oasis.opendocument.graphics", "vnd.oasis.opendocument.graphics");

    /** Microsoft Excel files */
    public static final ContentType application_vnd_ms_excel = register("application/vnd.ms-excel", "xls");

    /** Microsoft Powerpoint files */
    public static final ContentType application_vnd_ms_powerpoint = register("application/vnd.ms-powerpoint", "ppt");

    /** Microsoft Word files */
    public static final ContentType application_msword = register("application/msword", "doc");

    /** Mozilla XUL files */
    public static final ContentType application_vnd_mozilla_xul_xml = register("application/vnd.mozilla.xul+xml", "xul");

    /** KML files (e.g. for Google Earth) */
    public static final ContentType application_vnd_google_earth_kml_xml = register(
            "application/vnd.google-earth.kml+xml", "kml");

    /** Form Encoded Data; Documented in HTML 4.01 Specification, Section 17.13.4.1 */
    public static final ContentType application_x_www_form_urlencoded = register("application/x-www-form-urlencoded",
            "x-www-form-urlencoded ");

    /** device-independent document in DVI format */
    public static final ContentType application_x_dvi = register("application/x-dvi", "x-dvi");

    /** LaTeX files */
    public static final ContentType application_x_latex = register("application/x-latex", "x-latex");

    /** TrueType Font No registered MIME type, but this is the most commonly used */
    public static final ContentType application_x_font_ttf = register("application/x-font-ttf", "x-font-ttf");

    /**
     * Adobe Flash files for example with the extension .swf; Documented in Adobe TechNote tn_4151
     * and Adobe TechNote tn_16509
     */
    public static final ContentType application_x_shockwave_flash = register("application/x-shockwave-flash", "swf");

    /** StuffIt archive files */
    public static final ContentType application_x_stuffit = register("application/x-stuffit", "x-stuffit");

    /** RAR archive files */
    public static final ContentType application_x_rar_compressed = register("application/x-rar-compressed", "rar");

    /** Tarball files */
    public static final ContentType application_x_tar = register("application/x-tar", "tar");

    /** jQuery template data */
    public static final ContentType text_x_jquery_tmpl = register("text/x-jquery-tmpl", "x-jquery-tmpl");

    /** p12 files */
    public static final ContentType application_x_pkcs12 = register("application/x-pkcs12", "p12", "pfx");

    /** p7b files */
    public static final ContentType application_x_pkcs7_certificates = register("application/x-pkcs7-certificates",
            "p7b", "spc");

    /** p7r files */
    public static final ContentType application_x_pkcs7_certreqresp = register("application/x-pkcs7-certreqresp", "p7r");

    /** p7c files */
    public static final ContentType application_x_pkcs7_mime = register("application/x-pkcs7-mime", "p7c", "p7m");

    /** p7s files */
    public static final ContentType application_x_pkcs7_signature = register("application/x-pkcs7-signature", "p7s");

}