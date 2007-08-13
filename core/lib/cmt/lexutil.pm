package cmt::lexutil;       # helpers for Parse::Lex

use strict;
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(yylex2
                 );

sub yylex2 {
    my $lexer = shift;
    sub {
        my $t;
        my ($n, $v);
      T:while (1) {
            $t = $lexer->next;
            last if !defined($t) || $lexer->eoi;
            $n = $t->name;
            $v = $t->text;
          N:while (1) {
                # print "<$n>\n";
                next T if substr($n, 0, 2) eq '__';
                if (substr($n, 0, 1) eq '_') {
                    if (ref $v eq 'ARRAY') {
                        ($n, $v) = @$v;
                        next N;
                    } else {
                        $n = $v;
                    }
                }
                last
            }
            last
        }
        $n = '_'.$n if $n =~ /^[a-zA-Z]/;
        ($n, $v)
    }
}

1