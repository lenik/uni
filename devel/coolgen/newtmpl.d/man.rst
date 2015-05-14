<?php
    $MAN_SECTIONS = [
        '1' => 'Command Usage',
        '2' => 'System Call',
        '3' => 'Library Function',
        '4' => 'special files',
        '5' => 'File Format',
        '6' => 'Games',
        '7' => 'Miscellaneous',
        '8' => 'System Administration',
        '9' => 'Kernel Routines',
    ];

    $man_name = $Name;
    $man_sect = pathinfo($cfg['__FILE__'], PATHINFO_EXTENSION);
    $man_group = $MAN_SECTIONS[$man_sect];

    $man_version = '';
    $year = date("Y");
    $month = date("M");

?>.. <?= $man_name ?> - manual
.. Copyright (C) <?= $year ?> <?= $cfg['user'] ?>
..
.. This program is free software; you can redistribute it and/or modify
.. it under the terms of the GNU General Public License as published by
.. the Free Software Foundation; either version 2 of the License, or
.. (at your option) any later version.
..
.. This program is distributed in the hope that it will be useful,
.. but WITHOUT ANY WARRANTY; without even the implied warranty of
.. MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
.. GNU General Public License for more details.
.. You should have received a copy of the GNU General Public License
.. along with this program; if not, write to the Free Software
.. Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
..

===============================================================================
<?= $man_name ?>
===============================================================================

-------------------------------------------------------------------------------
<?= $words ?>
-------------------------------------------------------------------------------

:Author: <?= $cfg['user'] ?> <<?= $cfg['email'] ?>>
:Copyright: GNU GPLv3
:Date: <?= $month ?> <?= $year ?>
:Id: $Id:$
:Manual group: <?= $man_group ?>
:Manual section: <?= $man_sect ?>
:Version: <?= $man_version ?>


SYNOPSIS
========

    <?= $man_name ?> [ARGUMENTS]



DESCRIPTION
===========

<?= $words ?>



OPTIONS
=======



FILES
=====



ENVIRONMENT
===========



EXAMPLES
========

    * <?= $man_name ?> example-arguments

        example-description



SEE ALSO
========



BUGS
====

None currently known. However, this implimentation of the <?= $man_name ?> command was written by a computer hobbyist; the source code has not, as of <?= $man_version ?>, been reviewed by an experienced C programmer.

Please report any bugs, problems, queries, experiences, etc. directly to the author.
