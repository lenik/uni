package net.bodz.uni.typedet;

import java.io.File;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * Detect field types.
 */
@ProgramName("typedet")
public class TypeDet
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(TypeDet.class);

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        for (String path : args) {
            File file = new File(path);
            String ext = FilePath.extension(file);
            if (ext == null) {
                logger.error("unknown file type: " + file.getName());
                continue;
            }


            switch (ext) {
            case "xls":
            case "xlsx":
            }
        }
    }

    public static void main(String[] args)
            throws Exception {
        new TypeDet().execute(args);
    }

}
