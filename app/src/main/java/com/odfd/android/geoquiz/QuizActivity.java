package com.odfd.android.geoquiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
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

    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;

        messageResId = R.string.incorrect_toast;

        if(userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        setInvisibleButtonAnswers();
    }

    private void getNextQuestion() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        updateQuestion();
        setVisibleButtonAnswers();
    }

    private void getPreviousQuestion() {
        mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
        if(mCurrentIndex < 0){
            mCurrentIndex = 0;
        }
        updateQuestion();
        setVisibleButtonAnswers();
    }

    private void setVisibleButtonAnswers(){
        mFalseButton.setVisibility(View.VISIBLE);
        mTrueButton.setVisibility(View.VISIBLE);
    }

    private void setInvisibleButtonAnswers(){
        mFalseButton.setVisibility(View.INVISIBLE);
        mTrueButton.setVisibility(View.INVISIBLE);
    }
}
