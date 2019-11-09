package core;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.w3c.dom.ls.LSOutput;
import reactor.core.Disposable;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongConsumer;

import static java.time.Duration.ofSeconds;

/**
 * Created by Administrator on 2019/10/23 13:16.
 */
public class FluxTrial {

    public static void main(String[] args) {
        FluxTrial fluxTrial = new FluxTrial();

//        fluxTrial.create();//创建多值流
//        fluxTrial.from();//创建多值流
//        fluxTrial.push();//创建多值流
//        fluxTrial.generate();//创建无限流
//        fluxTrial.never();//创建空流
//        fluxTrial.range();//创建流
//        fluxTrial.just();//创建单值流


//        fluxTrial.delay();//延迟发送
        fluxTrial.block();//阻塞操作


//        fluxTrial.handle();//处理流中的元素
//        fluxTrial.buffer();//缓存多个元素，封装为List再发送，逆操作是flatMap()，分解元素为流，再发送，

//        fluxTrial.merge();//无序合并
//        fluxTrial.flatMap();//处理后无序合并
//        fluxTrial.concat();//有序合并
//        fluxTrial.concatMap();//处理后有序合并


//        fluxTrial.count();//计算元素个数


//        fluxTrial.subscriber();//系统自带流
//        fluxTrial.disposable();//系统自带流
//        fluxTrial.hot(); //冷/热模式
//        fluxTrial.async();


//        fluxTrial.hooks();

    }


