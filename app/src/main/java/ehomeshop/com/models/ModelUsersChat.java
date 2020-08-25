package ehomeshop.com.models;

public class ModelUsersChat {

    //use same name as in firebase database
    String name, email, profileImage, uid;

    public ModelUsersChat(){
    }

    public ModelUsersChat(String name, String email, String profileImage, String uid) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
