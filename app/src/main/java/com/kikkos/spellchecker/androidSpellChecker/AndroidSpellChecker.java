package com.kikkos.spellchecker.androidSpellChecker;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;

import java.util.Locale;

/**
 * Created by kikkos on 18/05/2017.
 */

public class AndroidSpellChecker implements SpellCheckerSession.SpellCheckerSessionListener {

    private AndroidSpellCheckerResponse mResponse;
    private SpellCheckerSession mSession;
    private Activity mActivity;

    public AndroidSpellChecker(Activity a, AndroidSpellCheckerResponse r) {
        this.mActivity = a;
        this.mResponse = r;
    }

    public void fetchSuggestionsFor(String input) {
        if (mActivity != null && mResponse != null && !input.isEmpty() && input.length() > 0) {
            TextServicesManager tsm = (TextServicesManager) mActivity.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
            if (mSession != null) {
                mSession = null;
            }
            this.mSession = tsm.newSpellCheckerSession(null, Locale.ENGLISH, this, true);
            if (mSession != null) {
                mSession.getSentenceSuggestions(new TextInfo[]{new TextInfo(input)}, 5);
            } else {
                Log.v("TEST", "MAKE SURE SPELL CHECKER IS ON FROM SETTINGS");
                ComponentName componentName = new ComponentName("com.android.settings",
                        "com.android.settings.Settings$SpellCheckersSettingsActivity");
                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(componentName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    mActivity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void destroy() {
        this.mResponse = null;
        this.mSession = null;
        this.mActivity = null;
    }

    @Override
    public void onGetSuggestions(SuggestionsInfo[] results) {

    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        final StringBuilder sb = new StringBuilder();

        for (SentenceSuggestionsInfo result : results) {
            int n = result.getSuggestionsCount();
            for (int i = 0; i < n; i++) {
                int m = result.getSuggestionsInfoAt(i).getSuggestionsCount();
                for (int k = 0; k < m; k++) {
                    String suggestedWord = result.getSuggestionsInfoAt(i).getSuggestionAt(k);
                    Log.v("TEST", "Android Spell Checker => suggested word: " + suggestedWord);
                    sb.append(suggestedWord)
                            .append("\n");
                    if ((result.getSuggestionsInfoAt(i).getSuggestionsAttributes() &
                            SuggestionsInfo.RESULT_ATTR_LOOKS_LIKE_TYPO) != SuggestionsInfo.RESULT_ATTR_LOOKS_LIKE_TYPO)
                        continue;
                }
                sb.append("\n");
            }
        }
        if (mResponse != null) {
            mResponse.onSpellCheckFinished(sb.toString());
        }
    }
}
