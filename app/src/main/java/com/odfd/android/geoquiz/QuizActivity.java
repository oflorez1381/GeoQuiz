package com.odfd.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEAT_INDEX = "cheat_index";
    private static final String KEY_VISIBILITY_BUTTONS_INDEX = "visibility_buttons_index";
    private static final String KEY_QUANTITY_CHEATS = "quantity_cheats";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int CHEAT_LIMIT = 3;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mQuantityCheatsTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private int mVisibilityButtons = View.VISIBLE;
    private int mQuantityCheats = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEAT_INDEX);
            mVisibilityButtons = savedInstanceState.getInt(KEY_VISIBILITY_BUTTONS_INDEX);
            mQuantityCheats = savedInstanceState.getInt(KEY_QUANTITY_CHEATS);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(view -> getNextQuestion());

        updateQuestion();

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(view -> checkAnswer(true));

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(view -> checkAnswer(false));

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(view -> getNextQuestion());

        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(view -> getPreviousQuestion());

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(view -> {
            mQuantityCheats++;
            boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
            Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
            startActivityForResult(intent, REQUEST_CODE_CHEAT);
        });

        setVisibilityStatusButtons(mVisibilityButtons);
        validateQuantityCheats();
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = R.string.judgment_toast;

        if (!mIsCheater) {
            messageResId = R.string.incorrect_toast;
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
            }
        }
        mVisibilityButtons = View.INVISIBLE;
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        setVisibilityStatusButtons(mVisibilityButtons);
    }

    private void getNextQuestion() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        mIsCheater = false;
        updateQuestion();
        mVisibilityButtons = View.VISIBLE;
        setVisibilityStatusButtons(mVisibilityButtons);
        validateQuantityCheats();
    }

    private void getPreviousQuestion() {
        mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
        mIsCheater = false;
        if (mCurrentIndex < 0) {
            mCurrentIndex = 0;
        }
        updateQuestion();
        mVisibilityButtons = View.VISIBLE;
        setVisibilityStatusButtons(mVisibilityButtons);
        validateQuantityCheats();
    }

    private void setVisibilityStatusButtons(int status){
        mFalseButton.setVisibility(status);
        mTrueButton.setVisibility(status);
        mCheatButton.setVisibility(status);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_CHEAT_INDEX, mIsCheater);
        savedInstanceState.putInt(KEY_VISIBILITY_BUTTONS_INDEX, mVisibilityButtons);
        savedInstanceState.putInt(KEY_QUANTITY_CHEATS, mQuantityCheats);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        validateQuantityCheats();
    }


    private void validateQuantityCheats(){

        mQuantityCheatsTextView = (TextView) findViewById(R.id.quantity_cheats_textview);
        int cheatAttempts = CHEAT_LIMIT - mQuantityCheats;
        String cheatAttemptsMessage = "Remaining Cheats : ";

        if(cheatAttempts <= 0){
            mCheatButton.setVisibility(View.INVISIBLE);
            cheatAttempts = 0;
        }

        mQuantityCheatsTextView.setText(cheatAttemptsMessage + cheatAttempts);

    }
}
