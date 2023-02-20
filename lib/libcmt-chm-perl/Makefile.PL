#!/usr/bin/perl

=head1 NAME

MakeMaker - 
=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::log(2);
    $LOGNAME    = 'MakeMaker'; # $0 =~ /([^\/\\.]+)(?:\.\w+)*$/;
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: - @VERSION@ @DATE@ @TIME@ - $');
use ExtUtils::MakeMaker;
use File::Copy;
use File::Spec;

our $opt_prefix = '/usr/local';
our $opt_destdir;

sub _main; sub _version; sub _help;

sub _boot {
    for (@ARGV) {
        $opt_prefix=$1      if /^--prefix=(.*)$/;
        $opt_destdir=$1     if /^--destdir=(.*)$/;
    }
    _main;
}

=head1 SYNOPSIS

B<MakeMaker>
    S<[ B<-q> | B<--quiet> ]>
    S<[ B<-v> | B<--verbose> ]>
    S<[ B<-h> | B<--help> ]>
    S<[ B<--version> ]>
    S<[ B<--> ]>
    S<[ I<...the rest of arguments...> ]>

=head1 DESCRIPTION

B<MakeMaker> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-MakeMaker-RESOLVES.

=head1 OPTIONS

=over 8

=item B<-m> | B<--my-option>

...

=item B<-q> | B<--quiet>

Repeat this option to suppress unimportant information to display.

=item B<-v> | B<--verbose>

Repeat this option to display more detailed information.

=item B<-h> | B<--help>

Display a breif help page and exit(0).

=item B<--version>

Display a short version information and exit(0).

=back

=head1 EXAMPLES

=over 4

=item Show help

    MakeMaker --help

=back

=head1 ENVIRONMENT

=over 8

=item TEMP, TMP

TEMP(or TMP, if $TEMP directory isn't existed) directory used to create
temporary files.

=back

=cut
sub _help {
    &_version;
    print "\nSyntax: \n    $0 [OPTION] [--] ...\n", <<'EOM';

Common options:
        --destdir=DIR       Specify the dest dir, / by default.
        --prefix=PATH       Specify the install prefix, /usr/local by default.
    -q, --quiet             Repeat to get less info
    -v, --verbose           Repeat to get more info
    -h, --help              Show this help page
        --version           Print the version info
EOM
}

exit (_boot or 0);

sub _main {
    my $prefix = $opt_prefix;
        $prefix = "$opt_destdir$prefix" if defined $opt_destdir;
    
    local @ARGV=();
    push @ARGV, "INSTALL_BASE=$prefix";
    
    my $INCLUDE = join ' ', map { $_ ne '' ? "-I$_" : '' }
                    split(/\s*;\s*/, $ENV{INCLUDE});
    my $LIB     = join ' ', map { $_ ne '' ? "-L$_" : ''  }
                    split(/\s*;\s*/, $ENV{LIB});

    system('echo ........................prepare...................');
    system('pwd');
    system('make -C chmlib');
    system('echo ........................prepare finished..................');
    
    WriteMakefile(
        NAME              => 'cmt::libchm',
        VERSION_FROM      => 'lib/cmt/libchm.pm', # finds $VERSION
        PREREQ_PM         => {}, # e.g., Module::Name => 1.1
        ($] >= 5.005 ?     ## Add these new keywords supported since 5.005
          (ABSTRACT_FROM  => 'lib/cmt/libchm.pm', # retrieve abstract from module
           AUTHOR         => 'Xima Lenik <lenik@bodz.net>') : ()),
        #DIR               => ['chmlib'],
        LIBS              => [], # e.g., '-lm'
        DEFINE            => '', # e.g., '-DHAVE_SOMETHING'
        INC               => '-I. -Ichmlib', # e.g., '-I. -I/usr/include/other'
        MYEXTLIB          => 'chmlib/chm_lib.o chmlib/lzx.o',
        OBJECT            => '$(O_FILES)', # link all the C files too
    );
    if  (eval {require ExtUtils::Constant; 1}) {
      # If you edit these definitions to change the constants used by this module,
      # you will need to use the generated const-c.inc and const-xs.inc
      # files to replace their "fallback" counterparts before distributing your
      # changes.
      my @names = (qw(CHM_COMPRESSED CHM_ENUMERATE_ALL CHM_ENUMERATE_DIRS
		     CHM_ENUMERATE_FILES CHM_ENUMERATE_META CHM_ENUMERATE_NORMAL
		     CHM_ENUMERATE_SPECIAL CHM_ENUMERATOR_CONTINUE
		     CHM_ENUMERATOR_FAILURE CHM_ENUMERATOR_SUCCESS CHM_MAX_PATHLEN
		     CHM_PARAM_MAX_BLOCKS_CACHED CHM_RESOLVE_FAILURE
		     CHM_RESOLVE_SUCCESS CHM_UNCOMPRESSED));
      ExtUtils::Constant::WriteConstants(
                                         NAME         => 'cmt::libchm',
                                         NAMES        => \@names,
                                         DEFAULT_TYPE => 'IV',
                                         C_FILE       => 'const-c.inc',
                                         XS_FILE      => 'const-xs.inc',
                                      );

    }
    else {
        foreach my $file ('const-c.inc', 'const-xs.inc') {
            my $fallback = File::Spec->catfile('fallback', $file);
            copy ($fallback, $file) or die "Can't copy $fallback to $file: $!";
        }
    }

    return 0;
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)

=head1 HACKING

(No Information)

=cut
# (MODULE FUNCTIONS)

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=cut
sub _version {
    print "[$LOGNAME]  \n";
    print "Written by Lenik,  Version 0.$RCSID{rev},  Last updated at $RCSID{date}\n";
}

=head1 SEE ALSO

The L<cmt/"Perl_simple_cli_program_template">

=head1 AUTHOR

Lenik (谢继雷) <lenik (at) bodz.net>

=cut
