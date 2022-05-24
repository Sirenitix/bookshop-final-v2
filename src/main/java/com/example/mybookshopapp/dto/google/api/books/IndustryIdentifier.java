package com.example.mybookshopapp.dto.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
public class IndustryIdentifier{
    @JsonProperty("type")
    public String getType() { 
		 return this.type; } 
    public void setType(String type) { 
		 this.type = type; } 
    String type;
    @JsonProperty("identifier") 
    public String getIdentifier() { 
		 return this.identifier; } 
    public void setIdentifier(String identifier) { 
		 this.identifier = identifier; } 
    String identifier;
}

