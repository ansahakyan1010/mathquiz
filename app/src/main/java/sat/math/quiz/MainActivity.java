package sat.math.quiz;

import static sat.math.quiz.Constants.TEST_DURATION_IN_MILLISECONDS;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView totalQuestionsTextView, questionTextView, counterContainer;
    private Button submitBtn, prevBtn;
    private TextView[] answerVariants = new TextView[4];

    private int score = 0;
    private int totalQuestion;
    private int currentQuestionIndex = 0;
    private String selectedAnswer = "";
    private String selectedAnswerIndex = "";
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = TEST_DURATION_IN_MILLISECONDS;
    private ArrayList<Object> choosenAnswers = new ArrayList<>();
    private CountDownTimer totalCountDownTimer;
    private ImageView iconView;
    private RadioGroup radioGroup;
    private ProgressBar progressBar;
    private Test test;
    private ArrayList<Question> questions;

    private void startTimer() {
        totalCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                intent.putExtra("score", String.valueOf(score));
                intent.putExtra("totalScores", String.valueOf(totalQuestion));
                finish();
                startActivity(intent);
            }
        }.start();
        mTimerRunning = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the countdown timer to stop it from running
        if (totalCountDownTimer != null) {
            totalCountDownTimer.cancel();
        }
    }

    private void initViews() {
        counterContainer = findViewById(R.id.counter);
        totalQuestionsTextView = findViewById(R.id.total_question);
        questionTextView = findViewById(R.id.question);
        prevBtn = findViewById(R.id.previous_btn);
        submitBtn = findViewById(R.id.submit_btn);
        iconView = findViewById(R.id.icon_view);
        radioGroup = findViewById(R.id.radio_group);
        progressBar = findViewById(R.id.progress_bar);
        answerVariants[0] = findViewById(R.id.ans_1);
        answerVariants[1] = findViewById(R.id.ans_2);
        answerVariants[2] = findViewById(R.id.ans_3);
        answerVariants[3] = findViewById(R.id.ans_4);
        for (int i = 0; i < answerVariants.length; i++) {
            String id = "ans_" + (i+1);
            answerVariants[i].setOnClickListener(v -> onAnswerVariantClicked(v, id));
        }
    }

    private void onAnswerVariantClicked(View view, String id) {
        selectedAnswer = ((Button) view).getText().toString();
        selectedAnswerIndex = id;
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToPreviousQuestion() {
        radioGroup.clearCheck();
        currentQuestionIndex--;
        choosenAnswers.remove(choosenAnswers.size() - 1);
        loadNewQuestion();
    }

    private void handleNextQuestion() {
        HashMap<String, String> testItemObj = new HashMap<>();

        radioGroup.clearCheck();
        testItemObj.put("questionIndex", String.valueOf(currentQuestionIndex));
        testItemObj.put("answerIndex", selectedAnswerIndex);
        choosenAnswers.add(testItemObj);

        String selectedAns = selectedAnswer.trim();
        String correctAns = questions.get(currentQuestionIndex).getAnswer().trim();

        if(selectedAns.equals(correctAns)){
            score++;
        }
        currentQuestionIndex++;
        loadNewQuestion();
    }

    private void handleSubmit() {
        submitBtn.setText("Submit");

        String selectedAns = selectedAnswer.trim();
        String correctAns = questions.get(currentQuestionIndex - 1).getAnswer().trim();


        if(selectedAns.equals(correctAns)){
            score++;
        }

        submitBtn.setOnClickListener(v -> goToResultsActivity());
    }


    private void goToNextQuestion() {
        submitBtn.setText("Next");
        submitBtn.setOnClickListener(v -> handleNextQuestion());
    }

    private void goToResultsActivity() {
        HashMap<String, String> testItemObj = new HashMap<>();

        // Add questionIndex and answerIndex into choosenAnswers
        testItemObj.put("questionIndex", String.valueOf(currentQuestionIndex));
        testItemObj.put("answerIndex", selectedAnswerIndex);
        choosenAnswers.add(testItemObj);

        // Send results to ResultsActivity
        Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
        intent.putExtra("score", String.valueOf(score));
        intent.putExtra("totalScores", String.valueOf(totalQuestion));
        intent.putExtra("data", choosenAnswers);
        intent.putExtra("quiz", test);
        finish();
        startActivity(intent);
    }

    private void updateProgress() {
        int step = currentQuestionIndex + 1;
        totalQuestionsTextView.setText("Total questions : " + step + " / " +totalQuestion);
        progressBar.setProgress((step * 100) / totalQuestion);
    }

    private void setListeners() {
        iconView.setOnClickListener(v -> goToHomeActivity());
        prevBtn.setOnClickListener(v -> goToPreviousQuestion());
    }

    private void togglePrevButton() {
        // If user passed first step enable previous button else hide
        if (currentQuestionIndex > 0) {
            prevBtn.setVisibility(View.VISIBLE);
        } else {
            prevBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void getDataFromIntent() {
        test = (Test) getIntent().getSerializableExtra("data");
        questions = test.getQuestions();
        totalQuestion = test.getQuestions().size();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        counterContainer.setText("Time Left: " + timeLeftFormatted);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        getDataFromIntent();
        setListeners();
        loadNewQuestion();
    }

    void loadNewQuestion(){
        // Get question and answer options
        String[] opts = questions.get(currentQuestionIndex).getChoices().split(",");
        String questionText = questions.get(currentQuestionIndex).getQuestions();

        // Draw question and answer options
        questionTextView.setText(questionText);
        for(int i = 0; i < opts.length; i++) {
            answerVariants[i].setText(opts[i]);
        }

        // Start timer if isn't
        if(!mTimerRunning){
            startTimer();
        }

        // Track for toggling prev button
        togglePrevButton();

        // Update progressbar and question counter
        updateProgress();

        // Check if the last question submit test either go to next one
        if(currentQuestionIndex + 1 == totalQuestion) {
            handleSubmit();
            return;
        }
        goToNextQuestion();
    }
}

