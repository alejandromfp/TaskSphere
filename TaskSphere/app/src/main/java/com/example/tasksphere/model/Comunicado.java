package com.example.tasksphere.model;

import java.util.Date;

public class Comunicado {
        String id, user, title, description;
        Date dateCreation;


        public Comunicado() {
        }

        public Comunicado(String id, String user, String title, String description, Date dateCreation) {
            this.id = id;
            this.user = user;
            this.title = title;
            this.description = description;
            this.dateCreation = dateCreation;
        }

        public String getId() {return id; }

        public void setId(String id) {this.id = id; }

        public String getUser() {
            return user;
        }

        public void setUser(String user){
            this.user = user;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getDateCreation() {
            return dateCreation;
        }

        public void setDateCreation(Date dateCreation) {
            this.dateCreation = dateCreation;
        }
    }

