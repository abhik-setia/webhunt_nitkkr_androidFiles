package com.example.dell.initialstage;

import java.io.Serializable;

/**
 * Created by Dell on 25-Sep-16.
 */
public class Question implements Serializable{
    String question,question_no,answer,user_answer;

    public Question(String question_no,String question,String answer){
        this.question_no=question_no;
        this.question=question;
        this.answer=answer;
    }

    public void setUser_answer(String user_answer) {
        this.user_answer = user_answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getQuestion_no() {
        return question_no;
    }

    public String getAnswer() {
        return answer;
    }

    public String getUser_answer() {
        return user_answer;
    }
}

