package pl.marpiec.simplifiedcqrs;

import java.io.Serializable;

public abstract class Aggregate implements Serializable {

    protected UID id;
    protected int version;

    protected Aggregate() {
        this(null, 0);
    }

    protected Aggregate(UID id, int version) {
        this.id = id;
        this.version = version;
    }

    public void incrementVersion() {
        version++;
    }

    public abstract Aggregate copy();

    public UID getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public void setId(UID id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
