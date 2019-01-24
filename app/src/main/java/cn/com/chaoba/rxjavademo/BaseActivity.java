package cn.com.chaoba.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BaseActivity extends AppCompatActivity {

    protected Button mLButton, mRButton;
    protected TextView mResultView;
    protected String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mLButton = (Button) findViewById(R.id.left);
        mRButton = (Button) findViewById(R.id.right);
        mResultView = (TextView) findViewById(R.id.result);
        TAG = getLocalClassName();
    }

    protected void log(Object s) {
        Log.i(TAG, String.valueOf(s));
//        把从其他页面接收到的Observable发出的数据，再用just发出去，然后再切换到UI线程处理
        Observable.just(s).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> {
            mResultView.setText(mResultView.getText() + "\n" + i);
        });

    }

}
