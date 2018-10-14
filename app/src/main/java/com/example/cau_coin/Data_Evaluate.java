package com.example.cau_coin;

import java.util.ArrayList;

public class Data_Evaluate {
    private String evaluateId;
    private String dept;
    private String grade;
    private String semester;
    private String subject;
    private String evaluate;
    private String takeYear;
    private String review;
    private String timeStamp;
    private ArrayList<String> score = new ArrayList<String>();
    private ArrayList<String> comment = new ArrayList<String>();

    public Data_Evaluate(String evaluateId, String dept, String grade, String semester, String subject, String evaluate, String takeYear, String review, String timeStamp, ArrayList<String> score, ArrayList<String> comment) {
        this.evaluateId = evaluateId;
        this.dept = dept;
        this.grade = grade;
        this.semester = semester;
        this.subject = subject;
        this.evaluate = evaluate;
        this.takeYear = takeYear;
        this.review = review;
        this.timeStamp = timeStamp;
        this.score.addAll(score);
        this.comment.addAll(comment);
    }

    public String getEvaluateId() {
        return evaluateId;
    }

    public String getDept() {
        return dept;
    }

    public String getGrade() {
        return grade;
    }

    public String getSemester() {
        return semester;
    }

    public String getSubject() {
        return subject;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public String getTakeYear() {
        return takeYear;
    }

    public String getReview() {
        return review;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getScore() {
        if (score.size() > 0) {
            int sum = 0;
            double result;
            for (int i = 0; i < score.size(); i++) {
                sum += Integer.parseInt(score.get(i));
            }

            result = (double) sum / score.size();
            result = result*1000;
            sum = (int)result;
            result = (double)sum/1000;

            return String.valueOf(result);
        } else {
            return "0";
        }
    }

    public double getDoubleScore(){
        return Double.parseDouble(getScore());
    }

    public int getCommentNum() {
        return comment.size();
    }

    public String getComment(int index) {
        return comment.get(index);
    }

}
