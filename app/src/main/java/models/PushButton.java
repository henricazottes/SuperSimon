package models;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Observable;

/**
 * Created by henri on 2/28/16.
 */
public class PushButton extends Observable {
    private int releasedId;
    private int pressedId;
    private ImageView imgButton;
    private static int master = -1;
    private int id;
    private static boolean locked = true;

    public PushButton(Context context, int released, int pressed, int id){
        setImgButton(new ImageView(context));
        setPressedId(pressed);
        setReleasedId(released);
        setId(id);

        getImgButton().setImageResource(this.getReleasedId());
        getImgButton().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        getImgButton().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if((getMaster() == -1 || getMaster() == getId()) && !isLocked()) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                setMaster(getId());
                                getImgButton().setImageResource(getPressedId());
                                break;
                            case MotionEvent.ACTION_UP:
                                getImgButton().setImageResource(getReleasedId());
                                setMaster(-1);
                                setChanged();
                                notifyObservers(getId());
                                break;
                        }
                        return true;
                    }else{
                        System.out.println("is locked: " + isLocked());
                        return false;
                    }
                }
            });
        }

    public static void lock(){
        setLocked(true);
    }

    public static void unlock(){
        setLocked(false);
    }

    public static boolean isLocked() {
        return locked;
    }

    public static void setLocked(boolean locked) {
        PushButton.locked = locked;
    }

    public void setPressed(){
        getImgButton().setImageResource(getPressedId());
    }

    public void setReleased(){
        getImgButton().setImageResource(getReleasedId());
    }

    public static int getMaster() {
        return master;
    }

    public static void setMaster(int master) {
        PushButton.master = master;
    }


    public int getReleasedId() {
        return releasedId;
    }

    public void setReleasedId(int releasedId) {
        this.releasedId = releasedId;
    }

    public int getPressedId() {
        return pressedId;
    }

    public void setPressedId(int pressedId) {
        this.pressedId = pressedId;
    }

    public ImageView getImgButton() {
        return imgButton;
    }

    public void setImgButton(ImageView imgButton) {
        this.imgButton = imgButton;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
