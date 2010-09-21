package cmt::winuser;

use strict;
use Exporter;
#use os-portable
use vars qw/@ISA @EXPORT/;
use Win32::API;

my $MessageBox_NPPN = new Win32::API('user32', 'MessageBox', 'NPPN', 'N');

sub escape {
    my $str = shift;
    $str =~ s/\\/\\\\/g;
    $str =~ s/\"/\\\"/g;
    $str =~ s/\'/\\\'/g;
    $str =~ s/\n/\\n/g;
    return $str;
}


my %msgbox_tokens = (
    'ok'        => 0b0000000000000001,
    'cancel'    => 0b0000000000000010,
    'yes'       => 0b0000000000000100,
    'no'        => 0b0000000000001000,
    'abort'     => 0b0000000000010000,
    'retry'     => 0b0000000000100000,
    'ignore'    => 0b0000000001000000,
    '?'         => 0b0000000100000000,
    '!'         => 0b0000001000000000,
    'i'         => 0b0000010000000000,
    'x'         => 0b0000100000000000,
    'modal'     => 0b1000000000000000,
    );

my @msgbox_ids = qw(
    null    ok      cancel  abort   retry   ignore  yes     no
    close   help
    );

sub msgbox {
    my ($msg, $title, @options) = @_;
    $title ||= __PACKAGE__;
    @options = qw/ok/ unless @options;

    my $com = 0;
    for (@options) {
        die "Invalid option: $_" if (! $msgbox_tokens{$_});
        $com |= $msgbox_tokens{$_};
    }

    my $button = 0;

    my $com1 = $com & 0b1111111;

    # ok only
    if ($com1 == 0b0000001) { $button = 0; }

    # ok cancel
    if ($com1 == 0b0000011) { $button = 1; $button |= 0x100 if ($options[0] eq 'cancel'); }

    # abort retry ignore
    if ($com1 == 0b1110000) { $button = 2; $button |= 0x100 if ($options[0] eq 'retry');
                                           $button |= 0x200 if ($options[0] eq 'ignore'); }

    # yes no cancel
    if ($com1 == 0b0001110) { $button = 3; $button |= 0x100 if ($options[0] eq 'no');
                                           $button |= 0x200 if ($options[0] eq 'cancel'); }

    # yes no
    if ($com1 == 0b0001100) { $button = 4; $button |= 0x100 if ($options[0] eq 'no'); }

    # retry cancel
    if ($com1 == 0b0100010) { $button = 5; $button |= 0x100 if ($options[0] eq 'cancel'); }

    $button |= 0x10 if $com & $msgbox_tokens{'x'};
    $button |= 0x20 if $com & $msgbox_tokens{'?'};
    $button |= 0x30 if $com & $msgbox_tokens{'!'};
    $button |= 0x40 if $com & $msgbox_tokens{'i'};

    $button |= 0x1000 if $com & $msgbox_tokens{'modal'};

    $msg = escape $msg;
    $title = escape $title;

    my $id = $MessageBox_NPPN->Call(0, $msg, $title, $button);

    return $msgbox_ids[$id];
}

our %_ID = (
            IDOK        => 1,
            IDCANCEL    => 2,
            IDABORT     => 3,
            IDRETRY     => 4,
            IDIGNORE    => 5,
            IDYES       => 6,
            IDNO        => 7,
            IDCLOSE     => 8,
            IDHELP      => 9
            );

