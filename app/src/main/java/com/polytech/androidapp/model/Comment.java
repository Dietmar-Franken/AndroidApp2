package com.polytech.androidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Cyprien on 12/04/2017.
 */

public class Comment implements Parcelable {
    private String auteur;
    private String language;
    private float rating;
    private String commentaire;
    private int time;
    private ArrayList<Aspect> aspectArrayList;

    public Comment() {
    }

    protected Comment(Parcel in) {
        auteur = in.readString();
        language = in.readString();
        rating = in.readFloat();
        commentaire = in.readString();
        time = in.readInt();
        in.readTypedList(aspectArrayList, Aspect.CREATOR);

    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public ArrayList<Aspect> getAspectArrayList() {
        return aspectArrayList;
    }

    public void setAspectArrayList(ArrayList<Aspect> aspectArrayList) {
        this.aspectArrayList = aspectArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(auteur);
        dest.writeString(language);
        dest.writeFloat(rating);
        dest.writeString(commentaire);
        dest.writeInt(time);
        dest.writeTypedList(aspectArrayList);
    }
}
