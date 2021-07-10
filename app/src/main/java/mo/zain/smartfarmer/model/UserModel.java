package mo.zain.smartfarmer.model;

public class UserModel {
    String UserName,Email,imageURL,Mobile,id;

    public UserModel() {
    }

    public UserModel(String userName, String email, String imageURL, String mobile, String id) {
        UserName = userName;
        Email = email;
        this.imageURL = imageURL;
        Mobile = mobile;
        this.id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
