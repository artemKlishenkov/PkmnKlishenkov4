package ru.mirea.pkmn;

import java.io.Serializable;

public class Student implements Serializable {
    public static final long serialVersionUID = 1L;
    private String firstName;
    private String surName;
    private String patronicName;
    private String group;

    public Student(String surName, String firstName, String patronicName, String group) {
        this.firstName = firstName;
        this.surName = surName;
        this.patronicName = patronicName;
        this.group = group;
    }

    public Student() {
    }

    @Override
    public String toString() {
        return "Student{" +
                "firstName='" + firstName + '\'' +
                ", surName='" + surName + '\'' +
                ", familyName='" + patronicName + '\'' +
                ", group='" + group + '\'' +
                '}';
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getPatronicName() {
        return patronicName;
    }

    public void setPatronicName(String patronicName) {
        this.patronicName = patronicName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