    private void concat() {

        //方式一
//        Flux.concat(Flux.just(1, 3, 5), Flux.just(2, 4, 6))
//                .subscribe(System.out::println);


        //方式二
        Flux<Integer> flux1 = Flux.just(1, 3, 5).delayElements(Duration.ofMillis(100));
        Flux<Integer> flux2 = Flux.just(2, 4, 6).delayElements(Duration.ofMillis(100));
        Flux.concat(flux1, flux2)
                .subscribe(System.out::println);

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void delay() {

        Flux.just(1, 2, 3, 4)
//                .delaySequence(Duration.ofMillis(100))
                .delayElements(Duration.ofMillis(100))
                .subscribe(System.out::println);

        System.out.println("end");
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void merge() {

        //方式一
//        Flux.merge(Flux.just(1, 3, 5), Flux.just(2, 4, 6))
//                .subscribe(System.out::println);


        //方式二
        Flux<Integer> flux1 = Flux.just(1, 3, 5).delayElements(Duration.ofMillis(100));
        Flux<Integer> flux2 = Flux.just(2, 4, 6).delayElements(Duration.ofMillis(100));
        Flux.merge(flux1, flux2)
                .subscribe(System.out::println);

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void buffer() {
        Flux.just(1, 2, 3, 4, 5)
                .buffer()
                .subscribe(integers -> {
                    System.out.println(integers);
                });
    }

    private void from() {

        //方式一
        Publisher<Integer> publisher = new Publisher<>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                for (int i = 0; i < 10; i++) s.onNext(i);
            }
        };
        Flux.from(publisher)
                .subscribe(System.out::println);


        //方式二
//        List<String> iterable = Arrays.asList("foo", "bar", "foobar");
//        Flux.fromIterable(iterable)
//                .subscribe(System.out::println);

    }

    private void just() {
        //有限制流
        Flux.just("foo", "bar", "foobar")
                .subscribe(System.out::println);
    }

    private void range() {
        //取值范围
        Flux.range(1, 5)
                .subscribe(System.out::println);

    }

    private void never() {
        //空流
        Flux.never()
                .subscribe(System.out::println);
    }

    private void generate() {

        Flux.generate((SynchronousSink<Integer> synchronousSink) -> {
            synchronousSink.next(1);
        }).map(integer -> integer + 1)
                .subscribe(System.out::println);

    }

    private void push() {


        Flux.create(emitter -> {
//        Flux.push(emitter -> {
            //第一个线程中发送
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        emitter.next(i);
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            //第二个线程中发送
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 10; i < 20; i++) {
                        emitter.next(i);
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }).subscribe(System.out::println);

    }

    private void concatMap() {

        Flux.just(1, 2, 3, 4, 5)
                .flatMap(i -> {//flatMap()合并时使用merge() ，即无序异步操作
//                .concatMap(i -> {//concatMap()合并时使用concat()，即有序同步操作
                    return Flux.just("one" + i, "two" + i, "three" + i, "four" + i)
                            .delayElements(Duration.ofMillis(100));
                }).subscribe(i -> System.out.println("subscribe|" + i));

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handle() {
        Flux.just(1, 2, 3, 4, 5)
                .handle((integer, synchronousSink) -> {
                    System.out.println("~~handle~~");
                    System.out.println(integer);
                    synchronousSink.next(integer);
                })
                .subscribe(i -> System.out.println("subscribe|" + i));
    }

    private void count() {
        Mono<Long> mono = Flux.just("foo", "bar", "foobar")
                .count();
    }

    private void hooks() {

        //doFirst()  doFinally()
//        Flux.just(1, 2, 3, 4)
//                .doFirst(() -> {
//
//                    while (true){
//                        try {
//                            System.out.println("-");
//                            Thread.sleep(1000L);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                })
//                .doFinally(signalType -> {
//                    System.out.println(signalType);
//                                        while (true) {
//                                            try {
//                                                System.out.println("-");
//                                                Thread.sleep(1000L);
//                                            } catch (InterruptedException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                })
//        .subscribe(System.out::println);


    }

    private void flatMap() {


        Flux.just(1, 2, 3, 4)
                .flatMap(integer -> {
                    return Flux.just(integer, integer * 2)
                            .delayElements(Duration.ofMillis(100));
                })
                .subscribe(System.out::println);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void block() {
        //阻塞操作
//        long l = Flux.interval(Duration.ofSeconds(1))
//                .log()
//                .take(5)
//                .blockLast();
//        System.out.println(l);


        //非阻塞方式
//        Flux.interval(Duration.ofSeconds(1))
//                .take(5)
//        .subscribe(System.out::println);
//        try {
//            Thread.sleep(6000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }


    private void disposable() {


        Disposable disposable = Flux.interval(Duration.ofSeconds(1))
                .subscribe(System.out::println);

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        disposable.dispose();

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


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

    private void subscriber() {


        //方式一：使用Subscriber
        Subscriber<Integer> subscriber = new Subscriber<>() {
            Subscription s;

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("~~" + getClass().getSimpleName() + ".onSubscribe~~");

                this.s = s;
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("~~" + getClass().getSimpleName() + ".onNext~~");

                System.out.println(integer);
                if (integer > 5) s.cancel();

            }

            @Override
            public void onError(Throwable t) {
                System.out.println("~~" + getClass().getSimpleName() + ".onError~~");

            }

            @Override
            public void onComplete() {
                System.out.println("~~" + getClass().getSimpleName() + ".onComplete~~");

            }
        };
        Flux.range(1, 10).subscribe(subscriber);


        //方式二：使用BaseSubscriber
//        BaseSubscriber<String> baseSubscriber = new BaseSubscriber<String>() {
//            public void hookOnSubscribe(Subscription subscription) {
//                System.out.println("~~" + getClass().getSimpleName() + ".hookOnSubscribe~~");
//                System.out.println("Subscribed is " + subscription);
//
//                request(1);
//            }
//
//            public void hookOnNext(String value) {
//                System.out.println("~~" + getClass().getSimpleName() + ".hookOnNext~~");
//                System.out.println("value is " + value);
//                request(1);
//            }
//        };
//        Flux.just("one", "two", "three").subscribe(baseSubscriber);


    }

    private void create() {

        //创建流
//        Flux.create(emitter -> {
//            for (int i = 0; i < 10; i++) emitter.next(i);
//        }).subscribe(System.out::println);

    }
}