our %_WM = (
            WM_NULL                     => 0x0000,
            WM_CREATE                   => 0x0001,
            WM_DESTROY                  => 0x0002,
            WM_MOVE                     => 0x0003,
            WM_SIZE                     => 0x0005,
            WM_ACTIVATE                 => 0x0006,
            WM_SETFOCUS                 => 0x0007,
            WM_KILLFOCUS                => 0x0008,
            WM_ENABLE                   => 0x000A,
            WM_SETREDRAW                => 0x000B,
            WM_SETTEXT                  => 0x000C,
            WM_GETTEXT                  => 0x000D,
            WM_GETTEXTLENGTH            => 0x000E,
            WM_PAINT                    => 0x000F,
            WM_CLOSE                    => 0x0010,
            WM_QUERYENDSESSION          => 0x0011,
            WM_QUIT                     => 0x0012,
            WM_QUERYOPEN                => 0x0013,
            WM_ERASEBKGND               => 0x0014,
            WM_SYSCOLORCHANGE           => 0x0015,
            WM_ENDSESSION               => 0x0016,
            WM_SHOWWINDOW               => 0x0018,
            WM_WININICHANGE             => 0x001A,
            WM_SETTINGCHANGE            => 0x001A,
            WM_DEVMODECHANGE            => 0x001B,
            WM_ACTIVATEAPP              => 0x001C,
            WM_FONTCHANGE               => 0x001D,
            WM_TIMECHANGE               => 0x001E,
            WM_CANCELMODE               => 0x001F,
            WM_SETCURSOR                => 0x0020,
            WM_MOUSEACTIVATE            => 0x0021,
            WM_CHILDACTIVATE            => 0x0022,
            WM_QUEUESYNC                => 0x0023,
            WM_GETMINMAXINFO            => 0x0024,
            WM_PAINTICON                => 0x0026,
            WM_ICONERASEBKGND           => 0x0027,
            WM_NEXTDLGCTL               => 0x0028,
            WM_SPOOLERSTATUS            => 0x002A,
            WM_DRAWITEM                 => 0x002B,
            WM_MEASUREITEM              => 0x002C,
            WM_DELETEITEM               => 0x002D,
            WM_VKEYTOITEM               => 0x002E,
            WM_CHARTOITEM               => 0x002F,
            WM_SETFONT                  => 0x0030,
            WM_GETFONT                  => 0x0031,
            WM_SETHOTKEY                => 0x0032,
            WM_GETHOTKEY                => 0x0033,
            WM_QUERYDRAGICON            => 0x0037,
            WM_COMPAREITEM              => 0x0039,
            WM_GETOBJECT                => 0x003D,
            WM_COMPACTING               => 0x0041,
            WM_WINDOWPOSCHANGING        => 0x0046,
            WM_WINDOWPOSCHANGED         => 0x0047,
            WM_POWER                    => 0x0048,
            WM_COPYDATA                 => 0x004A,
            WM_CANCELJOURNAL            => 0x004B,
            WM_NOTIFY                   => 0x004E,
            WM_INPUTLANGCHANGEREQUEST   => 0x0050,
            WM_INPUTLANGCHANGE          => 0x0051,
            WM_TCARD                    => 0x0052,
            WM_HELP                     => 0x0053,
            WM_USERCHANGED              => 0x0054,
            WM_NOTIFYFORMAT             => 0x0055,
            WM_CONTEXTMENU              => 0x007B,
            WM_STYLECHANGING            => 0x007C,
            WM_STYLECHANGED             => 0x007D,
            WM_DISPLAYCHANGE            => 0x007E,
            WM_GETICON                  => 0x007F,
            WM_SETICON                  => 0x0080,
            WM_NCCREATE                 => 0x0081,
            WM_NCDESTROY                => 0x0082,
            WM_NCCALCSIZE               => 0x0083,
            WM_NCHITTEST                => 0x0084,
            WM_NCPAINT                  => 0x0085,
            WM_NCACTIVATE               => 0x0086,
            WM_GETDLGCODE               => 0x0087,
            WM_SYNCPAINT                => 0x0088,
            WM_NCMOUSEMOVE              => 0x00A0,
            WM_NCLBUTTONDOWN            => 0x00A1,
            WM_NCLBUTTONUP              => 0x00A2,
            WM_NCLBUTTONDBLCLK          => 0x00A3,
            WM_NCRBUTTONDOWN            => 0x00A4,
            WM_NCRBUTTONUP              => 0x00A5,
            WM_NCRBUTTONDBLCLK          => 0x00A6,
            WM_NCMBUTTONDOWN            => 0x00A7,
            WM_NCMBUTTONUP              => 0x00A8,
            WM_NCMBUTTONDBLCLK          => 0x00A9,
            WM_KEYFIRST                 => 0x0100,
            WM_KEYDOWN                  => 0x0100,
            WM_KEYUP                    => 0x0101,
            WM_CHAR                     => 0x0102,
            WM_DEADCHAR                 => 0x0103,
            WM_SYSKEYDOWN               => 0x0104,
            WM_SYSKEYUP                 => 0x0105,
            WM_SYSCHAR                  => 0x0106,
            WM_SYSDEADCHAR              => 0x0107,
            WM_KEYLAST                  => 0x0108,
            WM_IME_STARTCOMPOSITION     => 0x010D,
            WM_IME_ENDCOMPOSITION       => 0x010E,
            WM_IME_COMPOSITION          => 0x010F,
            WM_IME_KEYLAST              => 0x010F,
            WM_INITDIALOG               => 0x0110,
            WM_COMMAND                  => 0x0111,
            WM_SYSCOMMAND               => 0x0112,
            WM_TIMER                    => 0x0113,
            WM_HSCROLL                  => 0x0114,
            WM_VSCROLL                  => 0x0115,
            WM_INITMENU                 => 0x0116,
            WM_INITMENUPOPUP            => 0x0117,
            WM_MENUSELECT               => 0x011F,
            WM_MENUCHAR                 => 0x0120,
            WM_ENTERIDLE                => 0x0121,
            WM_MENURBUTTONUP            => 0x0122,
            WM_MENUDRAG                 => 0x0123,
            WM_MENUGETOBJECT            => 0x0124,
            WM_UNINITMENUPOPUP          => 0x0125,
            WM_MENUCOMMAND              => 0x0126,
            WM_CTLCOLORMSGBOX           => 0x0132,
            WM_CTLCOLOREDIT             => 0x0133,
            WM_CTLCOLORLISTBOX          => 0x0134,
            WM_CTLCOLORBTN              => 0x0135,
            WM_CTLCOLORDLG              => 0x0136,
            WM_CTLCOLORSCROLLBAR        => 0x0137,
            WM_CTLCOLORSTATIC           => 0x0138,
            WM_MOUSEFIRST               => 0x0200,
            WM_MOUSEMOVE                => 0x0200,
            WM_LBUTTONDOWN              => 0x0201,
            WM_LBUTTONUP                => 0x0202,
            WM_LBUTTONDBLCLK            => 0x0203,
            WM_RBUTTONDOWN              => 0x0204,
            WM_RBUTTONUP                => 0x0205,
            WM_RBUTTONDBLCLK            => 0x0206,
            WM_MBUTTONDOWN              => 0x0207,
            WM_MBUTTONUP                => 0x0208,
            WM_MBUTTONDBLCLK            => 0x0209,
            WM_MOUSEWHEEL               => 0x020A,
            WM_MOUSELAST                => 0x020A,
            WM_MOUSELAST                => 0x0209,
            WM_PARENTNOTIFY             => 0x0210,
            WM_ENTERMENULOOP            => 0x0211,
            WM_EXITMENULOOP             => 0x0212,
            WM_NEXTMENU                 => 0x0213,
            WM_SIZING                   => 0x0214,
            WM_CAPTURECHANGED           => 0x0215,
            WM_MOVING                   => 0x0216,
            WM_POWERBROADCAST           => 0x0218,
            WM_DEVICECHANGE             => 0x0219,
            WM_MDICREATE                => 0x0220,
            WM_MDIDESTROY               => 0x0221,
            WM_MDIACTIVATE              => 0x0222,
            WM_MDIRESTORE               => 0x0223,
            WM_MDINEXT                  => 0x0224,
            WM_MDIMAXIMIZE              => 0x0225,
            WM_MDITILE                  => 0x0226,
            WM_MDICASCADE               => 0x0227,
            WM_MDIICONARRANGE           => 0x0228,
            WM_MDIGETACTIVE             => 0x0229,
            WM_MDISETMENU               => 0x0230,
            WM_ENTERSIZEMOVE            => 0x0231,
            WM_EXITSIZEMOVE             => 0x0232,
            WM_DROPFILES                => 0x0233,
            WM_MDIREFRESHMENU           => 0x0234,
            WM_IME_SETCONTEXT           => 0x0281,
            WM_IME_NOTIFY               => 0x0282,
            WM_IME_CONTROL              => 0x0283,
            WM_IME_COMPOSITIONFULL      => 0x0284,
            WM_IME_SELECT               => 0x0285,
            WM_IME_CHAR                 => 0x0286,
            WM_IME_REQUEST              => 0x0288,
            WM_IME_KEYDOWN              => 0x0290,
            WM_IME_KEYUP                => 0x0291,
            WM_MOUSEHOVER               => 0x02A1,
            WM_MOUSELEAVE               => 0x02A3,
            WM_CUT                      => 0x0300,
            WM_COPY                     => 0x0301,
            WM_PASTE                    => 0x0302,
            WM_CLEAR                    => 0x0303,
            WM_UNDO                     => 0x0304,
            WM_RENDERFORMAT             => 0x0305,
            WM_RENDERALLFORMATS         => 0x0306,
            WM_DESTROYCLIPBOARD         => 0x0307,
            WM_DRAWCLIPBOARD            => 0x0308,
            WM_PAINTCLIPBOARD           => 0x0309,
            WM_VSCROLLCLIPBOARD         => 0x030A,
            WM_SIZECLIPBOARD            => 0x030B,
            WM_ASKCBFORMATNAME          => 0x030C,
            WM_CHANGECBCHAIN            => 0x030D,
            WM_HSCROLLCLIPBOARD         => 0x030E,
            WM_QUERYNEWPALETTE          => 0x030F,
            WM_PALETTEISCHANGING        => 0x0310,
            WM_PALETTECHANGED           => 0x0311,
            WM_HOTKEY                   => 0x0312,
            WM_PRINT                    => 0x0317,
            WM_PRINTCLIENT              => 0x0318,
            WM_HANDHELDFIRST            => 0x0358,
            WM_HANDHELDLAST             => 0x035F,
            WM_AFXFIRST                 => 0x0360,
            WM_AFXLAST                  => 0x037F,
            WM_PENWINFIRST              => 0x0380,
            WM_PENWINLAST               => 0x038F,
            WM_APP                      => 0x8000,
            WM_USER                     => 0x0400,
            );

