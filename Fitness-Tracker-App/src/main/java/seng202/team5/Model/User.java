package seng202.team5.Model;

import java.util.ArrayList;
import java.util.Date;

public class User {
    private String name;
    private Date birthDate;
    private double height;
    private double weight;
    private int age;
    private int id = -1; // Need to add a handler for this
    private double bmi;
    private ArrayList<Activity> activities;
    private ArrayList<Alert> alerts;
    private ArrayList<Goal> goals = new ArrayList<>();


    /*
    Need another constructor here
     */

    /**
     *
     * @param name
     * @param age
     * @param height
     * @param weight
     * @param activities
     */
    public User(String name, int age, double height, double weight, ArrayList<Activity> activities){
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.activities = activities;
        bmi = DataAnalyser.calcBMI(height, weight);
    }


    /**
     *
     * @param id
     * @param name
     * @param age
     * @param height
     * @param weight
     * @param activities
     */
    public User(int id, String name, int age, double height, double weight, ArrayList<Activity> activities){
        this.id = id;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.activities = activities;
        bmi = DataAnalyser.calcBMI(height, weight);
    }

    /**
     *
     * @param name
     * @param age
     * @param height
     * @param weight
     */
    public User(String name, int age, double height, double weight) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        bmi = DataAnalyser.calcBMI(height, weight);
    }


    //getters
    public String getName() {return name;}
    public Date getBirthDate() {return birthDate;}
    public double getHeight() {return height;}
    public double getWeight() {return weight;}
    public ArrayList<Activity> getActivities() {return activities;}

    public double getBmi() {
        return bmi;
    }

    public int getAge() {
        return age;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    //setters
    public void setName(String tempName) {
        name = tempName;
    }
    public void setBirthDate(Date tempDate) {
        birthDate = tempDate;
    }
    public void setHeight(double tempHeight) {
        height = tempHeight;
    }
    public void setWeight(double tempWeight) {
        weight = tempWeight;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public void addGoal(Goal toAdd) {
        goals.add(toAdd);
    }

    /*
    //Adds activities from CSV to user's list of activities
    public void addActivities(String fileName) {
        ArrayList<Activity> activitiesToAdd = InputDataParser.parseCSVToActivities(String fileName);
        activities.addAll(activitiesToAdd);
    } */
}
