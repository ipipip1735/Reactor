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

//        fluxTrial.create();//创建流
//        fluxTrial.operate();
//        fluxTrial.result();
//        fluxTrial.subscriber();//系统自带流
//        fluxTrial.disposable();//系统自带流
//        fluxTrial.hot(); //冷/热模式
//        fluxTrial.async();

//        fluxTrial.block();
//        fluxTrial.flatMap();
        fluxTrial.hooks();


    }

    private void operate() {
        //handle()
//        Flux.just(1,2,3,4,5)
//                .handle((integer, synchronousSink) -> {
//                    System.out.println("~~handle~~");
//                    System.out.println(integer);
//                    synchronousSink.next(integer);
//                })
//        .subscribe(i-> System.out.println("subscribe|" + i));


        //buffer()
        Flux.just(1, 2, 3, 4, 5)
                .buffer()
                .subscribe(integers -> {
                    System.out.println(integers);
                });

    }

    private void result() {

        //count()
//        Mono<Long> mono = Flux.just("foo", "bar", "foobar").count();


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

        Flux.just(1,2,3,4,5,6)
                .doOnNext(integer ->
                        System.out.println("doOnNext|" + integer))
                .subscribe(System.out::println);







    }

    private void flatMap() {


        Flux.just(1, 2, 3, 4)
                .flatMap(integer -> {
                    return Flux.just(integer, integer * 2);
                })
                .subscribe(System.out::println);


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
//
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

//        Flux.push(emitter -> {
//            for (int i = 0; i < 10; i++) emitter.next(i);
//        }).subscribe(System.out::println);


//        Flux.generate((SynchronousSink<Integer> synchronousSink) -> {
//            synchronousSink.next(1);
//        }).map(integer -> integer + 1 )
//                .subscribe(System.out::println);


        //空流
//        Flux.never()
//        .subscribe(System.out::println);


        //取值范围
//        Flux.range(1,5)
//                .subscribe(System.out::println);


        //有限制流
//        Flux.just("foo", "bar", "foobar")
//                .subscribe(System.out::println);


        //from()
//        Publisher<Integer> publisher = new Publisher<>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> s) {
//                for (int i = 0; i < 10; i++) s.onNext(i);
//            }
//        };
//        Flux.from(publisher)
//                .subscribe(System.out::println);


//        List<String> iterable = Arrays.asList("foo", "bar", "foobar");
//        Flux.fromIterable(iterable)
//                .subscribe(System.out::println);


        //与最后元素逐一合并
//        Publisher<Integer> source1 = Flux.just(1, 3, 5);
//        Publisher<Integer> source2 = Flux.just(2, 4, 6);
//        Flux.combineLatest(objects -> {
//            for (Object o : objects) System.out.println(o);
//            return Integer.valueOf(-1);
//        }, source1, source2)
//                .subscribe(System.out::println);


        //串联
//        Flux.concat(Flux.just(1, 3, 5), Flux.just(2, 4, 6))
//                .subscribe(System.out::println);


        //using
//        Callable<Integer> callable = new Callable<Integer>() {
////            @Override
////            public Integer call() throws Exception {
////                System.out.println("~~call~~");
////                return Integer.valueOf(1);
////            }
////        };
////
////        Function<Integer, Publisher<String>> sourceSupplier =
////                new Function<Integer, Publisher<String>>() {
////            @Override
////            public Publisher<String> apply(Integer integer) {
////                System.out.println("~~apply~~");
////                System.out.println(integer);
////                return Flux.just(integer + "-one");
////            }
////        };
////
////        Consumer<Integer> consumer = new Consumer<Integer>() {
////            @Override
////            public void accept(Integer integer) {
////                System.out.println("~~accept~~");
////                System.out.println(integer);
////            }
////        };
////
////        Flux.using(callable, sourceSupplier, consumer)
////                .subscribe(s -> {
////                    System.out.println("subscribe is " + s);
////                });


        //zip()
        Function<Object[], Integer> combinator = new Function<Object[], Integer>() {
            @Override
            public Integer apply(Object[] objects) {
                System.out.println("~~apply~~");
                for (Object o : objects) System.out.println(o);
                return Integer.valueOf(11);
            }
        };

        Publisher<String> publisher1 = Flux.just("11", "22", "33", "44", "55", "66");
        Publisher<String> publisher2 = Flux.just("one", "two", "three", "four", "five");
        Publisher<String> publisher3 = Flux.just("aaa", "bbb", "ccc", "ddd", "fff", "ggg");

        Flux.zip(combinator, publisher1, publisher2, publisher3)
                .subscribe(s -> {
                    System.out.println("[subscribe]" + s);
                });


    }
}
