package pl.marpiec.simplifiedcqrs.memoryimpl;

import pl.marpiec.simplifiedcqrs.UID;
import pl.marpiec.simplifiedcqrs.UIDGenerator;

public class UIDGeneratorMemoryImpl implements UIDGenerator {

    private long id = 0L;

    @Override
    public synchronized UID nextUID() {
        id++;
        return new UID(id);
    }
}
