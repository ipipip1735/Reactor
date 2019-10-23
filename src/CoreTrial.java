import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;

import static java.time.Duration.ofSeconds;

/**
 * Created by Administrator on 2019/10/23 13:16.
 */
public class CoreTrial {

    public static void main(String[] args) {
        CoreTrial fluxTrial = new CoreTrial();

//        fluxTrial.flux();
//        fluxTrial.mono();
//        fluxTrial.baseSubscriber();
//        fluxTrial.hot();
        fluxTrial.async();


    }

    private void mono() {

        //方式一：使用静态方法
//        Mono.just(1).subscribe(System.out::println);


        //方式二：运算结果
        Mono<Long> mono = Flux.range(1, 5).count();
        mono.subscribe(System.out::println);


    }

    private void async() {
        Flux.just(1, 2, 3, 4)
                .publishOn(Schedulers.parallel())
                .log()
                .publishOn(Schedulers.parallel())
                .log()
                .subscribeOn(Schedulers.parallel())
                .subscribe(i->{
                    System.out.println(Thread.currentThread().getName());
                });

        System.out.println("ok");

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void hot() {

        ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
            for (int i = 0; i < 10; i++) {
                fluxSink.next(i);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).sample(ofSeconds(3))
                .publish();

        publish.subscribe(System.out::println);
        publish.subscribe(System.out::println);
        publish.connect();

        System.out.println("ok");


    }

    private void baseSubscriber() {

        BaseSubscriber<String> baseSubscriber = new BaseSubscriber<String>() {
            public void hookOnSubscribe(Subscription subscription) {
                System.out.println("~~" + getClass().getSimpleName() + ".hookOnSubscribe~~");
                System.out.println("Subscribed is " + subscription);

                request(1);
            }

            public void hookOnNext(String value) {
                System.out.println("~~" + getClass().getSimpleName() + ".hookOnNext~~");
                System.out.println("value is " + value);
                request(1);
            }
        };

        Flux.just("one", "two", "three").subscribe(baseSubscriber);


    }

    private void flux() {


        //例一：使用Just
        Flux<String> flux = Flux.just("foo", "bar", "foobar");

        //例二：使用容器
//        List<String> iterable = Arrays.asList("foo", "bar", "foobar");
//        Flux<String> flux = Flux.fromIterable(iterable);


        flux.subscribe(System.out::println);

    }
}
