package material.pooling;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class ObjectPool<T> {
    private final Queue<T> pool;
    private final Supplier<T> objectFactory;

    public ObjectPool(int initialSize, Supplier<T> objectFactory) {
        this.pool = new ConcurrentLinkedQueue<>();
        this.objectFactory = objectFactory;

        for (int i = 0; i < initialSize; i++) {
            pool.add(objectFactory.get());
        }
    }

    public T acquire() {
        T object = pool.poll();
        return object != null ? object : objectFactory.get();
    }

    public void release(T object) {
        pool.offer(object);
    }
}

