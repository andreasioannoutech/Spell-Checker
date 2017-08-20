package com.kikkos.spellchecker.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.kikkos.spellchecker.R;
import com.kikkos.spellchecker.androidSpellChecker.AndroidSpellChecker;
import com.kikkos.spellchecker.androidSpellChecker.AndroidSpellCheckerResponse;
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

public class MainActivity extends AppCompatActivity implements AndroidSpellCheckerResponse {

    @BindView(R.id.searchBox)
    EditText mSearchBox;

    @BindView(R.id.suggestionsView)
    TextView mSuggestionsView;

    @BindView(R.id.searchBoxAndroid)
    EditText mSearchBoxAndroid;

    @BindView(R.id.suggestionsViewAndroid)
    TextView mSuggestionsViewAndroid;

    Disposable mDisposable;
    AndroidSpellChecker mAndroidSpellChecker;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // disposing the disposable so that we don't get any response after this activity is finished
        mDisposable.dispose();
        if (mAndroidSpellChecker != null) {
            mAndroidSpellChecker.destroy();
        }
        mAndroidSpellChecker = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // get retrofit client
        SpellSuggestionApiResponse apiResponse = ApiClient.getClient().create(SpellSuggestionApiResponse.class);

        mDisposable = RxTextView.textChangeEvents(mSearchBox)
                // will create Observable only when the emitted data are not empty
                .filter(changes -> changes != null && !changes.text().toString().isEmpty())
                // will emit an Observable if the defined time has passed and no other data has been emitted.
                .debounce(400, TimeUnit.MILLISECONDS)
                // passing the data into the api retrofit call and emitting an observable of the api response data type
                .flatMap(data -> apiResponse.getSuggestion(data.text().toString()))
                // if we receive any error then clear the suggestion textview
                .doOnError(data -> clearSuggestedView(mSuggestionsView))
                // resubscribe on the apicall Observable if an error occurs. In this way we maintain the Observable stream
                .retry()
                // execute the apicall at the background thread pool
                .subscribeOn(Schedulers.io())
                // get results on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                // manage the results
                .subscribeWith(getBingResponseObserver());


        // creating the spell checker var
        mAndroidSpellChecker = new AndroidSpellChecker(this, this);
        // adding listener to our search box
        mSearchBoxAndroid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mAndroidSpellChecker != null) {
                    // input text in spell checker
                    mAndroidSpellChecker.fetchSuggestionsFor(s.toString());
                }
            }
        });
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
                                showSuggestedView(mSuggestionsView, s.getSuggestion());
                            }
                        } else {
                            clearSuggestedView(mSuggestionsView);
                        }
                    }
                } else {
                    clearSuggestedView(mSuggestionsView);
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

    @Override
    public void onSpellCheckFinished(String correction) {
        if (mSuggestionsViewAndroid != null) {
            showSuggestedView(mSuggestionsViewAndroid, correction);
        }
    }

    @OnClick(R.id.suggestionsView)
    public void swapText() {
        mSearchBox.setText(mSuggestionsView.getText().toString());
    }

    @OnClick(R.id.suggestionsViewAndroid)
    public void swapTextAndroid() {
        mSearchBoxAndroid.setText(mSuggestionsViewAndroid.getText().toString());
    }

    private void clearSuggestedView(TextView view) {
        // remove text from suggestedView and making it invisible
        Log.v("TEST", "clearing suggestion");
        view.setText("");
    }

    private void showSuggestedView(TextView view, String data) {
        // making suggestedView visilbe and setting the data as a Text
        Log.v("TEST", "suggesting: " + data);
        view.setText(data);
    }
}