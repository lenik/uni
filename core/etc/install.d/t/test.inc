
- _use: labat::win32

- _defun:
    hello: print "Hello, World!\n";

    tell:STD: |-
        my $ctx = shift;
        print "You are $name\n";

- _define:
    TEST: 123
