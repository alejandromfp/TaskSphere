package com.example.tasksphere.model;

public class Comunicado {
        String user, title, description, dateCreation;


        public Comunicado() {
            // Constructor vacío necesario para Firestore
        }

        public Comunicado(String user, String title, String description, String dateCreation) {
            this.user = user;
            this.title = title;
            this.description = description;
            this.dateCreation = dateCreation;
        }

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

        public String getDateCreation() {
            return dateCreation;
        }

        public void setDateCreation(String dateCreation) {
            this.dateCreation = dateCreation;
        }
    }

