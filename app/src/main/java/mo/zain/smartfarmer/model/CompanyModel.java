package mo.zain.smartfarmer.model;

public class CompanyModel {
    String companyname,email,imageURL,phone,id;


    public CompanyModel() {
    }

    public CompanyModel(String companyname, String email, String imageURL, String phone, String id) {
        this.companyname = companyname;
        this.email = email;
        this.imageURL = imageURL;
        this.phone = phone;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
