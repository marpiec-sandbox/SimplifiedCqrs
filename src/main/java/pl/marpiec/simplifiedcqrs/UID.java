package pl.marpiec.simplifiedcqrs;

import java.io.Serializable;

public class UID implements Serializable, Comparable<UID> {

    private static final UID ZERO = new UID(0L);

    private final long id;

    public UID(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UID &&
                id == ((UID) obj).id;
    }

    @Override
    public int hashCode() {
        // copied from hash code implementation in Long class
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public int compareTo(UID another) {
        return this.id < another.id ? -1 : (this.id == another.id ? 0 : 1);
    }

    @Override
    public String toString() {
        return Long.toString(id);
    }

    public static UID parseOrZero(String str) {
        try {
            return new UID(Long.parseLong(str));
        } catch (NumberFormatException e) {
            return ZERO;
        }
    }
}
