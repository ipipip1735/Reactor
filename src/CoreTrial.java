import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static java.time.Duration.ofSeconds;

/**
 * Created by Administrator on 2019/10/23 13:16.
 */
public class CoreTrial {

    public static void main(String[] args) {
        CoreTrial coreTrial = new CoreTrial();

//        coreTrial.create();//创建流
//        coreTrial.baseSubscriber();//系统自带流
        coreTrial.hot(); //冷/热模式
//        coreTrial.async();


    }

    private void async() {
        Flux.just(1, 2, 3, 4)
                .log()
                .map(i -> i * 2)
                .subscribeOn(Schedulers.parallel())
                .subscribe(System.out::println);

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
        }).sample(Duration.ofSeconds(3))
                .publish();

        publish.subscribe(i -> System.out.println("[1]" + i));
        publish.subscribe(i -> System.out.println("[2]" + i));
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

    private void create() {

        //创建多值发布者
        //方式一
        Flux<String> flux = Flux.just("foo", "bar", "foobar");

        //方式二
        List<String> iterable = Arrays.asList("foo", "bar", "foobar");
//        Flux<String> flux = Flux.fromIterable(iterable);



        //创建单值发布者
        //方式一
//        Mono<String> mono = Mono.just("ok");


        //方式二：从计算结果创建
        Mono<Long> mono = Flux.just("foo", "bar", "foobar").count();

    }
}
