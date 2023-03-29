package sat.math.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    private TextView scoreTextContainer;
    private Button backToMainMenu, reviewAnswers;
    private Test test;
    private String scoreStr, totalScoreStr;
    private ArrayList<String> chosenAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_screen);

        initViews();
        getIntentData();
        setListeners();
        displayScore();
    }

    private void initViews() {
        scoreTextContainer = findViewById(R.id.scoreText);
        backToMainMenu = findViewById(R.id.goToMainMenu);
        reviewAnswers = findViewById(R.id.reviewAnswers);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        test = (Test) intent.getSerializableExtra("quiz");
        scoreStr = intent.getStringExtra("score");
        totalScoreStr = intent.getStringExtra("totalScores");
        chosenAnswers = intent.getStringArrayListExtra("data");
    }

    private void goToReviewAnswers() {
        Intent intent = new Intent(this, ReviewAnswers.class);
        intent.putExtra("data", chosenAnswers);
        intent.putExtra("quiz", test);
        startActivity(intent);
        finish();
    }

    private void goToMainMenu() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void setListeners() {
        reviewAnswers.setOnClickListener(v -> goToReviewAnswers());
        backToMainMenu.setOnClickListener(v -> goToMainMenu());
    }

    private void displayScore() {
        int score = scoreStr != null ? Integer.parseInt(scoreStr) : 0;
        int totalScore = totalScoreStr != null ? Integer.parseInt(totalScoreStr) : 0;
        scoreTextContainer.setText(score + " / " + totalScore);
    }
}
