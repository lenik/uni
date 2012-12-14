
package webcon::fext::util::ID3v1;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT/;
use vars qw/@ID3v1_HEADER @ID3v1_Genre/;


@ID3v1_HEADER = (
	['magic', 	3], 		# ='TAG'
	['title', 	30],
	['artist', 	30],
	['album', 	30],
	['year', 	4],
	['comment', 29],
	['track', 	1], 		# =0, if not used.
	['genre', 	1],
	);

@ID3v1_Genre = (
	#[0,5			[1,6			[2,7			[3,8			[4,9
	'Blues', 		'Classic Rock',	'Country',		'Dance',		'Disco', 		# 0
	'Funk', 		'Grunge', 		'Hip-Hop', 		'Jazz', 		'Metal',
	'New Age', 		'Oldies', 		'Other',		'Pop',			'R&B',			# 10
	'Rap',			'Reggae',		'ROck',			'Techno',		'Industrial',
	'Alternative', 	'Ska', 			'Death Metal', 	'Pranks', 		'Soundtrack', 	# 20
	'Euro-Techno', 	'Ambient', 		'Trip-Hop', 	'Vocal', 		'Jazz+Funk',
	'Fusion', 		'Trance', 		'Classical', 	'Instrumental', 'Acid', 		# 30
	'House', 		'Game', 		'Sound Clip', 	'Gospel', 		'Noise',
	'AlternRock', 	'Bass', 		'Soul', 		'Punk', 		'Space', 		# 40
	'Meditative', 	'Instrumental Pop','Instrumental Rock','Ethnic','Gothic',
	'Darkwave', 	'Techno-Industirla','Electronic','Pop-Folk', 	'Eurodance', 	# 50
	'Dream', 		'Southern Rock', 'Comedy', 		'Cult', 		'Gangsta',
	'Top 40', 		'Christian Rap', 'Pop/Funk', 	'Jungle', 		'Native American',
	'Cabaret', 		'New Wave', 	'Psychadelic', 	'Rave', 		'Showtimes', 	# 65
	'Trailer', 		'Lo-Fi', 		'Tribal', 		'Acid Punk', 	'Acid Jazz', 	# 70
	'Polka', 		'Retro', 		'Musical', 		'Roc & Roll', 	'Hard Rock',
	'Folk', 		'Folk-Rock', 	'National Folk','Swing', 		'Fast Fusion', 	# 80
	'Bebob', 		'Latin', 		'Revival', 		'Celtic', 		'Bluegrass',
	'Avantgarde', 	'Gothic Rock',	'Progressive Rock','Psychedelic Rock','Symphonic Rock',
	'Slow Rock', 	'Big Band', 	'Chorus', 		'Easy Listening','Acoustic', 	# 95
	'Humour', 		'Speech', 		'Chanson', 		'Opera', 		'Chamber Music',
	'Sonata', 		'Symphony', 	'Booty Bass', 	'Primus', 		'Porn Groove', 	# 105
	'Satire', 		'Slow Jam', 	'Club', 		'Tango', 		'Samba', 		# 110
	'Folklore', 	'Ballad', 		'Power Ballad', 'Rhythmic Soul','Freestyle',
	'Duet', 		'Punk Rock', 	'Drum Solo', 	'A capella', 	'Euro-House', 	# 120
	'Dance Hall',
	);

@ISA = qw(Exporter);
@EXPORT = qw(
	@ID3v1_HEADER
	@ID3v1_Genre
	);

1;
