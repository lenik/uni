package cmt::lexutil;       # helpers for Parse::Lex

use strict;
use Exporter;
use UNIVERSAL('isa');

our @ISA    = qw(Exporter);
our @EXPORT = qw(yylex2
                 lex_dump
                 );

# $plex     = Parse::Lex
# $lexer    = yylex2($plex)
# $yylex    = $lexer->($text)

# ($toknam, $tokval) = yylex2($plex)->($text)->()
sub yylex2 {
    my $plex = shift;
    sub {
        my $text = shift;
        return $plex unless defined $text;

        $plex->from($text);
        sub {
            my $t;
            my ($n, $v);
          T:while (1) {
                $t = $plex->next;
                if (!defined($t) || $plex->eoi) {
                    if (substr($n, 0, 2) eq '__') {
                        ($n, $v) = ('', undef);
                    }
                    last
                }
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

sub lex_dump {
    my $what = shift;
    my $yylex;
    if (isa($what, 'Parse::Lex')) {
        my $plex = $what;
        $yylex = sub {
            my $t = $plex->next;
            if ($plex->eoi || !defined $t) {
                ('', undef)
            } else {
                ($t->name, $t->text)
            }
        };
    } elsif (@_) {
        $yylex = $what->(shift);
    } else {
        $yylex = $what;
    }
    while (my @ret = $yylex->()) {
        last if $ret[0] eq '';
        printf "%-10s '%s'\n", @ret;
    }
}

1