package cmt::english;

use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(plural
                 presentp
                 pastp
                 perfectp
                 );

# V - vowel
# C - consonant
sub isVe    { /[aeiou]e$/ }
sub isCe    { /[^aeiou]e$/ }
sub isVVC   { /[aeiou][aeiou][^aeiou]$/ }
sub isCVC   { /[^aeiou][aeiou][^aeiou]$/ }

sub plural {
    # [C]y      -> *ies     fly *ies
    # [xsaiou]  -> ~es      fax -es, go -es
    # [else]    -> ~s       dog -s
    local $_ = shift;
    return substr($_, 0, -1) . 'ies'
                        if /[^aeiou]y$/;
    return $_.'es'      if /[xsaiou]$/;
    return $_.'s';
}

sub irr_init;

our $irr_pastp      = undef;
our $irr_perfectp   = undef;

sub presentp {
    # [V]e -> ~ying     d(ie) -ying
    # [C]e  -> ~ing     lov(e) -ing
    # [VVC] -> ~ing     eat -ing
    # [CVC] -> ~$ing    program -ming
    # (else)-> ~ing     act -ing
    local($_) = shift;
    my $x = substr($_, -1);
    return $_.'ying'    if isVe;
    return $_.'ing'     if isCe;
    return $_.'ing'     if isVVC;
    return $_.$x.'ing'  if isCVC;
    return $_.'ing';
}

sub _pastp {
    # [V]e  -> ~d       die -d
    # [C]e  -> ~d       love -d
    # [VVC] -> ~ed      eat -ed
    # [CVC] -> ~$ed     program -med
    # [else]-> ~ed      act -ed
    local($_) = shift;
    my $x = substr($_, -1);
    return $_.'d'       if isVe;
    return $_.'d'       if isCe;
    return $_.'ed'      if isVVC;
    return $_.$x.'ed'   if isCVC;
    return $_.'ed';
}

sub pastp {
    my $word = shift;
    irr_init if not defined $irr_pastp;
    my $irr = $irr_pastp->{$word};
    return $irr if defined $irr;
    return _pastp($word);
}

sub perfectp {
    my $word = shift;
    irr_init if not defined $irr_perfectp;
    my $irr = $irr_perfectp->{$word};
    return $irr if defined $irr;
    return _pastp($word);
}

sub irr_init {
    while (<DATA>) {
        if (m/(\S+)\s+(\S+)\s+(\S+)/) {
            my ($verb, $past, $perfect) = ($1, $2, $3);
            my ($past) = $past =~ m/^(\w+)/;
            my ($perfect) = $perfect =~ m/^(\w+)/;
            $irr_pastp->{$verb} = $past;
            $irr_perfectp->{$verb} = $perfect;
        }
    }
}

