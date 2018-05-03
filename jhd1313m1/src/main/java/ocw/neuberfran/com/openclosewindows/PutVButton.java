package ocw.neuberfran.com.openclosewindows;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PutVButton {

    private String myaction;

    public PutVButton() {

    }

    public String setMyaction() {
     //   myaction.setValue(myaction);
        return myaction;
    }

    public void setMyaction(String myaction) {this.myaction = myaction; }
}

