package com.example.cau_coin;

public class RecycleItem {
    String subject;
    String dept;
    String grade;
    String semester;
    String takeYear;
    String evaluateId;
    String score;
    String evaluate;
    String review;
    int lookup; // 1이면 열람함, 2면 본인 글

    public String getSubject() {
        return subject;
    }

    public String getGrade() {
        return grade;
    }

    public String getSemester() {
        return semester;
    }

    public String getTakeYear() {
        return takeYear;
    }

    public String getEvaluateId() {
        return evaluateId;
    }

    public int getLookup() {
        return lookup;
    }

    public String getDept() {
        return dept;
    }

    public void setLookup(int lookup) {
        this.lookup = lookup;
    }

    public String getScore() {
        return score;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public String getReview() {
        return review;
    }

    public RecycleItem(String dept, String grade, String semester, String subject, String takeYear, String evaluateId, String score, String evaluate, String review, int lookup) {
        this.dept = dept;
        this.subject = subject;
        this.grade = grade;
        this.semester = semester;
        this.takeYear = takeYear;
        this.evaluateId = evaluateId;
        this.score = score;
        this.evaluate = evaluate;
        this.review = review;
        this.lookup=lookup;
    }
}
