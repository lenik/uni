package cmt::zio;

=head1 NAME

cmt::zio - i/o with zipped stream

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::lang('_or');
use cmt::log(2);
    our $LOGNAME    = __PACKAGE__;
    our $LOGLEVEL   = 1;
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use FileHandle;

our @ISA        = qw(Exporter);
our @EXPORT_OK  = qw(open_file
                     open_gzfile
                     open_auto
                     open_fh
                     h_iter
                     hi_iter
                     hs_iter
                     files_iter
                     zfiles_iter
                     zargs_iter
                     );

# INITIALIZORS
sub open_file;
sub open_gzfile;
sub open_auto;
sub open_fh;
sub h_iter;
sub hi_iter;
sub hs_iter;
sub files_iter;
sub zfiles_iter;
sub zargs_iter;

=head1 SYNOPSIS

    use cmt::zio;
    mysub(arguments...)

=head1 i/o with zipped stream

B<cmt::zio> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::zio-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut

sub open_file {
    my $path = shift;
    my $fh = new FileHandle($path) or die "can't open $path: $!";
    my $h = sub { local *__ANON__ = "<file:$path>";
        local $_ = <$fh>;
        $fh->close() unless defined $_;
        $_
    };
    $h
}

sub open_gzfile {
    require Compress::Zlib;
    my $path = shift;
    my $fh;
    if (ref $path) {
        $fh = $path;
    } else {
        $fh = new FileHandle($path) or die "can't open $path: $!";
    }
    my $zh = Compress::Zlib::gzopen($fh, 'rt');
    my $h = sub { local *__ANON__ = "<gzfile:$path>";
        local $_;
        if ($zh->gzreadline($_)) {
            $_
        } else {
            $zh->gzclose();
            $fh->close();
            undef
        }
    };
    $h
}

sub open_auto {
    my $path = shift;
    if ($path =~ /\.\w*gz$/) {
        open_gzfile($path);
    } else {
        open_file($path);
    }
}

sub open_fh {
    my ($fh, $nam, $unzip) = @_;
    if ($unzip) {
        require Compress::Zlib;
        $fh = Compress::Zlib::gzopen($fh, 'rt');
        return sub { local *__ANON__ = "<zh:$nam>";
            local $_;
            if ($fh->gzreadline($_)) {
                $_
            } else {
                $fh->gzclose();
                undef
            }
        };
    }
    sub { local *__ANON__ = "<h:$nam>";
        local $_ = <$fh>;
        $_
    }
}

sub h_iter {
    my $h = shift;
    my $iter;
    $iter = sub { local *__ANON__ = "<h_iter:$h>";
        $_ = $h->();
        defined $_ ? $iter : undef;
    }
}

sub hi_iter { # handle-iterator
    my $hi = shift;
    my $hi_iter;
    $hi_iter = sub { local *__ANON__ = '<hi_iter:$hi>';
        my $h = $hi->();
        if (defined $h) {
            my $h_iter = h_iter($h);
            my $chain;
            $chain = sub { local *__ANON__ = '<h_chain>';
                $h_iter = $h_iter->();
                if (defined $h_iter) {
                    $chain
                } else {
                    $hi_iter->()
                }
            };
            $chain->()
        } else {
            undef
        }
    }
}

sub hs_iter { # handle-set
    my @q = @_;
    my $hi = sub { local *__ANON__ = '<hs2hi>';
        shift @q;
    };
    hi_iter($hi)
}

sub files_iter {
    my $openf = undef;
       $openf = shift if ref $_[0];
    my @q = @_;
    my $hi = sub { local *__ANON__ = '<files2hi>';
        my $file = shift @q;
        return undef unless defined $file;
        defined $openf ? $openf->($file) : open_file($file);
    };
    hi_iter($hi)
}

sub zfiles_iter {
    files_iter \&open_auto, @_;
}

sub zargs_iter {
    my $unzip = shift;
    if (@ARGV) {
        zfiles_iter(@ARGV);
    } else {
        my @fhs = @_ ? @_ : (\*STDIN);
        my @hs = map { open_fh($_, "$_", $unzip) } @fhs;
        hs_iter(@hs)
    }
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=head1 SEE ALSO

The L<cmt/"Perl_simple_module_template">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
1