1
__DATA__
arise                 arose                 arisen
awake                 awoke                 awoken
be                    was/were              been
bear                  bore                  borne
beat                  beat                  beaten
become                became                become
begin                 began                 begun
bend                  bent                  bent
beset                 beset                 beset
bet                   bet/betted            bet
bid                   bid                   bid
bind                  bound                 bound
bite                  bit                   bitten
bleed                 bled                  bled
blow                  blew                  blown
break                 broke                 broken
breed                 bred                  bred
bring                 brought               brought
broadcast             broadcast             broadcast
build                 built                 built
burn                  burnt/burned          burnt/burned
burst                 burst                 burst
buy                   bought                bought
cast                  cast                  cast
catch                 caught                caught
choose                chose                 chosen
cling                 clung                 clung
come                  came                  come
cost                  cost                  cost
creep                 crept                 crept
cut                   cut                   cut
deal                  dealt                 dealt
dig                   dug                   dug
dive                  dived/dove            dived
do                    did                   done
draw                  drew                  drawn
dream                 dreamt/dreamed        dreamt/dreamed
drink                 drank                 drunk
drive                 drove                 driven
eat                   ate                   eaten
fall                  fell                  fallen
feed                  fed                   fed
feel                  felt                  felt
fight                 fought                fought
find                  found                 found
fit                   fit                   fit
flee                  fled                  fled
fling                 flung                 flung
fly                   flew                  flown
forbid                forbade               forbidden
forget                forgot                forgotten
forego/forgo          forewent              foregone
forgive               forgave               forgiven
forsake               forsook               forsaken
foretell              foretold              foretold
freeze                froze                 frozen
get                   got                   got/gotten
give                  gave                  given
go                    went                  gone
grind                 ground                ground
grow                  grew                  grown
hang                  hung                  hung
hang                  hanged                hanged
have                  had                   had
hear                  heard                 heard
hide                  hid                   hidden
hit                   hit                   hit
hold                  held                  held
hurt                  hurt                  hurt
keep                  kept                  kept
kneel                 knelt                 knelt
know                  knew                  known
lay                   laid                  laid
lead                  led                   led
lean                  leant/leaned          lean/leaned
leap                  leapt/leaped          leapt/leaped
learn                 learnt/learned        learnt/learned
leave                 left                  left
lend                  lent                  lent
let                   let                   let
lie                   lay                   lain
light                 lit/lighted           lit/lighted
lose                  lost                  lost
make                  made                  made
mean                  meant                 meant
meet                  met                   met
misspell              misspelt/misspelled   misspelt/misspelled
mistake               mistook               mistaken
mow                   mowed                 mown/mowed
overcome              overcame              overcome
overdo                overdid               overdone
overtake              overtook              overtaken
overthrow             overthrew             overthrown
pay                   paid                  paid
plead                 pleaded/plead         pleaded/plead
prove                 proved                proved/proven
put                   put                   put
quit                  quit                  quit
read                  read                  read
rid                   rid                   rid
ride                  rode                  ridden
ring                  rang                  rung
rise                  rose                  risen
run                   ran                   run
saw                   sawed                 sawn/sawed
say                   said                  said
see                   saw                   seen
seek                  sought                sought
sell                  sold                  sold
send                  sent                  sent
set                   set                   set
sew                   sewed                 sewn/sewed
shake                 shook                 shaken
shear                 sheared               sheared/shorn
shed                  shed                  shed
shine                 shone                 shone
shoot                 shot                  shot
show                  showed                shown/showed
shrink                shrank                shrunk
shut                  shut                  shut
sing                  sang                  sung
sink                  sank                  sunk
sit                   sat                   sat
sleep                 slept                 slept
slay                  slew                  slayed/slain
slide                 slid                  slid
sling                 slung                 slung
slit                  slit                  slit
smell                 smelt/smelled         smelt/smelled
smite                 smote                 smitten
sow                   sowed                 sown/sowed
speak                 spoke                 spoken
speed                 sped/speeded          sped/speeded
spell                 spelt/spelled         spelt/spelled
spend                 spent                 spent
spill                 spilt/spilled         spilt/spilled
spin                  spun                  spun
spit                  spat                  spat
split                 split                 split
spoil                 spoilt/spoiled        spoilt/spoiled
spread                spread                spread
spring                sprang                sprung
stand                 stood                 stood
steal                 stole                 stolen
stick                 stuck                 stuck
sting                 stung                 stung
stink                 stank                 stunk
stride                strode                stridden
strike                struck                struck
strive                strove                striven
swear                 swore                 sworn
sweep                 swept                 swept
swell                 swelled               swelled/swollen
swim                  swam                  swum
swing                 swung                 swung
take                  took                  taken
teach                 taught                taught
tear                  tore                  torn
tell                  told                  told
think                 thought               thought
thrive                thrived/throve        thrived
throw                 threw                 thrown
thrust                thrust                thrust
tread                 trod                  trodden
understand            understood            understood
uphold                upheld                upheld
upset                 upset                 upset
wake                  woke/waked            woken/waked
wear                  wore                  worn
weave                 wove/weaved           woven/weaved
wed                   wedded/wed            wedded/wed
weep                  wept                  wept
win                   won                   won
wind                  wound                 wound
withdraw              withdrew              withdrawn
withhold              withheld              withheld
withstand             withstood             withstood
wring                 wrung                 wrung
write                 wrote                 written
