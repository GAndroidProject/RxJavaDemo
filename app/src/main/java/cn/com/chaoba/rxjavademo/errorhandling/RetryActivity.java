package cn.com.chaoba.rxjavademo.errorhandling;

import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import cn.com.chaoba.rxjavademo.BaseActivity;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class RetryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLButton.setText("retry");
        mLButton.setOnClickListener(e -> {
            retryObserver().subscribe(new Subscriber<Integer>() {
                @Override
                public void onCompleted() {
                    log("retry-onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    log("retry-onError:" + e.getMessage());
                }

                @Override
                public void onNext(Integer s) {
                    log("retry-onNext:" + s);
                }
            });
        });
        mRButton.setText("retryWhen");
        mRButton.setOnClickListener(e -> {
            retryWhenObserver().subscribe(new Subscriber<Integer>() {
                @Override
                public void onCompleted() {
                    log("retryWhen-onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    log("retryWhen-onError:" + e.getMessage());
                }

                @Override
                public void onNext(Integer s) {
                    log("retryWhen-onNext:" + s);
                }
            });
        });
    }

    private Observable<Integer> retryObserver() {
        //        重试2次，1+2就是3次
        return createObserver().retry(2);
    }

    private Observable<Integer> retryWhenObserver() {
        //      遇到错误就执行retryWhen，所以最后重试的次数由 zip 中的just中的个数确定。
        return createObserver().
                retryWhen(
                new Func1<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Throwable> observable) {
                        return observable.zipWith(Observable.just(2, 3, 4, 5),
                                new Func2<Throwable, Integer, String>() {
                                    @Override
                                    public String call(Throwable throwable, Integer integer) {
                                        //                    速度太快看不出来一次一次执行
                                        //   log(throwable.getMessage() + integer);
                                        // 把"#Exception#" 字符串和 just（）依次发射出的值拼接
                                        return throwable.getMessage() + integer;
                                    }
                                })
                                .flatMap(new Func1<String, Observable<String>>() {
                                    @Override
                                    public Observable<String> call(String s) {
                                       return createObserver1()
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .doOnCompleted(new Action0() {
                                                    @Override
                                                    public void call() {
                                                        Log.i("ErrorInterceptor:","doOnCompleted");
                                                    }
                                                })
//                                               会被执行
                                                .doOnError(new Action1<Throwable>() {
                                                    @Override
                                                    public void call(Throwable throwable) {
                                                        Log.i("ErrorInterceptor:","doOnError");
                                                    }
                                                })
                                                .doOnNext(new Action1<String>() {
                                                    @Override
                                                    public void call(String s) {
                                                        log(s);
                                                        Log.i("ErrorInterceptor:","doOnNext");
                                                    }
                                                });
                                    }
                                })
                                .flatMap(new Func1<String, Observable<Long>>() {
                                    @Override
                                    public Observable<Long> call(String s) {
                                        //    把zip中拼接的异常输出。
                                        log(s);
                                        //   返回一个Observable，计时执行，
                                        return Observable.timer(1, TimeUnit.SECONDS);
                                        //  return Observable.just( 1L);
                                    }
                                });
                    }
                });
//                .onErrorResumeNext();
    }

    private Observable<Integer> createObserver() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                log("subscribe");
                for (int i = 0; i < 3; i++) {
                    if (i == 2) {
                        subscriber.onError(new Exception("#Exception#"));
                    } else {
                        subscriber.onNext(i);
                    }
                }
            }
        });
    }

    private Observable<String> createObserver1() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                log("subscribe");
                for (int i = 0; i < 3; i++) {
                    if (i == 2) {
                        subscriber.onError(new Exception("#Exception#"));
                    } else {
                        subscriber.onNext(i+"");
                    }
                }
            }
        });
    }
}


// retry  执行 onError结束， retryWhen 执行 complete结束
//
