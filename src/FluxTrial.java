import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;

import static java.time.Duration.ofSeconds;

/**
 * Created by Administrator on 2019/10/23 13:16.
 */
public class FluxTrial {

    public static void main(String[] args) {
        FluxTrial fluxTrial = new FluxTrial();

//        fluxTrial.base();
//        fluxTrial.baseSubscriber();
//        fluxTrial.hot();
        fluxTrial.async();


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

    private void base() {


        Flux<String> flux = Flux.just("foo", "bar", "foobar");

        List<String> iterable = Arrays.asList("foo", "bar", "foobar");
//        Flux<String> flux = Flux.fromIterable(iterable);

    }
}
