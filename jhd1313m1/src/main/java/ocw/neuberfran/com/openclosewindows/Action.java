package ocw.neuberfran.com.openclosewindows;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Action {

    private String myaction;

    public Action() {

    }

    public String getMyaction()

    {
        return myaction;
    }

    public void setMyaction(String myaction)
    {this.myaction = myaction; }
}
