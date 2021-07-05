package mo.zain.smartfarmer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Plant implements Serializable {

    @SerializedName("plant_name")
    @Expose
    private String plantName;
    @SerializedName("plant_desc")
    @Expose
    private String plantDesc;
    @SerializedName("plant_time")
    @Expose
    private String plantTime;
    @SerializedName("plant_long_time")
    @Expose
    private String plantLongTime;
    @SerializedName("plant_soil")
    @Expose
    private String plantSoil;
    @SerializedName("plant_diseases")
    @Expose
    private String plantDiseases;
    @SerializedName("plant_img")
    @Expose
    private String plantImg;

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantDesc() {
        return plantDesc;
    }

    public void setPlantDesc(String plantDesc) {
        this.plantDesc = plantDesc;
    }

    public String getPlantTime() {
        return plantTime;
    }

    public void setPlantTime(String plantTime) {
        this.plantTime = plantTime;
    }

    public String getPlantLongTime() {
        return plantLongTime;
    }

    public void setPlantLongTime(String plantLongTime) {
        this.plantLongTime = plantLongTime;
    }

    public String getPlantSoil() {
        return plantSoil;
    }

    public void setPlantSoil(String plantSoil) {
        this.plantSoil = plantSoil;
    }

    public String getPlantDiseases() {
        return plantDiseases;
    }

    public void setPlantDiseases(String plantDiseases) {
        this.plantDiseases = plantDiseases;
    }

    public String getPlantImg() {
        return plantImg;
    }

    public void setPlantImg(String plantImg) {
        this.plantImg = plantImg;
    }

}