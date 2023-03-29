package sat.math.quiz;

import java.io.Serializable;
import java.util.ArrayList;

public class Test implements Serializable {
    private String name;
    private String id;
    private ArrayList<Question> questions;

    public Test(String name, String id, ArrayList<Question> questions) {
        this.name = name;
        this.id = id;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }
}
