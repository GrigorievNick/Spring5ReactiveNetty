package scala;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.reactive.RedisReactiveCommands;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.google.common.collect.Iterables.elementsEqual;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;

public class FluxExample {

    public static void main(String[] args) throws Exception {
        String[] vals = {"a", "b", "c", "d", "e", "f", "g", "y", "t", "h", "f", "s", "l", "p", "o"};
        Flux.fromIterable(asList(copyOfRange(vals, 0, 2)))
                .concatWith(Flux.fromIterable(asList(copyOfRange(vals, 2, 4))))
                .concatWith(Flux.fromIterable(asList(copyOfRange(vals, 4, 6))))
                .concatWith(Flux.fromIterable(asList(copyOfRange(vals, 6, 8))))
                .concatWith(Flux.fromIterable(asList(copyOfRange(vals, 8, 15))))
                .buffer(vals.length)
                .subscribe(list -> {
                    if (list.size() != vals.length && !elementsEqual(list, asList(copyOfRange(vals, 0, vals.length))))
                        System.out.println("Shirt attempt " + list);
                });


        RedisClient client = RedisClient.create("redis://172.17.0.2:6379/0");
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        config.setMinIdle(0);
        int count = 10000;
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
        reactive.get(key1)
                .subscribeOn(Schedulers.immediate())
                .subscribe(k -> {
                    reactive.setAutoFlushCommands(false);
                    reactive.lrange(key, 0, 1).map(val -> asList(val, k, "country" + i))
                            .concatWith(reactive.lrange(key, 2, 3).map(val -> asList(val, k, "country" + i)))
                            .concatWith(reactive.lrange(key, 4, 5).map(val -> asList(val, k, "country" + i)))
                            .concatWith(reactive.lrange(key, 6, 7).map(val -> asList(val, k, "country" + i)))
                            .concatWith(reactive.lrange(key, 8, 14).map(val -> asList(val, k, "country" + i)))
                            .buffer(vals.length)
                            .subscribeOn(Schedulers.immediate())
                            .subscribe(list -> {
                                List<String> takeRedisResult = list.stream().map(l -> l.get(0)).collect(Collectors.toList());
                                if (list.size() != vals.length && !elementsEqual(takeRedisResult, asList(vals)))
                                    System.out.println("Shirt attempt " + i + " " + list);
                                pool.returnObject(conneciton);
                                latch.countDown();
                            });
                    reactive.flushCommands();
                    reactive.setAutoFlushCommands(true);
                });
        // terminating
    }
}
