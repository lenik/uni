
#include <stdio.h>
#include <unistd.h>

#if 0
typedef struct MZ_Header {
    u16_t magic;
};

struct PE_Header {
};

struct Opt_Header {
    u16_t magic;                        /* 0b 01 */
    u16_t linker_ver;
    u32_t code_size;
    u32_t data_size;
    u32_t udata_size;
};
#endif

int main(int argc, char **argv) {
	// getopt(argc, argv, "f");

}

// end
