package com.example.cau_coin;

public class RecycleItem {
    String subject;
    String grade;
    String semester;
    String takeYear;
    String evaluateId;

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

    public RecycleItem(String grade, String semester,String subject, String takeYear, String evaluateId) {
        this.subject = subject;
        this.grade = grade;
        this.semester = semester;
        this.takeYear = takeYear;
        this.evaluateId = evaluateId;
    }
}
