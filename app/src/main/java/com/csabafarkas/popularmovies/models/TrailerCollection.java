package com.csabafarkas.popularmovies.models;


import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrailerCollection implements Parcelable, PopularMoviesModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<Trailer> trailers = null;
    public final static Parcelable.Creator<TrailerCollection> CREATOR = new Creator<TrailerCollection>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TrailerCollection createFromParcel(Parcel in) {
            return new TrailerCollection(in);
        }

        public TrailerCollection[] newArray(int size) {
            return (new TrailerCollection[size]);
        }

    }
            ;

    protected TrailerCollection(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.trailers, (Trailer.class.getClassLoader()));
    }

    public TrailerCollection() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Trailer> getResults() {
        return trailers;
    }

    public void setResults(List<Trailer> results) {
        this.trailers = results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(trailers);
    }

    public int describeContents() {
        return  0;
    }
}
