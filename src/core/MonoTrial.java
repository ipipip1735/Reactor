package core;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by Administrator on 2019/11/8 8:16.
 */
public class MonoTrial {

    public static void main(String[] args) {
        MonoTrial monoTrial = new MonoTrial();

//        monoTrial.create();
//        monoTrial.never();
    }


    private void never() {

        Mono.never()
                .doOnNext( s->{
                    System.out.println("~~doOnNext~~");
                    System.out.println(s);
                })
        .subscribe(s-> {
                    System.out.println("~~subscribe~~");
            System.out.println(s);
        });

    }

    private void create() {

        //钩子函数
        Disposable disposable = null;

        disposable = Mono.<Integer>create(monoSink -> {
            new Thread(() -> {
                try {
                    Thread.sleep(6000L);
                    monoSink.success(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            monoSink.onDispose(() -> System.out.println("~~onDispose~~"));
            monoSink.onCancel(() -> System.out.println("~~onCancel~~"));
        })
                .doOnCancel(() -> System.out.println("~~doOnCancel~~"))
                .subscribe(System.out::println);

        disposable.dispose();

    }
}
