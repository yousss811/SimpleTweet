package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    private final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LEN = 280;

    TextView tvCharCount;
    EditText etCompose;
    Button btnTweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        tvCharCount = findViewById(R.id.tvCharCount);

        etCompose = findViewById(R.id.etCompose);
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvCharCount.setText(String.format("0/%d", etCompose.getText().toString().length(), MAX_TWEET_LEN));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etCompose.getText().toString().length() > MAX_TWEET_LEN) {
                    tvCharCount.setTextColor(Color.rgb(255,0,0));
                }
                else{
                    tvCharCount.setTextColor(Color.rgb(0,0,0));
                }
                tvCharCount.setText(String.format("%d/%d", etCompose.getText().toString().length(), MAX_TWEET_LEN));

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnTweet = findViewById(R.id.btnTweet);
        btnTweet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty!", Toast.LENGTH_LONG);
                }
                else if(tweetContent.length() > MAX_TWEET_LEN){
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long!", Toast.LENGTH_LONG);
                }
                else{
                    client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess to publish tweet");
                            try {
                                Tweet tweet = Tweet.fromJson(json.jsonObject);
                                Intent intent = new Intent();
                                intent.putExtra("Tweet", Parcels.wrap(tweet));
                                setResult(RESULT_OK, intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure to publish tweet", throwable);
                        }
                    });
                }
            }
        });
    }
}