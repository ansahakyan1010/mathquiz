package sat.math.quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReviewAnswers extends AppCompatActivity {

    private ImageView iconView;
    private TextView questionTextView;
    private RadioButton[] answerVariants = new RadioButton[4];
    private Button nextButton, prevButton;

    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_answers);

        initViews();
        setListeners();
        loadQuestions();
    }


    private void loadQuestions() {

        Intent intent = getIntent();
        List chosenAnswers = intent.getStringArrayListExtra("data");
        Test test = (Test) getIntent().getSerializableExtra("quiz");

        ArrayList<Question> questions = test.getQuestions();

        String[] opts = questions.get(currentQuestionIndex).getChoices().split(",");
        String questionText = questions.get(currentQuestionIndex).getQuestions();

        questionTextView.setText(questionText);
        // Get answer variant buttons and attach event listeners
        for (int i = 0; i < answerVariants.length; i++) {
            String id = "ans_" + (i + 1); // ans_1 ans_2 ans_3 ans_4
            int resId = getResources().getIdentifier(id, "id", getPackageName());
            answerVariants[i] = findViewById(resId);
            answerVariants[i].setEnabled(false);
        }

        for (int i = 0; i < opts.length; i++) {
            answerVariants[i].setText(opts[i]);
            String id = "ans_" + (i + 1);

            // This is the correct answer string
            String correctAnswer = questions.get(currentQuestionIndex).getAnswer().trim();

            String optionString = opts[i].trim();

            Log.d("MyTag", optionString);
            Log.d("MyTag", "CorrectAnswer: " + correctAnswer);

            if (optionString.equals(correctAnswer)) {
                String correctAnswerId = "ans_" + (i + 1);
                Log.d("MyTag", correctAnswerId);
                int correctOptionId = getResources().getIdentifier(correctAnswerId, "id", getPackageName());
                RadioButton correctOptionRadioButton = findViewById(correctOptionId);
                correctOptionRadioButton.setTextColor(Color.GREEN);
            }

            for (Object item : chosenAnswers) {
                HashMap<String, String> obj = (HashMap<String, String>) item;
                String questionIndex = obj.get("questionIndex");
                // This is the answer of user
                String answerIndex = obj.get("answerIndex");


                String selectedAns = opts[i].trim();

                if (questionIndex.equals(String.valueOf(currentQuestionIndex))) {
                    if (id.equals(answerIndex)) {
                        int resId = getResources().getIdentifier(answerIndex, "id", getPackageName());
                        RadioButton userAnsweredOption = findViewById(resId);
                        userAnsweredOption.setChecked(true);
                        userAnsweredOption.setTextColor(Color.RED);
                    }

                    // Mark with green correct answer
                    if (selectedAns.equals(correctAnswer)) {
                        String correctAnswerId = "ans_" + (i + 1);
                        int correctOptionId = getResources().getIdentifier(correctAnswerId, "id", getPackageName());
                        RadioButton correctOptionRadioButton = findViewById(correctOptionId);
                        correctOptionRadioButton.setTextColor(Color.GREEN);
                    }
                }
            }
        }

        questionTextView.setText(questionText);
    }

    private void togglePrevButton() {
        // If user passed first step enable previous button else hide
        if (currentQuestionIndex > 0) {
            prevButton.setVisibility(View.VISIBLE);
        } else {
            prevButton.setVisibility(View.INVISIBLE);
        }
    }

    private void resetPreviousVariantsUI() {
        for (int i = 0; i < answerVariants.length; i++) {
            answerVariants[i].setTextColor(Color.BLACK);
            answerVariants[i].setChecked(false);
        }
    }

    private void showMainMenuButton() {
        Test test = (Test) getIntent().getSerializableExtra("quiz");

        ArrayList<Question> questions = test.getQuestions();
        if(currentQuestionIndex + 1 == questions.size()) {
            nextButton.setText("Main Menu");
            nextButton.setOnClickListener(v -> goToHomeActivity());
        } else {
            nextButton.setText("Next");
            nextButton.setOnClickListener(v -> handleNextButton());

        }
    }

    private void handleNextButton() {
        currentQuestionIndex++;
        togglePrevButton();
        showMainMenuButton();
        resetPreviousVariantsUI();
        loadQuestions();
    }

    private void handlePrevButton() {
        currentQuestionIndex--;
        togglePrevButton();
        showMainMenuButton();
        resetPreviousVariantsUI();
        loadQuestions();
    }

    private void initViews() {
        iconView = findViewById(R.id.icon_view);
        questionTextView = findViewById(R.id.question);
        nextButton = findViewById(R.id.submit_btn);
        prevButton = findViewById(R.id.previous_btn);
    }

    private void setListeners() {
        iconView.setOnClickListener(v -> goToHomeActivity());
        nextButton.setOnClickListener(v -> handleNextButton());
        prevButton.setOnClickListener(v -> handlePrevButton());
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
