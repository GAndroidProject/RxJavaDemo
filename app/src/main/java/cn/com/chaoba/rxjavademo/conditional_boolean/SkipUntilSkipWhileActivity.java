package cn.com.chaoba.rxjavademo.conditional_boolean;

import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import cn.com.chaoba.rxjavademo.BaseActivity;
import rx.Observable;
import rx.functions.Action1;

public class SkipUntilSkipWhileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLButton.setText("skipUntil");
        mLButton.setOnClickListener(e -> {
            skipUntilObserver().compose(bindToLifecycle()).subscribe(new Action1<Long>() {
                @Override
                public void call(Long i) {
                    log("skipUntil:" + i);
                }
            });
        });
        mRButton.setText("skipWhile");
        mRButton.setOnClickListener(e -> {
            skipWhileObserver().compose(bindToLifecycle()).subscribe(new Action1<Long>() {
                @Override
                public void call(Long i) {
                    log("skipWhile:" + i);
                }
            });
        });
    }

    private Observable<Long> skipUntilObserver() {
        return Observable.interval(1, TimeUnit.SECONDS)
                .skipUntil(Observable.timer(3, TimeUnit.SECONDS));
    }

    private Observable<Long> skipWhileObserver() {
        return Observable.interval(1, TimeUnit.SECONDS)
                .skipWhile(aLong -> aLong < 5);
    }
}


