package net.bodz.uni.catme;

public class FrameFn {

    public static String dump(IFrame frame) {
        return dump(frame, "");
    }

    public static String dump(IFrame frame, String prefix) {
        StringBuilder sb = new StringBuilder(200);
        int index = 0;
        sb.append(prefix + "Frame stack:\n");
        int line = frame.getCurrentLine();
        int column = frame.getCurrentColumn();
        while (frame != null) {
            sb.append(prefix + "    [");
            sb.append(index);
            sb.append("] ");
            if (frame.isFile()) {
                FileFrame f = (FileFrame) frame;
                sb.append("file: " + f.file);
            } else {
                String type = frame.getClass().getSimpleName();
                sb.append(type + ": " + frame);
            }
            sb.append(" (at line " + (line + 1) + ", column " + (column + 1) + ")");
            line = frame.getParentLine();
            column = frame.getParentColumn();
            frame = frame.getParent();
            sb.append("\n");
        }
        return sb.toString();
    }

}
