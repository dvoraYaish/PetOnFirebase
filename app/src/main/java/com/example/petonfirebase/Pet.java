package com.example.petonfirebase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pet {
    private String id;
    private String name;
    private String species; // סוג החיה (כלב, חתול וכו')
    private String breed;   // גזע
    private double age;
    private String gender;
    private double weight;
    private String imageBase64;
    private Date birthday;
    private List<String> vaccinations; // חיסונים
    private String notes;

    public Pet() {
        this.vaccinations = new ArrayList<>();
        this.birthday = new Date();
    }

    public Pet(String name, String species, String breed, double age,
               String gender, double weight, String imageBase64, Date birthday) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.imageBase64 = imageBase64;
        this.birthday = birthday;
        this.vaccinations = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public double getAge() { return age; }
    public void setAge(double age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    public List<String> getVaccinations() { return vaccinations; }
    public void setVaccinations(List<String> vaccinations) { this.vaccinations = vaccinations; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}