our %_VK = (
            VK_DBE_ALPHANUMERIC             => 0x0f0,
            VK_DBE_KATAKANA                 => 0x0f1,
            VK_DBE_HIRAGANA                 => 0x0f2,
            VK_DBE_SBCSCHAR                 => 0x0f3,
            VK_DBE_DBCSCHAR                 => 0x0f4,
            VK_DBE_ROMAN                    => 0x0f5,
            VK_DBE_NOROMAN                  => 0x0f6,
            VK_DBE_ENTERWORDREGISTERMODE    => 0x0f7,
            VK_DBE_ENTERIMECONFIGMODE       => 0x0f8,
            VK_DBE_FLUSHSTRING              => 0x0f9,
            VK_DBE_CODEINPUT                => 0x0fa,
            VK_DBE_NOCODEINPUT              => 0x0fb,
            VK_DBE_DETERMINESTRING          => 0x0fc,
            VK_DBE_ENTERDLGCONVERSIONMODE   => 0x0fd,
            VK_LBUTTON      => 0x01,
            VK_RBUTTON      => 0x02,
            VK_CANCEL       => 0x03,
            VK_MBUTTON      => 0x04,
            VK_BACK         => 0x08,
            VK_TAB          => 0x09,
            VK_CLEAR        => 0x0C,
            VK_RETURN       => 0x0D,
            VK_SHIFT        => 0x10,
            VK_CONTROL      => 0x11,
            VK_MENU         => 0x12,
            VK_PAUSE        => 0x13,
            VK_CAPITAL      => 0x14,
            VK_KANA         => 0x15,
            VK_HANGUL       => 0x15,
            VK_JUNJA        => 0x17,
            VK_FINAL        => 0x18,
            VK_HANJA        => 0x19,
            VK_KANJI        => 0x19,
            VK_ESCAPE       => 0x1B,
            VK_CONVERT      => 0x1C,
            VK_NONCONVERT   => 0x1D,
            VK_ACCEPT       => 0x1E,
            VK_MODECHANGE   => 0x1F,
            VK_SPACE        => 0x20,
            VK_PRIOR        => 0x21,
            VK_NEXT         => 0x22,
            VK_END          => 0x23,
            VK_HOME         => 0x24,
            VK_LEFT         => 0x25,
            VK_UP           => 0x26,
            VK_RIGHT        => 0x27,
            VK_DOWN         => 0x28,
            VK_SELECT       => 0x29,
            VK_PRINT        => 0x2A,
            VK_EXECUTE      => 0x2B,
            VK_SNAPSHOT     => 0x2C,
            VK_INSERT       => 0x2D,
            VK_DELETE       => 0x2E,
            VK_HELP         => 0x2F,
            VK_LWIN         => 0x5B,
            VK_RWIN         => 0x5C,
            VK_APPS         => 0x5D,
            VK_NUMPAD0      => 0x60,
            VK_NUMPAD1      => 0x61,
            VK_NUMPAD2      => 0x62,
            VK_NUMPAD3      => 0x63,
            VK_NUMPAD4      => 0x64,
            VK_NUMPAD5      => 0x65,
            VK_NUMPAD6      => 0x66,
            VK_NUMPAD7      => 0x67,
            VK_NUMPAD8      => 0x68,
            VK_NUMPAD9      => 0x69,
            VK_MULTIPLY     => 0x6A,
            VK_ADD          => 0x6B,
            VK_SEPARATOR    => 0x6C,
            VK_SUBTRACT     => 0x6D,
            VK_DECIMAL      => 0x6E,
            VK_DIVIDE       => 0x6F,
            VK_F1           => 0x70,
            VK_F2           => 0x71,
            VK_F3           => 0x72,
            VK_F4           => 0x73,
            VK_F5           => 0x74,
            VK_F6           => 0x75,
            VK_F7           => 0x76,
            VK_F8           => 0x77,
            VK_F9           => 0x78,
            VK_F10          => 0x79,
            VK_F11          => 0x7A,
            VK_F12          => 0x7B,
            VK_F13          => 0x7C,
            VK_F14          => 0x7D,
            VK_F15          => 0x7E,
            VK_F16          => 0x7F,
            VK_F17          => 0x80,
            VK_F18          => 0x81,
            VK_F19          => 0x82,
            VK_F20          => 0x83,
            VK_F21          => 0x84,
            VK_F22          => 0x85,
            VK_F23          => 0x86,
            VK_F24          => 0x87,
            VK_NUMLOCK      => 0x90,
            VK_SCROLL       => 0x91,
            VK_LSHIFT       => 0xA0,
            VK_RSHIFT       => 0xA1,
            VK_LCONTROL     => 0xA2,
            VK_RCONTROL     => 0xA3,
            VK_LMENU        => 0xA4,
            VK_RMENU        => 0xA5,
            VK_PROCESSKEY   => 0xE5,
            VK_ATTN         => 0xF6,
            VK_CRSEL        => 0xF7,
            VK_EXSEL        => 0xF8,
            VK_EREOF        => 0xF9,
            VK_PLAY         => 0xFA,
            VK_ZOOM         => 0xFB,
            VK_NONAME       => 0xFC,
            VK_PA1          => 0xFD,
            VK_OEM_CLEAR    => 0xFE,
            );

@ISA = qw(Exporter);
@EXPORT = qw(
    msgbox
    %_ID %_WM %_VK
    );

1;
