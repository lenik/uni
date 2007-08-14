package cmt::lexutil;       # helpers for Parse::Lex

use strict;
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(yylex2
                 );

# ($toknam, $tokval) = yylex2($lexer)->($text)->()
sub yylex2 {
    my $lex = shift;
    sub {
        my $text = shift;
        return $lex unless defined $text;

        $lex->from($text);
        sub {
            my $t;
            my ($n, $v);
          T:while (1) {
                $t = $lex->next;
                last if !defined($t) || $lex->eoi;
                $n = $t->name;
                $v = $t->text;
              N:while (1) {
                    # print "<$n>\n";
                    next T if substr($n, 0, 2) eq '__';
                    if (substr($n, 0, 1) eq '_') {
                        if (ref $v eq 'ARRAY') {
                            ($n, $v) = @$v;
                        } else {
                            $n = $v;
                        }
                        next N
                    }
                    last
                }
                last
            }
            $n = '_'.$n if $n =~ /^[a-zA-Z]/;
            ($n, $v)
        }
    }
}

1