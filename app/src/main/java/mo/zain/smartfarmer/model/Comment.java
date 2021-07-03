package mo.zain.smartfarmer.model;

public class Comment {
    String UserUid,UserEmail,UserName,myDp,commenttxt;

    public Comment() {
    }

    public Comment(String userUid, String userEmail, String userName, String myDp, String commenttxt) {
        UserUid = userUid;
        UserEmail = userEmail;
        UserName = userName;
        this.myDp = myDp;
        this.commenttxt = commenttxt;
    }

    public String getUserUid() {
        return UserUid;
    }

    public void setUserUid(String userUid) {
        UserUid = userUid;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getMyDp() {
        return myDp;
    }

    public void setMyDp(String myDp) {
        this.myDp = myDp;
    }

    public String getCommenttxt() {
        return commenttxt;
    }

    public void setCommenttxt(String commenttxt) {
        this.commenttxt = commenttxt;
    }
}
