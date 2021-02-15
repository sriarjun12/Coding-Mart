package com.abort.employeetimesheet.Model;

public class TimeSheetModel {
    String task,date,discription,hours;

    public TimeSheetModel(String task, String date, String discription, String hours) {
        this.task = task;
        this.date = date;
        this.discription = discription;
        this.hours = hours;
    }

    public TimeSheetModel() {
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }
}
