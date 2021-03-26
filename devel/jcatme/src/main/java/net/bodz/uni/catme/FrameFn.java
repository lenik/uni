package net.bodz.uni.catme;

public class FrameFn {

    public static String dump(IFrame frame) {
        StringBuilder sb = new StringBuilder(200);
        int i = 0;
        sb.append("Frame stack:\n");
        while (frame != null) {
            sb.append("    [");
            sb.append(i);
            sb.append("] ");
            if (frame.isFile()) {
                FileFrame f = (FileFrame) frame;
                sb.append("file: " + f.file);
            } else {
                String type = frame.getClass().getSimpleName();
                sb.append(type + ": " + i + ": " + frame);
            }
            frame = frame.getParent();
        }
        return sb.toString();
    }

}
