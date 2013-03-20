package pl.marpiec.simplifiedcqrs.memoryimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.marpiec.simplifiedcqrs.Aggregate;
import pl.marpiec.simplifiedcqrs.AggregateStoreCache;
import pl.marpiec.simplifiedcqrs.UID;

import java.util.Map;
import java.util.WeakHashMap;

public class AggregateStoreCacheMemoryImpl implements AggregateStoreCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregateStoreCacheMemoryImpl.class);

    private final Map<Class<? extends Aggregate>, Map<UID, Aggregate>> cache =
            new WeakHashMap<Class<? extends Aggregate>, Map<UID, Aggregate>>();

    @Override
    public Aggregate get(Class<? extends Aggregate> aggregateClass, UID id) {
        Map<UID, Aggregate> aggregatesForType = cache.get(aggregateClass);
        if (aggregatesForType == null) {
            return null;
        } else {
            Aggregate aggregate = aggregatesForType.get(id);
            if (aggregate == null) {
                return null;
            } else {
                if(LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Aggregate has been taken from cache. [type:"+aggregate.getClass().getName()+", id:"+aggregate.getId()+", version:"+aggregate.getVersion()+"]");
                }
                return aggregate;
            }
        }
    }

    @Override
    public void put(Aggregate aggregate) {
        Map<UID, Aggregate> aggregatesForType = cache.get(aggregate.getClass());
        if (aggregatesForType == null) {
            aggregatesForType = new WeakHashMap<UID, Aggregate>();
            cache.put(aggregate.getClass(), aggregatesForType);
        }
        aggregatesForType.put(aggregate.getId(), aggregate);
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Aggregate has been put in cache. [type:"+aggregate.getClass().getName()+", id:"+aggregate.getId()+", version:"+aggregate.getVersion()+"]");
        }
    }
}
