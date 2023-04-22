#include <cstdlib>
#include <sstream>
#include <iostream>

using namespace std;

int main() {
    stringbuf sb("hello");

    cout << "content: " << sb.str() << endl;
    return 0;
}