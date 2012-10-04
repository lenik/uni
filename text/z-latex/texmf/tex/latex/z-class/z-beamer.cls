% vim: set filetype=tex :
\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{z-beamer}[2012/10/02 boDz Templates]

\newif\ifz@CJK

\DeclareOption{CJK}{\z@CJKtrue}
\DeclareOption{CJKspace}{\PassOptionsToPackage{CJKspace}{xeCJK}}
\DeclareOption{CJKmath}{\PassOptionsToPackage{CJKmath}{xeCJK}}
\DeclareOption{CJKglue}{\PassOptionsToPackage{CJKglue}{xeCJK}}
\DeclareOption*{\PassOptionsToClass{\CurrentOption}{beamer}}
\ProcessOptions\relax

\LoadClass{beamer}
\RequirePackage{z-latexenh}

\ifz@CJK
    \RequirePackage[slantfont,boldfont]{xeCJK}
    \setCJKmainfont{WenQuanYi Micro Hei}
    %\setCJKmathfont{WenQuanYi Micro Hei}
    \XeTeXlinebreaklocale "zh"
    \XeTeXlinebreakskip = 0pt plus 1pt
    \linespread{1.2}
    \setlength{\columnsep}{.5in}
\fi

% Convert accent-glyph sequence to a single Unicode character.
    \RequirePackage{xunicode}

% xltxtra is included in fontspec. xltxtra < fontspec
    %\RequirePackage{xltxtra}

% fontspec provides auto interfaces to AAT and OpenType fonts thru NFSS. fontspec > l3kernel
    \RequirePackage{fontspec}

% common used fonts
    \RequirePackage[textcomp,manfnt]{z-fonts}

% private abbreviations
    \RequirePackage{z-abbrev}

\RequirePackage{ifthen}     % \ifthenelse
%\RequirePackage{suffix}     % \WithSuffix *

\mode<presentation> {
    \setbeamercovered{transparent}
}

\newcommand<>{\itemframe}[2]{
    \begin{frame}
        \frametitle{#1}
        \begin{itemize}[#3]
            #2
        \end{itemize}
    \end{frame}
}

\newcommand<>{\enumframe}[2]{
    \begin{frame}
        \frametitle{#1}
        \begin{enumerate}[#3]
            #2
        \end{enumerate}
    \end{frame}
}

\newcommand<>{\itemsframe}[3][]{
    \ifthenelse{\equal{#1}{}}{\subsection{#2}}{\subsection[#1]{#2}}
    \itemframe#4{#2}{#3}
}

\newcommand<>{\itemssframe}[3][]{
    \ifthenelse{\equal{#1}{}}{\subsection{#2}}{\subsection[#1]{#2}}
    \itemframe#4{#2}{#3}
}

\newcommand<>{\enumsframe}[3][]{
    \ifthenelse{\equal{#1}{}}{\subsection{#2}}{\subsection[#1]{#2}}
    \enumframe#4{#2}{#3}
}

\newcommand<>{\enumssframe}[3][]{
    \ifthenelse{\equal{#1}{}}{\subsection{#2}}{\subsection[#1]{#2}}
    \enumframe#4{#2}{#3}
}
