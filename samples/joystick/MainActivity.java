import com.widgex.ui.widget.Joystick;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Joystick joystick = (Joystick) findViewById(R.id.joystick);
    joystick.setOnKnobMoveNotificationRate(500);
    joystick.setOnKnobMoveListener(new Joystick.OnKnobMoveListener() {
      @Override
      public void onKnobMove(float magnitude, float angle, int direction) {
        // this method will be called on another thread not the main UI thread.
        // please read the class description of Joystick.java to fully understand it.
        Log.i("joystick", "magnitude = " + magnitude +
            " ,, angle = " + angle +
            " ,, direction = " + direction);
      }
    });
  }
}
