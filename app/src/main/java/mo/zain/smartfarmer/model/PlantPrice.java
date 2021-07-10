package mo.zain.smartfarmer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlantPrice {

    @SerializedName("name_plant")
    @Expose
    private String namePlant;
    @SerializedName("size_plant")
    @Expose
    private String sizePlant;
    @SerializedName("category_plant")
    @Expose
    private String categoryPlant;
    @SerializedName("rate_plant")
    @Expose
    private String ratePlant;
    @SerializedName("price_plant")
    @Expose
    private String pricePlant;
    @SerializedName("desc_plant")
    @Expose
    private String descPlant;
    @SerializedName("img_plant")
    @Expose
    private String imgPlant;

    public String getNamePlant() {
        return namePlant;
    }

    public void setNamePlant(String namePlant) {
        this.namePlant = namePlant;
    }

    public String getSizePlant() {
        return sizePlant;
    }

    public void setSizePlant(String sizePlant) {
        this.sizePlant = sizePlant;
    }

    public String getCategoryPlant() {
        return categoryPlant;
    }

    public void setCategoryPlant(String categoryPlant) {
        this.categoryPlant = categoryPlant;
    }

    public String getRatePlant() {
        return ratePlant;
    }

    public void setRatePlant(String ratePlant) {
        this.ratePlant = ratePlant;
    }

    public String getPricePlant() {
        return pricePlant;
    }

    public void setPricePlant(String pricePlant) {
        this.pricePlant = pricePlant;
    }

    public String getDescPlant() {
        return descPlant;
    }

    public void setDescPlant(String descPlant) {
        this.descPlant = descPlant;
    }

    public String getImgPlant() {
        return imgPlant;
    }

    public void setImgPlant(String imgPlant) {
        this.imgPlant = imgPlant;
    }

}
