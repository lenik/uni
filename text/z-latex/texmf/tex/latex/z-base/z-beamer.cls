% vim: set filetype=tex :
\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{z-beamer}[2012/10/02 boDz Templates]

\newif\ifz@CJK@
\newif\ifz@titling@
\DeclareOption{CJK}{\z@CJK@true}
\DeclareOption{CJKspace}{\PassOptionsToPackage{CJKspace}{xeCJK}}
\DeclareOption{CJKglue}{\PassOptionsToPackage{CJKglue}{xeCJK}}

\DeclareOption{titling}{\z@titling@true}

\DeclareOption*{\PassOptionsToClass{\CurrentOption}{beamer}}
\ProcessOptions\relax

\LoadClass{beamer}
\RequirePackage{z-latexenh}

\ifz@CJK@
    \RequirePackage[slantfont, boldfont, CJKmath=true]{xeCJK}
    \setCJKmainfont{WenQuanYi Micro Hei}
    %\setCJKmathfont{WenQuanYi Micro Hei}
    \RequirePackage{z-zhcore}
\fi

% Convert accent-glyph sequence to a single Unicode character.
    \RequirePackage{xunicode}

% xltxtra is included in fontspec. xltxtra < fontspec
    %\RequirePackage{xltxtra}

% fontspec provides auto interfaces to AAT and OpenType fonts thru NFSS. fontspec > l3kernel
    \RequirePackage{fontspec}

% common used fonts
    \RequirePackage[textcomp,manfnt]{z-fonts}

% \thetitle, \theauthor, \thedate
    \ifz@titling@
        \RequirePackage{titling}
        %\let\orig@title\title
        %\let\orig@subtitle\subtitle
        %\let\orig@author\author
        %\def\thetitle{}
        %\def\thesubtitle{}
        %\def\theauthor{}
        %\renewcommand\title[2][]{\orig@title{#1}{#2}\def\thetitle{#2}}
        %\renewcommand\subtitle[1]{\orig@subtitle{#1}\def\thesubtitle{#1}}
        %\renewcommand\author[1]{\orig@author{#1}\def\theauthor{#1}}
    \fi
    % \authorEmail
    \def\theauthorEmail{yourname@example.com}
    \newcommand\authorEmail[1]{\def\theauthorEmail{#1}}

% private abbreviations
    \RequirePackage{z-abbrev}

% class specific implementation
    \RequirePackage{z-beamer-impl}

