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
	    $id = ParseId('$Id: fontmap.php,v 1.4 2004-11-30 00:46:56 dansei Exp $');
	    ?>
[FONTMAP] Font Map Generator
Written by Snima Denik,  Version <?=$id['rev']?>,  Last updated <?=$id['time']?>

Syntax:
    fontmap
        --help
        --family=<font-name or ttf-file>
        --size=<font-size, default 10>
        --sameh (all character have same height in canvas size)
        --angle=<rotate-angle(degree), default 0>
        --alias (no anti-alias)
        --italics
        --bold
        --under
        --strike
        --color=<font-color, default 0 (black)>
        --bgcolor=<background-color, default transparent>
        --format=<image-format, default png>
        --chars=<characters-to-map, default [0-9a-z]>
        --pad=<pad-size-all, default 1>
        --padl=<pad-size-left>
        --padr=<pad-size-right>
        --padt=<pad-size-top>
        --padb=<pad-size-bottom>
        --pattern=<output-filename-pattern, default nc (eg. ncx.png)>
	    <?
	}


	function DumpOptions() {
        echo <<<EOM
        family  = $opt_family
        size    = $opt_size
        sameh   = $opt_sameh
        angle   = $opt_angle
        alias   = $opt_alias
        italics = $opt_italics
        bold    = $opt_bold
        under   = $opt_under
        strike  = $opt_strike
        color   = $opt_color
        bgcolor = $opt_bgcolor
        format  = $opt_format
        chars   = $opt_chars
        padl    = $opt_padl
        padr    = $opt_padr
        padt    = $opt_padt
        padb    = $opt_padb
        pattern = $opt_pattern
EOM
        ;
    }



	###########################################################################
	#
	# Parse Options
	#
	# $opt_family = 'Courier New';
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
        $opt_chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
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
                case 'family':  $opt_family = $val; break;
                case 'size':    $opt_size = $val * 1; break;
                case 'sameh':   $opt_sameh = $val * 1; break;
                case 'angle':   $opt_angle = $val * 1; break;
                case 'alias':   $opt_alias = $val * 1; break;
                case 'italics': $opt_italics = $val; break;
                case 'bold':    $opt_bold = $val; break;
                case 'under':   $opt_under = $val; break;
                case 'strike':  $opt_strike = $val; break;
                case 'color':   $opt_color = $val; break;
                case 'bgcolor': $opt_bgcolor = $val; break;
                case 'format':  $opt_format = $val; break;
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

        if (isset($opt_help) || ! isset($opt_family)) {
            Help();
            exit;
        }

        if ($opt_size < 2) {
            echo "Font-size must be greater than 2";
            exit;
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

        if (! eregi('\.tt[cf]$', $opt_family)) {
            echo 'Currently only .ttf files are support, please specify .ttf file path. ';
            exit;
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
    $textbox = imagettfbbox($opt_size, 0, $opt_family, $opt_chars);
    $t_left = $textbox[0];
    $t_bottom = -$textbox[1];
    $t_right = $textbox[2];
    $t_top = -$textbox[5];
    $t_width = abs($t_right - $t_left);
    $t_height = abs($t_top - $t_bottom);
    $char = $opt_chars;
    $filename = "$opt_pattern-alphabet.$opt_format";
    echo "$t_bottom\n";
    for ($i = 0; $i < $opt_nchars; $i++) {
        if ($i >= 0) {
            $char = substr($opt_chars, $i, 1);
            $filename = $opt_pattern.CharName($char).'.'.$opt_format;
        }

        echo "\nGenerate $filename \t'$char' ";

        $charbox = imagettfbbox($opt_size, $opt_angle, $opt_family, $char);
        $c_left = $charbox[0];
        $c_bottom = -$charbox[1];
        $c_right = $charbox[2];
        $c_top = -$charbox[5];
        $c_width = abs($c_right - $c_left) + 1;
        $c_height = abs($c_top - $c_bottom) + 1;

        echo "[ $c_left  $c_bottom  $c_right  $c_top ]";

        # this is really a simple solution for sameh-option.
        if (! $opt_sameh) {
            $t_height = $c_height;
            $t_bottom = $c_bottom;
        }

        $imagew = $c_width + $opt_padl + $opt_padr;
        $imageh = $t_height + $opt_padt + $opt_padb;
        $im = imagecreate($imagew, $imageh);

        # bgcolor should be allocated at first. (So they will have index 0)
        $bgcolor = snm_newcolor($im, $opt_bgcolor);
        if ($opt_trans) imagecolortransparent($im, $bgcolor);

        $color = snm_newcolor($im, $opt_color);
        $colorindex = snm_colorindex($im, $opt_color);
        if ($opt_alias) $colorindex = -$colorindex;

        imagefilledrectangle($im, 0, 0, $imagew, $imageh, $bgcolor);

        imagettftext($im, $opt_size, $opt_angle,
                     $opt_padl - $c_left,
                     # $opt_padt + $t_top - $c_top + $c_height,
                     $imageh - $opt_padb + $t_bottom,
                     $colorindex, $opt_family, $char);

        imagepng($im, $filename);
        snm_freecolor($im, $color);
        snm_freecolor($im, $bgcolor);
        imagedestroy($im);
    }

?>
