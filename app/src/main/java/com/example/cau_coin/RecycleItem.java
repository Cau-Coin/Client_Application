package com.example.cau_coin;

public class RecycleItem {
    String subject;
    String grade;
    String semester;
    String takeYear;

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

    public RecycleItem(String grade, String semester,String subject, String takeYear) {
        this.subject = subject;
        this.grade = grade;
        this.semester = semester;
        this.takeYear = takeYear;
    }
}
