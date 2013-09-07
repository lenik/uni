use 5.008;
use ExtUtils::MakeMaker;

my $DIRT_HOME=$ENV{DIRT_HOME};

push @ARGV, "PREFIX=$DIRT_HOME/0"
    if -f "$DIRT_HOME/.dirt";

my $INCLUDE = join ' ', map { $_ ne '' ? "-I$_" : '' }
                split(/\s*;\s*/, $ENV{INCLUDE});
my $LIB     = join ' ', map { $_ ne '' ? "-L$_" : ''  }
                split(/\s*;\s*/, $ENV{LIB});

WriteMakefile(
    NAME              => 'Net::Adapter',
    VERSION_FROM      => 'lib/Net/Adapter.pm', # finds $VERSION
    PREREQ_PM         => {}, # e.g., Module::Name => 1.1
    ($] >= 5.005 ?     ## Add these new keywords supported since 5.005
      (ABSTRACT_FROM  => 'lib/Net/Adapter.pm', # retrieve abstract from module
       AUTHOR         => 'Xima Lenik <lenik@bodz.net>') : ()),
    LIBS              => $LIB.' -lIPHlpApi -luuid -llibcpf', # e.g., '-lm'
    DEFINE            => '', # e.g., '-DHAVE_SOMETHING'
    INC               => '-I. '.$INCLUDE, # e.g., '-I. -I/usr/include/other'
	# Un-comment this if you add C files to link with later:
    OBJECT            => '$(O_FILES)', # link all the C files too
);

sub MY::postamble {'

IPTYPES_H='.$ENV{CIRK_HOME}.'/sdk/psdk/include/iptypes.h

Adapter.c : Adapter.class.xsh Adapter.typemap.xsh

Adapter.class.xsh : $(IPTYPES_H)
	fun ~stwrap -r -s PIP_ADAPTER_INFO $(IPTYPES_H) >$@

Adapter.typemap.xsh : Adapter.xs
	fun ~typemap Adapter.xs >$@

all :: blib/arch/auto/Net/Adapter/netcon.dll

blib/arch/auto/Net/Adapter/% : arch/auto/Net/Adapter/%
	$(CP) arch/auto/Net/Adapter/$* $@


clean ::
	- $(RM_F) \
	    *.class.xsh \
	    *.typemap.xsh
'}
