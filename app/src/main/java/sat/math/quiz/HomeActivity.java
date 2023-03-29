package sat.math.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private ArrayList<Test> tests = new ArrayList<>();
    private LinearLayout testsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        testsContainer = findViewById(R.id.tests_container);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tests");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) datas.getValue();
                    String name = (String) map.get("name");
                    String id = (String) map.get("id");
                    List<Map<String, Object>> questionsList = (List<Map<String, Object>>) map.get("questions");

                    ArrayList<Question> questions = new ArrayList<>();
                    for (Map<String, Object> questionMap : questionsList) {
                        String answer = (String) questionMap.get("answer");
                        String question = (String) questionMap.get("question");
                        String choices = (String) questionMap.get("choices");
                        Log.d("answer", answer);
                        questions.add(new Question(answer, choices, question));
                    }

                    Test test = new Test(name, id, questions);
                    tests.add(test);
                }

                Collections.sort(tests, new Comparator<Test>() {
                    @Override
                    public int compare(Test t1, Test t2) {
                        return Integer.parseInt(t1.getId()) - Integer.parseInt(t2.getId());
                    }
                });

                for (Test test : tests) {
                    addButtonForTest(test);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Button button = findViewById(R.id.daily_questions);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Test test = tests.get(3);
                launchTestActivity(test);
            }
        });
    }

    private void addButtonForTest(Test test) {
        Button practiceButton = new Button(this);
        practiceButton.setText(test.getName());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, 10);

        practiceButton.setLayoutParams(params);
        practiceButton.setBackgroundResource(R.color.red);
        practiceButton.setTextColor(getResources().getColor(R.color.white));
        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchTestActivity(test);
            }
        });
        testsContainer.addView(practiceButton);
    }

    private void launchTestActivity(Test test) {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.putExtra("data", test);
        startActivity(intent);
    }
}

