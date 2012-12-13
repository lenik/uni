package net.bodz.bas.std.rfc.oid;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class OidVector
        implements Serializable {

    private static final long serialVersionUID = 1L;

    final int[] vector;

    public OidVector(int... vector) {
        if (vector == null)
            throw new NullPointerException("vector");
        this.vector = vector;
    }

    /**
     * Format the given OID to a string.
     *
     * @param delim
     *            The separator used to delimit oid numbers.
     * @return The formatted string.
     */
    public String format(char delim) {
        StringBuilder buf = new StringBuilder(vector.length * 15);
        for (int i = 0; i < vector.length; i++) {
            if (i != 0)
                buf.append(delim);
            buf.append(vector[i]);
        }
        return buf.toString();
    }

    public String toPath() {
        return format('/');
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(vector);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OidVector other = (OidVector) obj;
        if (!Arrays.equals(vector, other.vector))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return format('.');
    }

    static boolean explicitOnly = true;

    /**
     * Get OID from an annoated element.
     *
     * If there's no @Oid found on the the annoated element, <code>null</code> is returned.
     *
     * @param element
     *            The referred element.
     * @param altName
     *            The alternative name to use when there's no @Oid annotation.
     * @return Internal integer array for the OID, if there is any.
     */
    public static OidVector parse(AnnotatedElement element, String altName) {
        Oid oidAnnotation = element.getAnnotation(Oid.class);
        if (oidAnnotation != null) {
            int[] vector = oidAnnotation.value();
            return new OidVector(vector);
        }

        if (explicitOnly)
            return null;

        StringTokenizer st = new StringTokenizer(altName, ".");
        List<Integer> ints = new ArrayList<Integer>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            ints.add(token.hashCode());
        }

        int[] vector = new int[ints.size()];
        for (int i = 0; i < ints.size(); i++)
            vector[i] = ints.get(i);

        return new OidVector(vector);
    }

    /**
     * Parse the OID string to internal integer array.
     *
     * @param delim
     *            The delimitor to use.
     * @param oidString
     *            The OID string, non-<code>null</code>.
     * @return The result array parsed, <code>null</code> if failed to parse.
     */
    public static OidVector parse(char delim, String oidString) {
        if (oidString == null)
            throw new NullPointerException("oidString");
        List<Integer> list = new ArrayList<Integer>(oidString.length() / 2);
        StringTokenizer st = new StringTokenizer(oidString, String.valueOf(delim));
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            try {
                int num = Integer.parseInt(token);
                list.add(num);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        int[] vector = new int[list.size()];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = list.get(i);
        }
        return new OidVector(vector);
    }

}