package com.abort.employeetimesheet.Model;

public class EmployDetail {
    String id,name,email,designation,mobile;


    public EmployDetail(String id, String name, String email, String designation, String mobile) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public EmployDetail() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
