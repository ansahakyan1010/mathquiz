package sat.math.quiz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {
    private String answer;
    private String choices;
    private String question;

    public Question(String answer, String choices, String question) {
        this.answer = answer;
        this.choices = choices;
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getChoices() {
        return choices;
    }

    public String getQuestions() {
        return question;
    }

}
