<?

	###########################################################################
	#
	# Common Functions
	#
    function snm_time($time)        { return date("Y-m-d H:i:s", $time); }
    function snm_gmtime($time)      { return gmdate("Y-m-d H:i:s", $time); }
    function snm_gmstrtotime($str)  {
        $timeofday = gettimeofday();
        $gmtadj = -60 * $timeofday['minuteswest'];
        return strtotime($str) + $gmtadj;
    }
	function snm_newcolor(&$im, $rgb) {
		return imagecolorallocate($im, $rgb >> 16, ($rgb >> 8) & 0xFF, $rgb & 0xFF);
	}
	function snm_freecolor(&$im, &$clr) {
	    imagecolordeallocate($im, $clr);
	}
	function snm_colorindex(&$im, $rgb) {
	    return imagecolorexact($im, $rgb >> 16, ($rgb >> 8) & 0xFF, $rgb & 0xFF);
	}


	###########################################################################
	#
	# Subroutines
	#
	function CharName($c) {
	    if (ereg('[0-9a-z]', $c)) return $c;
	    if (ereg('[A-Z]', $c)) return strtolower("u$c");
	    return dechex(ord($c));
	}


	$font_charinfo = null;
	$font_charpat = null;
	function FontBuildCharInfo() {
	    global $font_charinfo, $font_charpat;
	    global $opt_textfont;

	    # build char-info
	        $font_charinfo = array( array(0x20, -999) );
	    # only use printable chars.
	    for ($asc = 0x21; $asc <= 0x7E; $asc++) {
                $charbox = imagettfbbox(12, 0, $opt_textfont, chr($asc));
                $c_left = $charbox[0];
                $c_bottom = -$charbox[1];
                $c_right = $charbox[2];
                $c_top = -$charbox[5];
                $c_width = abs($c_right - $c_left) + 1;
                $c_height = abs($c_top - $c_bottom) + 1;

                $im = imagecreate($c_width, $c_height);
                $bgcolor = snm_newcolor($im, 0xFFFFFF);
                $color = snm_newcolor($im, 0);
                $colorindex = snm_colorindex($im, 0);

                imagefilledrectangle($im, 0, 0, $c_width, $c_height, $bgcolor);
                imagettftext($im, 12, 0,
                             -$c_left,
                             $c_height + $c_bottom,
                             -$colorindex, $opt_textfont, chr($asc));
                             # imagepng($im, "temp_$asc.png");
                $dots = 0;
                for ($y = 0; $y < $c_height; $y++)
                    for ($x = 0; $x < $c_width; $x++)
                        if (imagecolorat($im, $x, $y) != 0) $dots++;
                imagecolordeallocate($im, $color);
                imagecolordeallocate($im, $bgcolor);
                imagedestroy($im);
                array_push($font_charinfo, array($asc, $dots));
	    }

	    # sort to $font_palette, with dots(0) at first, dots(max) at last.
	        function charinfo_cmp($a, $b) {
	            if ($a[1] == $b[1]) return 0;
	            return $a[1] < $b[1] ? -1 : 1;
	        }
	        usort($font_charinfo, "charinfo_cmp");
	        $font_charpat = array();
	        for ($i = 0; $i < 0x7E-0x20+1; $i++) {
	            array_push($font_charpat, $font_charinfo[$i][0]);
	        }
	}


	function ImageText(&$im, $filename) {
	    global $font_charinfo, $font_charpat;

	    if ($font_charinfo == null)
            FontBuildCharInfo();

        $w = imagesx($im);
        $h = imagesy($im);
        $f = fopen($filename, "w");
        if (! $f) die("Can't open file $filename");

        $patsize = 0x7E-0x20+1;
        for ($y = 0; $y < $h; $y++) {
            $line = '';
            for ($x = 0; $x < $w; $x++) {
                $clr = imagecolorat($im, $x, $y);
                $rgb = imagecolorsforindex($im, $clr);
                $val = ($patsize-1) - floor(
                            ( 0.3333*$rgb['red'] + 0.3333*$rgb['green'] + 0.3333*$rgb['blue'] )
                            * $patsize / 0x100   # 256 -> 96 scale
                            );
                if ($val < 0) $val = 0;
                if ($val >= $patsize) $val = $patsize - 1;

                # so val=0 if blank, or max if full.
                $line .= chr($font_charpat[$val]);
            }
            $line = rtrim($line);
            fwrite($f, "$line\n");
        }
        fclose($f);
	}


    function ParseId($id = '') {
        preg_match(
                '/^ \$ [I][d][:] \s (.*?) \s ([0-9.]+) \s ([0-9\/\\\-]+) \s
                 ([0-9:]+) \s (.*?) \s (\w+) \s \$ $/x',
                $id, $segs);
        $id = array(
                'rcs' => $segs[1],
                'rev' => $segs[2],
                'date' => $segs[3],
                'time' => $segs[4],
                'author' => $segs[5],
                'state' => $segs[6],
                );
        $id['time'] = snm_time(snm_gmstrtotime("$id[date] $id[time]"));
        return $id;
    }


	function Help() {
	    $id = ParseId('$Id: fontmap.php,v 1.8 2004-12-03 13:51:25 dansei Exp $');
	    ?>
[FONTMAP] Font Map Generator
Written by Snima Denik,  Version <?=$id['rev']?>,  Last updated <?=$id['time']?>

Syntax:
    fontmap
        --alias (no anti-alias)
        --angle=<rotate-angle(degree), default 0>
        --bgcolor=<background-color, default transparent>
        --bold=<delta-width>
        --chars=<characters-to-map, default [\x21-\xFF]>
        --color=<font-color, default 0 (black)>
        --font=<font-name or ttf-file>
        --format=<image-format, default png>
            (Format supported: png jpg gif wbmp gd gd2 txt)
        --help
        --inter (interlace, or progressive for jpeg)
        --italics
        --pad=<pad-size-all, default 1>
        --padb=<pad-size-bottom>
        --padl=<pad-size-left>
        --padr=<pad-size-right>
        --padt=<pad-size-top>
        --pattern=<output-filename-pattern, default nc (eg. ncx.png)>
        --quality=<jpeg-quality, default 75>
        --sameh (all character have same height in canvas size)
        --size=<font-size, default 10>
        --strike
        --textfont=<font used in txt format, default cour.ttf>
        --under (underline)
	    <?
	}


	function DumpOptions() {
        echo <<<EOM
        alias   = $opt_alias
        angle   = $opt_angle
        bgcolor = $opt_bgcolor
        bold    = $opt_bold
        chars   = $opt_chars
        color   = $opt_color
        font    = $opt_font
        format  = $opt_format
        inter   = $opt_inter
        italics = $opt_italics
        pad     = $opt_pad
        padb    = $opt_padb
        padl    = $opt_padl
        padr    = $opt_padr
        padt    = $opt_padt
        pattern = $opt_pattern
        quality = $opt_quality
        sameh   = $opt_sameh
        size    = $opt_size
        strike  = $opt_strike
        textfont= $opt_textfont
        under   = $opt_under
EOM
        ;
    }



	###########################################################################
	#
	# Parse Options
	#
	# $opt_font = 'Courier New';
        $opt_size = 10;
        $opt_sameh = 0;
        $opt_angle = 0;
        $opt_alias = 0;
        $opt_italics = 0;
        $opt_bold = 0;
        $opt_under = 0;
        $opt_strike = 0;
        $opt_color = 0;     # black
        $opt_bgcolor = 'trans';
        $opt_format = 'png';
        $opt_quality = 75;
        $opt_inter = 0;
        $opt_chars =
            "!\"#$%&\'()*+,-./0123456789:;<=>?@"
            . "ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`"
            . "abcdefghijklmnopqrstuvwxyz{|}~"
            . "\xA0\xA1\xA2\xA3\xA4\xA5\xA6\xA7\xA8\xA9\xAA\xAB\xAC\xAD\xAE\xAF"
            . "\xB0\xB1\xB2\xB3\xB4\xB5\xB6\xB7\xB8\xB9\xBA\xBB\xBC\xBD\xBE\xBF"
            . "\xC0\xC1\xC2\xC3\xC4\xC5\xC6\xC7\xC8\xC9\xCA\xCB\xCC\xCD\xCE\xCF"
            . "\xD0\xD1\xD2\xD3\xD4\xD5\xD6\xD7\xD8\xD9\xDA\xDB\xDC\xDD\xDE\xDF"
            . "\xE0\xE1\xE2\xE3\xE4\xE5\xE6\xE7\xE8\xE9\xEA\xEB\xEC\xED\xEE\xEF"
            . "\xF0\xF1\xF2\xF3\xF4\xF5\xF6\xF7\xF8\xF9\xFA\xFB\xFC\xFD\xFE\xFF"
            ;
        $opt_padl = 1;
        $opt_padr = 1;
        $opt_padt = 1;
        $opt_padb = 1;

        import_request_variables('pg', 'opt_');

        for ($i = 1; $i < $argc; $i++) {
            $a = explode('=', trim($argv[$i]), 2);
            $key = $a[0];
            if (substr($key, 0, 2) != '--') {
                echo "Invalid option: ".$argv[$i];
                exit;
            } else {
                $key = substr($key, 2);
            }
            if (isset($a[1])) $val = $a[1];
            else $val = 1;

            switch ($key) {
                case 'font':    $opt_font = $val; break;
                case 'size':    $opt_size = $val * 1; break;
                case 'sameh':   $opt_sameh = $val * 1; break;
                case 'angle':   $opt_angle = $val * 1; break;
                case 'alias':   $opt_alias = $val * 1; break;
                case 'italics': $opt_italics = $val; break;
                case 'bold':    $opt_bold = $val * 1; break;
                case 'under':   $opt_under = $val; break;
                case 'strike':  $opt_strike = $val; break;
                case 'color':   $opt_color = $val; break;
                case 'bgcolor': $opt_bgcolor = $val; break;
                case 'format':  $opt_format = $val; break;
                case 'quality': $opt_quality = $val * 1; break;
                case 'inter':   $opt_inter = $val; break;
                case 'textfont':$opt_textfont = $val; break;
                case 'chars':   $opt_chars = $val; break;
                case 'pad':     $opt_pad = $val; break;
                case 'padl':    $opt_padl = $val; break;
                case 'padr':    $opt_padr = $val; break;
                case 'padt':    $opt_padt = $val; break;
                case 'padb':    $opt_padb = $val; break;
                case 'pattern': $opt_pattern = $val; break;
                case 'help':    $opt_help = $val; break;
            }
        }

        if (isset($opt_help) || ! isset($opt_font)) {
            Help();
            exit;
        }

        if ($opt_size < 2) {
            echo "Font-size must be greater than 2";
            exit;
        }

        if (! eregi('\.tt[cf]$', $opt_font)) {
            echo 'Currently only .ttf files are support, please specify .ttf file path. ';
            exit;
        }

        if (! isset($opt_textfont)) {
            $opt_textfont = 'cour.ttf'; #$opt_font;
        }

        # chars process:  \x \[range\]
            $tmp_chars = $opt_chars;
            $groups = preg_match_all('/(\\\\\\[ [^\\]]+ \\\\\\]) | (\\\\.)/x',
                    $tmp_chars, $matches,
                    PREG_SET_ORDER+PREG_OFFSET_CAPTURE);

            if ($groups) {
                $mark = 0;
                $opt_chars = '';
                for ($grp = 0; $grp < $groups; $grp++) {
                    $offset_0 = $matches[$grp][0][1];
                    $opt_chars .= substr($tmp_chars, $mark, $offset_0 - $mark);

                    $is_range = true;
                    if (isset($matches[$grp][2])) {
                        # \x
                        $grp2_char = substr($matches[$grp][2][0], 1);
                        if ($grp2_char == 'n') { $opt_chars .= "\n"; }
                        elseif ($grp2_char == 't') { $opt_chars .= "\t"; }
                        elseif ($grp2_char == 'r') { $opt_chars .= "\r"; }
                        else { $opt_chars .= $grp2_char; }
                        $mark = $offset_0 + 2;
                    } else {
                        # \[..-..\]
                        $grp1 = substr($matches[$grp][1][0], 2, -2);
                        $dash = strpos($grp1, '-');
                        $r1 = hexdec(substr($grp1, 0, $dash));
                        $r2 = hexdec(substr($grp1, $dash+1));
                        for ($ri = $r1; $ri < $r2; $ri++)
                            $opt_chars .= chr($ri);
                        $mark = $offset_0 + strlen($grp1) + 4;
                    }
                }
                $opt_chars .= substr($tmp_chars, $mark);
            }

        if (! isset($opt_pattern)) {
            if ($opt_size < 7) $opt_pattern = 'tc';         #  0..6     5
            elseif ($opt_size < 10) $opt_pattern = 'sc';    #  7..9     8
            elseif ($opt_size < 17) $opt_pattern = 'nc';    # 10..16    12
            elseif ($opt_size < 32) $opt_pattern = 'lc';    # 17..31    24
            else $opt_pattern = 'hc';                       # 32..inf   48
        }

        if (isset($opt_pad)) {
            $opt_padl = $opt_pad;
            $opt_padr = $opt_pad;
            $opt_padt = $opt_pad;
            $opt_padb = $opt_pad;
        }


        # Prepare options
        $opt_color = hexdec($opt_color);
        $opt_trans = false;
        if ($opt_bgcolor == 'trans') {
            $opt_trans = true;
            $opt_bgcolor = ((~ $opt_color) & 0x00FFFFFF); # ^ 0x000000CC;
        } else {
            $opt_bgcolor = hexdec($opt_bgcolor);
        }


	###########################################################################
	#
	# Main
	#
    $opt_nchars = strlen($opt_chars);
    $textbox = imagettfbbox($opt_size, 0, $opt_font, $opt_chars);
    $t_left = $textbox[0];
    $t_bottom = -$textbox[1];
    $t_right = $textbox[2];
    $t_top = -$textbox[5];
    $t_width = abs($t_right - $t_left);
    $t_height = abs($t_top - $t_bottom);
    $char = $opt_chars;
    $filename = "$opt_pattern-alphabet.$opt_format";

    for ($i = 0; $i < $opt_nchars; $i++) {
        if ($i >= 0) {
            $char = substr($opt_chars, $i, 1);
            $filename = $opt_pattern.CharName($char).'.'.$opt_format;
        }

        echo "\nGenerate $filename \t'$char' ";

        $charbox = imagettfbbox($opt_size, $opt_angle, $opt_font, $char);
        $c_bl_x = $charbox[0];  # base-x
        $c_bl_y =-$charbox[1];  # base-y
        $c_br_x = $charbox[2];
        $c_br_y =-$charbox[3];
        $c_tr_x = $charbox[4];
        $c_tr_y =-$charbox[5];
        $c_tl_x = $charbox[6];
        $c_tl_y =-$charbox[7];
        $c_width = abs(min($c_bl_x, $c_br_x, $c_tl_x, $c_tr_x) -
                       max($c_bl_x, $c_br_x, $c_tl_x, $c_tr_x)) + 1;
        $c_height = abs(min($c_bl_y, $c_br_y, $c_tl_y, $c_tr_y) -
                        max($c_bl_y, $c_br_y, $c_tl_y, $c_tr_y)) + 1;

        echo "[ $c_bl_x  $c_bl_y  $c_br_x  $c_br_y  $c_tl_x  $c_tl_y  $c_tr_x  $c_tr_y ]";

        # this is really a simple solution for sameh-option.
        if (! $opt_sameh) {
            $t_height = $c_height;
            $t_bottom = $c_bl_y;
        }

        $imagew = $c_width + $opt_padl + $opt_padr + $opt_bold;
        $imageh = $t_height + $opt_padt + $opt_padb;
        $im = imagecreate($imagew, $imageh);

        # bgcolor should be allocated at first. (So they will have index 0)
        $bgcolor = snm_newcolor($im, $opt_bgcolor);
        if ($opt_trans) imagecolortransparent($im, $bgcolor);

        $color = snm_newcolor($im, $opt_color);
        $colorindex = snm_colorindex($im, $opt_color);
        if ($opt_alias) $colorindex = -$colorindex;

        imagefilledrectangle($im, 0, 0, $imagew, $imageh, $bgcolor);

        for ($boldx = 0; $boldx <= $opt_bold; $boldx++)
            imagettftext($im, $opt_size, $opt_angle,
                         $opt_padl - $c_bl_x + $boldx,
                         $imageh - $opt_padb + $t_bottom,
                         $colorindex, $opt_font, $char);


        switch ($opt_format) {
            case 'png':
                imageinterlace($im, $opt_inter);
                imagepng($im, $filename);
                break;
            case 'jpg':
                imageinterlace($im, $opt_inter);
                imagejpeg($im, $filename, $opt_quality);
                break;
            case 'gif':
                imagegif($im, $filename);
                break;
            case 'wbmp':
                imagewbmp($im, $filename);
                break;
            case 'gd':
                imagegd($im, $filename);
                break;
            case 'gd2':
                imagegd2($im, $filename);
                break;
            case 'txt':
                ImageText($im, $filename);
                break;
            default:
                die("File format $opt_format isn't supported. ");
        }

        snm_freecolor($im, $color);
        snm_freecolor($im, $bgcolor);
        imagedestroy($im);
    }

?>
