# Spell-Checker App

In this app I perform a spell check of users input while typing with the Bing Spell Check API from Microsoft.
I am using the follow libraries: RXJava, Retrofit, RxBinding, Retrolambda and ButterKnife.

## Getting Started

1. Go to https://www.microsoft.com/cognitive-services/en-US/subscriptionsa nd get an Bing Spell Check API key
2. Go to app\src\main\java\com.kikkos.spellchecker\io\ApiClient.java file
3. Insert your Bing API key OKHttpClient interceptor (replace => "BING SPELL CHECK API KEY HERE")
4. compile and run ap
5. start typing into the EditText and you will get a suggestion bellow that into a TextView

Update: Implemented Android Spell checker as well
