package com.kikkos.spellchecker.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.kikkos.spellchecker.R;
import com.kikkos.spellchecker.data.BingFlaggedToken;
import com.kikkos.spellchecker.data.BingResponse;
import com.kikkos.spellchecker.data.BingSuggestion;
import com.kikkos.spellchecker.io.ApiClient;
import com.kikkos.spellchecker.io.SpellSuggestionApiResponse;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.searchBox)
    EditText mEditText;

    @BindView(R.id.suggestionsView)
    TextView mTextView;

    Disposable mDisposable;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // disposing the disposable so that we don't get any response after this activity is finished
        mDisposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // get retrofit client
        SpellSuggestionApiResponse apiResponse = ApiClient.getClient().create(SpellSuggestionApiResponse.class);

        mDisposable = RxTextView.textChangeEvents(mEditText)
                // will create Observable only when the emitted data are not empty
                .filter(changes -> changes != null && !changes.text().toString().isEmpty())
                // will emit an Observable if the defined time has passed and no other data has been emitted.
                .debounce(400, TimeUnit.MILLISECONDS)
                // passing the data into the api retrofit call and emitting an observable of the api response data type
                .flatMap(data -> apiResponse.getSuggestion(data.text().toString()))
                // if we receive any error then clear the suggestion textview
                .doOnError(data -> clearSuggestedView())
                // resubscribe on the apicall Observable if an error occurs. In this way we maintain the Observable stream
                .retry()
                // execute the apicall at the background thread pool
                .subscribeOn(Schedulers.io())
                // get results on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                // manage the results
                .subscribeWith(getBingResponseObserver());
    }

    // method for getting the text DisposableObserver
    private DisposableObserver<BingResponse> getBingResponseObserver() {
        return new DisposableObserver<BingResponse>() {
            @Override
            public void onNext(BingResponse data) {
                // present the suggestion to the user
                Log.v("TEST", "onNext()");
                if (data != null && data.getFlaggedTokens() != null && data.getFlaggedTokens().size() > 0) {
                    for (BingFlaggedToken t : data.getFlaggedTokens()) {
                        if (t != null && t.getSuggestions() != null && t.getSuggestions().size() > 0) {
                            for (BingSuggestion s : t.getSuggestions()) {
                                showSuggestedView(s.getSuggestion());
                            }
                        } else {
                            clearSuggestedView();
                        }
                    }
                } else {
                    clearSuggestedView();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("TEST", "onError()");
            }

            @Override
            public void onComplete() {
                Log.v("TEST", "onComplete()");
            }
        };
    }

    @OnClick(R.id.suggestionsView)
    public void swapText() {
        mEditText.setText(mTextView.getText().toString());
    }

    private void clearSuggestedView() {
        Log.v("TEST", "clearing suggestion");
        mTextView.setText("");
        if (mTextView.getVisibility() != View.GONE) {
            mTextView.setVisibility(View.GONE);
        }
    }

    private void showSuggestedView(String data) {
        Log.v("TEST", "suggesting: " + data);
        if (mTextView.getVisibility() != View.VISIBLE) {
            mTextView.setVisibility(View.VISIBLE);
        }
        mTextView.setText(data);
    }
}