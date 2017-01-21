package scala;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.reactive.RedisReactiveCommands;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.google.common.collect.Iterables.elementsEqual;

public class FluxExample {

    public static void main(String[] args) throws Exception {
        RedisClient client = RedisClient.create("redis://172.17.0.2:6379/0");
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        config.setMinIdle(0);
        int count = 1;
        CountDownLatch downLatch = new CountDownLatch(count);
        GenericObjectPool<StatefulRedisConnection<String, String>> pool =
                ConnectionPoolSupport.createGenericObjectPool(client::connect, config, false);
        for (int i = 0; i < count; i++)
            run(pool, i, downLatch);
        downLatch.await();
        pool.close();
        client.shutdown();
    }

    private static void run(GenericObjectPool<StatefulRedisConnection<String, String>> pool, int i, CountDownLatch latch)
            throws Exception {
        // executing work
        StatefulRedisConnection<String, String> conneciton = pool.borrowObject();

        String key = "key";

        RedisCommands<String, String> sync = conneciton.sync();
        sync.flushall();
        String key1 = "set";
        sync.set(key1, "1");
        String[] vals = {"a", "b", "c", "d", "e", "f", "g", "y", "t", "h", "f", "s", "l", "p", "o"};
        sync.rpush(key, vals);

        RedisReactiveCommands<String, String> reactive = conneciton.reactive();
        reactive.get("nosuchkey").defaultIfEmpty("dummy").subscribe(s -> System.out.println(s + "  ss"));
        Mono.just("dummy").subscribe(v ->
        reactive.get(key1)
                .subscribeOn(Schedulers.immediate())
                .subscribe(k -> {
                    System.out.println("Mono " + Thread.currentThread().toString());
                    reactive.setAutoFlushCommands(false);
                    reactive.lrange(key, 0, 1).map(val -> Arrays.asList(val, k, "country" + i))
                            .mergeWith(reactive.lrange(key, 2, 3).map(val -> Arrays.asList(val, k, "country" + i)))
                            .mergeWith(reactive.lrange(key, 4, 5).map(val -> Arrays.asList(val, k, "country" + i)))
                            .mergeWith(reactive.lrange(key, 6, 7).map(val -> Arrays.asList(val, k, "country" + i)))
                            .mergeWith(reactive.lrange(key, 8, 14).map(val -> Arrays.asList(val, k, "country" + i)))
                            .take(14)
                            .collectList()
                            .subscribeOn(Schedulers.immediate())
                            .subscribe(list -> {
                                System.out.println("Flux " + Thread.currentThread().toString());
                                latch.countDown();
                                List<String> takeRedisResult = list.stream().map(l -> l.get(0)).collect(Collectors.toList());
                                if (list.size() != 8 && !elementsEqual(takeRedisResult, Arrays.asList(Arrays.copyOfRange(vals, 0, 14))))
                                    System.out.println("Shirt attempt " + i + " " + list);
                                pool.returnObject(conneciton);
                            });
                    reactive.flushCommands();
                    reactive.setAutoFlushCommands(true);
                }));
        // terminating
    }
}
