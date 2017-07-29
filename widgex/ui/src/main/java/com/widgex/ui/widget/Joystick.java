package com.widgex.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.widgex.graphics.utilities.TransformationMatrix;
import com.widgex.graphics.utilities.Vector2D;
import com.widgex.ui.R;

public class Joystick extends View {
  /**
   * central region index.
   */
  public static final int DIRECTION_CENTER = -1;
  /**
   * constant value of right direction.
   */
  public static final int DIRECTION_RIGHT = 0;
  /**
   * constant value of right forward direction.
   */
  public static final int DIRECTION_RIGHT_FORWARD = 1;
  /**
   * constant value of straight forward direction.
   */
  public static final int DIRECTION_FORWARD = 2;
  /**
   * constant value of left forward direction.
   */
  public static final int DIRECTION_LEFT_FORWARD = 3;
  /**
   * constant value of left direction.
   */
  public static final int DIRECTION_LEFT = 4;
  /**
   * constant value of left backward direction.
   */
  public static final int DIRECTION_LEFT_BACKWARD = 5;
  /**
   * constant value of straight backward direction.
   */
  public static final int DIRECTION_BACKWARD = 6;
  /**
   * constant value of right backward direction.
   */
  public static final int DIRECTION_RIGHT_BACKWARD = 7;

  /**
   * number of sectors/directions.
   */
  public static final int NUMBER_OF_SECTORS = 8;

  /**
   * default ratio between joystick side length to minimum screen dimension (min between width
   * and height), used to calculate the size of the joystick in proportion to screen size if it
   * is not exactly specified by the parent container.<br>
   * the joystick is always square so it has one dimension length.
   */
  protected static final float DEFAULT_VIEW_DIMENSION_TO_SCREEN_RATIO = 0.4f;

  /**
   * the default maximum height of the joystick component, used to keep the joystick with
   * reasonable size if the screen is large and the ratio*screen_dimension is too large.
   */
  protected static final int DEFAULT_MAX_VIEW_DIMENSION = 300; // pixels

  /**
   * the ratio between the radius of the inner circle to the radius of the joystick which is
   * half the side length.
   */
  public static final float INNER_CIRCLE_RADIUS_TO_JOYSTICK_RADIUS_DEFAULT_RATIO = 0.3f;

  /**
   * the default radius of the knob when the user is touching the joystick.
   */
  public static final float ACTIVE_KNOB_DEFAULT_RADIUS = 50.0f;

  /**
   * the default radius of the knob when the joystick is idle.
   */
  public static final float IDLE_KNOB_DEFAULT_RADIUS = 35.0f;

  /**
   * the default opacity of the joystick when the user is touching it.
   */
  public static final float ACTIVE_VIEW_DEFAULT_OPACITY = 0.7f;

  /**
   * the default opacity of the joystick when it is idle.
   */
  public static final float IDLE_VIEW_DEFAULT_OPACITY = 0.2f;

  /**
   * number of view corners.
   */
  protected static final int NUMBER_OF_CORNERS = 4;

  /**
   * the coordinate space of the joystick view is mapped to [-1, 1] on both x-axis and y-axis
   * to ease the calculations, but for the y-axis the coordinate space is inverted because
   * graphical components have the positive direction of the y-axis downward.<br>
   * this is the mapped minimum x coordinate.
   */
  protected static final float MAPPED_MIN_X_COORD = -1;
  /**
   * the mapped minimum y coordinate.
   */
  protected static final float MAPPED_MIN_Y_COORD = -1;
  /**
   * the mapped maximum x coordinate.
   */
  protected static final float MAPPED_MAX_X_COORD = 1;
  /**
   * the mapped maximum y coordinate.
   */
  protected static final float MAPPED_MAX_Y_COORD = 1;

  /**
   * the angle of each sector.
   */
  public static final float SECTOR_ANGLE = 45.0f;

  /**
   * maximum angle.
   */
  public static final float MAX_ANGLE = 360.0f;

  /**
   * the call to onKnobMove of OnKnobMoveListener is threaded, this is the default number of
   * milliseconds between each two successive calls.
   */
  public static final long ON_KNOB_MOVE_NOTIFICATION_DEFAULT_RATE = 50; // milliseconds

  protected static final String ERROR_MSG_NULL_ARGUMENT = "passed parameter is null";
  protected static final String ERROR_MSG_KNOB_RADIUS = "invalid knob radius, knob radius should " +
          "be >= 0.0 and <= joystickRadius";
  protected static final String ERROR_MSG_INNER_CIRCLE_RADIUS = "invalid inner circle radius, inner " +
          "circle radius should be >= 0.0 and <= joystickRadius";
  protected static final String ERROR_MSG_INNER_CIRCLE_RADIUS_RATIO = "invalid innerCircleRadiusToJoystickRadiusRatio, " +
          "this ratio should be >= 0.0 and <= 1.0";
  protected static final String ERROR_MSG_OPACITY_VALUE = "invalid opacity value, opacity value should " +
          "be >= 0.0 and <= 1.0";
  protected static final String ERROR_MSG_ON_KNOB_MOVE_NOTIFICATION_RATE_VALUE = "invalid onKnobMoveNotificationRate, " +
          "onKnobMoveNotificationRate value should be >= 0";
  protected static final String ERROR_MSG_DIMENSIONS_SET_BEFORE_LAYOUT = "the dimensions of inner " +
          "visual components of the joystick cannot be set before the dimensions of the joystick has " +
          "been calculated by laying out the joystick on the parent component, consider using 'isLaidOut' " +
          "method or adding an 'OnLayoutChangeListener' using 'addOnLayoutChangeListener' method";

  ///////////////////////////////////////////////////////////////////////////////////////////////

  // graphics attributes
  protected boolean isActive;
  protected float screenWidth;
  protected float screenHeight;
  protected float dimension;
  protected float joystickRadius;
  protected float joystickCenterX;
  protected float joystickCenterY;
  protected float innerCircleRadius;
  protected float innerCircleRadiusToJoystickRadiusRatio;
  protected float activeKnobRadius;
  protected float idleKnobRadius;
  protected Path sectorLeftBound[];
  protected Path sectorRightBound[];
  protected TransformationMatrix transformationMatrix;
  protected TransformationMatrix detransformationMatrix;
  protected PointF[] matrixCalibrationValuesSrc;
  protected PointF[] matrixCalibrationValuesDst;

  // drawing attributes
  protected boolean isLaidOut;
  protected Paint innerCirclePaint;
  protected Paint neutralInnerCirclePaint;
  protected Paint neutralBorderCirclePaint;
  protected Paint sectorBoundsPaint;
  protected float idleViewOpacity;
  protected float activeViewOpacity;
  protected Drawable knob;
  protected boolean innerCircleEnabled;
  protected boolean isBorderCircleEnabled;
  protected boolean sectorBoundsEnabled;

  // control attributes
  protected OnKnobMoveListener onKnobMoveListener;
  protected OnKnobMoveNotifier onKnobMoveNotifier;
  protected long onKnobMoveNotificationRate;
  protected Vector2D touchCoordinates;
  protected Vector2D mappedTouchCoordinates;
  protected float magnitude;
  protected float angle;
  protected int direction;


  ///////////////////////////////////////////////////////////////////////////////////////////////

  public Joystick(Context context) {
    super(context);
    initJoystick(context);
  }

  public Joystick(Context context, AttributeSet attrs) {
    super(context, attrs);
    initJoystick(context);
  }

  public Joystick(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initJoystick(context);
  }

  protected void initJoystick(Context context) {
    Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    screenWidth = size.x;
    screenHeight = size.y;

    isActive = false;
    innerCircleRadiusToJoystickRadiusRatio = INNER_CIRCLE_RADIUS_TO_JOYSTICK_RADIUS_DEFAULT_RATIO;
    activeKnobRadius = ACTIVE_KNOB_DEFAULT_RADIUS;
    idleKnobRadius = IDLE_KNOB_DEFAULT_RADIUS;
    sectorLeftBound = new Path[NUMBER_OF_SECTORS];
    sectorRightBound = new Path[NUMBER_OF_SECTORS];
    transformationMatrix = new TransformationMatrix();
    detransformationMatrix = new TransformationMatrix();
    matrixCalibrationValuesSrc = new PointF[NUMBER_OF_CORNERS];
    matrixCalibrationValuesDst = new PointF[NUMBER_OF_CORNERS];
    for (int i = 0; i < NUMBER_OF_CORNERS; ++i) {
      matrixCalibrationValuesSrc[i] = new PointF();
      matrixCalibrationValuesDst[i] = new PointF();
    }

    isLaidOut = false;
    innerCircleEnabled = true;
    isBorderCircleEnabled = true;
    sectorBoundsEnabled = true;
    innerCirclePaint = new Paint();
    innerCirclePaint.setStyle(Paint.Style.STROKE);
    innerCirclePaint.setStrokeWidth(5);
    innerCirclePaint.setColor(Color.WHITE);

    neutralInnerCirclePaint = new Paint();
    neutralInnerCirclePaint.setStyle(Paint.Style.STROKE);
    neutralInnerCirclePaint.setStrokeWidth(5);
    neutralInnerCirclePaint.setColor(Color.GREEN);

    neutralBorderCirclePaint = new Paint();
    neutralBorderCirclePaint.setStyle(Paint.Style.STROKE);
    neutralBorderCirclePaint.setStrokeWidth(5);
    neutralBorderCirclePaint.setColor(Color.GREEN);

    sectorBoundsPaint = new Paint();
    sectorBoundsPaint.setStyle(Paint.Style.STROKE);
    sectorBoundsPaint.setStrokeWidth(5);
    sectorBoundsPaint.setColor(Color.WHITE);

    idleViewOpacity = IDLE_VIEW_DEFAULT_OPACITY;
    activeViewOpacity = ACTIVE_VIEW_DEFAULT_OPACITY;
    this.setAlpha(idleViewOpacity);

    knob = context.getResources().getDrawable(R.drawable.shape_knob);
    super.setBackground(context.getResources().getDrawable(R.drawable.shape_joystick_background));

    onKnobMoveNotifier = new OnKnobMoveNotifier();
    onKnobMoveNotificationRate = ON_KNOB_MOVE_NOTIFICATION_DEFAULT_RATE;
    touchCoordinates = new Vector2D(joystickCenterX, joystickCenterY);
    mappedTouchCoordinates = new Vector2D(0, 0);
    magnitude = 0;
    angle = 0;
    direction = -1;
  }

  protected void initMeasures() {
    joystickRadius = dimension / 2;
    joystickCenterX = dimension / 2;
    joystickCenterY = dimension / 2;
    innerCircleRadius = innerCircleRadiusToJoystickRadiusRatio * joystickRadius;
    touchCoordinates.set(joystickCenterX, joystickCenterY);
    mappedTouchCoordinates.set(0, 0);

    if (idleKnobRadius > joystickRadius || activeKnobRadius > joystickRadius) {
      idleKnobRadius = IDLE_KNOB_DEFAULT_RADIUS;
      activeKnobRadius = ACTIVE_KNOB_DEFAULT_RADIUS;
    }

    // upper left corner
    matrixCalibrationValuesSrc[0].set(0, 0);
    matrixCalibrationValuesDst[0].set(MAPPED_MIN_X_COORD, MAPPED_MAX_Y_COORD);

    // bottom left corner
    matrixCalibrationValuesSrc[1].set(0, dimension);
    matrixCalibrationValuesDst[1].set(MAPPED_MIN_X_COORD, MAPPED_MIN_Y_COORD);

    // upper right corner
    matrixCalibrationValuesSrc[2].set(dimension, 0);
    matrixCalibrationValuesDst[2].set(MAPPED_MAX_X_COORD, MAPPED_MAX_Y_COORD);

    // bottom right corner
    matrixCalibrationValuesSrc[3].set(dimension, dimension);
    matrixCalibrationValuesDst[3].set(MAPPED_MAX_X_COORD, MAPPED_MIN_Y_COORD);

    boolean transformationMatrixSet = transformationMatrix.setPolyToPoly(
            matrixCalibrationValuesSrc, matrixCalibrationValuesDst);

    boolean detransformationMatrixSet = detransformationMatrix.setPolyToPoly(
            matrixCalibrationValuesDst, matrixCalibrationValuesSrc);

    // safety check
    if (!transformationMatrixSet || !detransformationMatrixSet)
      throw new RuntimeException("error in calibrating the transformation matrices");
  }

  protected void initSectorBounds() {
    double currentAngle = MAX_ANGLE - (SECTOR_ANGLE * 0.5f);
    PointF mappedLeftBoundStart = new PointF();
    PointF mappedLeftBoundEnd = new PointF();
    PointF mappedRightBoundStart = new PointF();
    PointF mappedRightBoundEnd = new PointF();

    for (int currentSectorIndex = 0; currentSectorIndex < NUMBER_OF_SECTORS; ++currentSectorIndex) {
      sectorLeftBound[currentSectorIndex] = new Path();
      sectorRightBound[currentSectorIndex] = new Path();

      mappedLeftBoundStart.x = (float) (Math.cos(Math.toRadians(currentAngle)) *
              innerCircleRadiusToJoystickRadiusRatio);
      mappedLeftBoundStart.y = (float) (Math.sin(Math.toRadians(currentAngle)) *
              innerCircleRadiusToJoystickRadiusRatio);
      mappedLeftBoundEnd.x = (float) Math.cos(Math.toRadians(currentAngle));
      mappedLeftBoundEnd.y = (float) Math.sin(Math.toRadians(currentAngle));

      currentAngle += SECTOR_ANGLE;
      if (currentAngle > MAX_ANGLE)
        currentAngle -= MAX_ANGLE;

      mappedRightBoundStart.x = (float) (Math.cos(Math.toRadians(currentAngle)) *
              innerCircleRadiusToJoystickRadiusRatio);
      mappedRightBoundStart.y = (float) (Math.sin(Math.toRadians(currentAngle)) *
              innerCircleRadiusToJoystickRadiusRatio);
      mappedRightBoundEnd.x = (float) Math.cos(Math.toRadians(currentAngle));
      mappedRightBoundEnd.y = (float) Math.sin(Math.toRadians(currentAngle));

      PointF realLeftBoundStart = detransformationMatrix.mapPoint(mappedLeftBoundStart);
      PointF realLeftBoundEnd = detransformationMatrix.mapPoint(mappedLeftBoundEnd);

      PointF realRightBoundStart = detransformationMatrix.mapPoint(mappedRightBoundStart);
      PointF realRightBoundEnd = detransformationMatrix.mapPoint(mappedRightBoundEnd);

      sectorLeftBound[currentSectorIndex].moveTo(realLeftBoundStart.x, realLeftBoundStart.y);
      sectorLeftBound[currentSectorIndex].lineTo(realLeftBoundEnd.x, realLeftBoundEnd.y);

      sectorRightBound[currentSectorIndex].moveTo(realRightBoundStart.x, realRightBoundStart.y);
      sectorRightBound[currentSectorIndex].lineTo(realRightBoundEnd.x, realRightBoundEnd.y);
    }
  }

  @Override
  public boolean onTouchEvent(@NonNull MotionEvent event) {
    touchCoordinates.set(event.getX(), event.getY());
    transformationMatrix.mapPoint(touchCoordinates, mappedTouchCoordinates);
    magnitude = mappedTouchCoordinates.length();
    angle = (float) mappedTouchCoordinates.angle();
    if (magnitude > innerCircleRadiusToJoystickRadiusRatio) {
      direction = ((int) (((angle + (SECTOR_ANGLE / 2)) * NUMBER_OF_SECTORS) / MAX_ANGLE)) %
              NUMBER_OF_SECTORS;
    } else direction = DIRECTION_CENTER;

    if (magnitude > 1.0) {
      mappedTouchCoordinates.set((float) Math.cos(Math.toRadians(angle)),
              (float) Math.sin(Math.toRadians(angle)));
      detransformationMatrix.mapPoint(mappedTouchCoordinates, touchCoordinates);
    }

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN: {
        if (onKnobMoveNotifier.isAlive()) {
          onKnobMoveNotifier.interrupt();
          onKnobMoveNotifier = new OnKnobMoveNotifier();
        }

        onKnobMoveNotifier.start();
        isActive = true;
        this.setAlpha(activeViewOpacity);
        break;
      }
      case MotionEvent.ACTION_UP: {
        onKnobMoveNotifier.interrupt();
        onKnobMoveNotifier = new OnKnobMoveNotifier();
        touchCoordinates.set(joystickCenterX, joystickCenterY);
        mappedTouchCoordinates.set(0, 0);
        magnitude = 0.0f;
        angle = 0.0f;
        direction = DIRECTION_CENTER;
        isActive = false;
        this.setAlpha(idleViewOpacity);
        break;
      }
    }

    this.invalidate();
    return true;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (isActive) {
      knob.setBounds((int) (touchCoordinates.x - activeKnobRadius),
              (int) (touchCoordinates.y - activeKnobRadius),
              (int) (touchCoordinates.x + activeKnobRadius),
              (int) (touchCoordinates.y + activeKnobRadius));

      if (direction == DIRECTION_CENTER) {
        if (innerCircleEnabled) {
          canvas.drawCircle(joystickCenterX,
                  joystickCenterY,
                  innerCircleRadius,
                  neutralInnerCirclePaint);
        }

        if (isBorderCircleEnabled) {
          canvas.drawCircle(joystickCenterX,
                  joystickCenterY,
                  joystickRadius - 1,
                  neutralBorderCirclePaint);
        }
      } else {
        if (innerCircleEnabled) {
          canvas.drawCircle(joystickCenterX,
                  joystickCenterY,
                  innerCircleRadius,
                  innerCirclePaint);
        }

        if (sectorBoundsEnabled) {
          canvas.drawPath(sectorLeftBound[direction], sectorBoundsPaint);
          canvas.drawPath(sectorRightBound[direction], sectorBoundsPaint);
        }
      }
    } else {
      knob.setBounds((int) (touchCoordinates.x - idleKnobRadius),
              (int) (touchCoordinates.y - idleKnobRadius),
              (int) (touchCoordinates.x + idleKnobRadius),
              (int) (touchCoordinates.y + idleKnobRadius));

      if (innerCircleEnabled) {
        canvas.drawCircle(joystickCenterX,
                joystickCenterY,
                innerCircleRadius,
                innerCirclePaint);
      }
    }

    knob.draw(canvas);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    this.isLaidOut = true;
    super.onLayout(changed, left, top, right, bottom);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    dimension = Math.min(DEFAULT_MAX_VIEW_DIMENSION,
            DEFAULT_VIEW_DIMENSION_TO_SCREEN_RATIO * Math.min(screenWidth, screenHeight));
    int measuredWidth = (int) dimension;
    int measuredHeight = (int) dimension;

    if (this.getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT)
      measuredWidth = (int) dimension;
    else if (this.getLayoutParams().width != ViewGroup.LayoutParams.MATCH_PARENT)
      measuredWidth = this.getLayoutParams().width;
    else if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED)
      measuredWidth = MeasureSpec.getSize(widthMeasureSpec);

    if (this.getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT)
      measuredHeight = (int) dimension;
    else if (this.getLayoutParams().height != ViewGroup.LayoutParams.MATCH_PARENT)
      measuredHeight = this.getLayoutParams().height;
    else if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.UNSPECIFIED)
      measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

    dimension = Math.min(measuredWidth, measuredHeight);
    setMeasuredDimension((int) dimension, (int) dimension);
    initMeasures();
    initSectorBounds();
  }

  @Override
  public void setBackground(@NonNull Drawable background) {
    if (background == null)
      throw new NullPointerException(ERROR_MSG_NULL_ARGUMENT);
    super.setBackground(background);
    this.invalidate();
  }

  public boolean isLaidOut() {
    return isLaidOut;
  }

  public boolean isSectorBoundsEnabled() {
    return sectorBoundsEnabled;
  }

  public void setSectorBoundsEnabled(boolean sectorBoundsEnabled) {
    this.sectorBoundsEnabled = sectorBoundsEnabled;
  }

  public boolean isInnerCircleEnabled() {
    return innerCircleEnabled;
  }

  public void setInnerCircleEnabled(boolean innerCircleEnabled) {
    this.innerCircleEnabled = innerCircleEnabled;
    this.invalidate();
  }

  public float getDimension() {
    return dimension;
  }

  public void setDimension(float dimension) {
    this.getLayoutParams().width = (int) dimension;
    this.getLayoutParams().height = (int) dimension;
    this.dimension = dimension;
    initMeasures();
    initSectorBounds();
  }

  public float getJoystickRadius() {
    return joystickRadius;
  }

  public Paint getInnerCirclePaint() {
    return innerCirclePaint;
  }

  public void setInnerCirclePaint(@NonNull Paint innerCirclePaint) {
    if (innerCirclePaint == null)
      throw new NullPointerException(ERROR_MSG_NULL_ARGUMENT);
    this.innerCirclePaint = innerCirclePaint;
  }

  public Paint getNeutralInnerCirclePaint() {
    return neutralInnerCirclePaint;
  }

  public void setNeutralInnerCirclePaint(@NonNull Paint neutralInnerCirclePaint) {
    if (neutralInnerCirclePaint == null)
      throw new NullPointerException(ERROR_MSG_NULL_ARGUMENT);
    this.neutralInnerCirclePaint = neutralInnerCirclePaint;
  }

  public Paint getNeutralBorderCirclePaint() {
    return neutralBorderCirclePaint;
  }

  public void setNeutralBorderCirclePaint(@NonNull Paint neutralBorderCirclePaint) {
    if (neutralBorderCirclePaint == null)
      throw new NullPointerException(ERROR_MSG_NULL_ARGUMENT);
    this.neutralBorderCirclePaint = neutralBorderCirclePaint;
  }

  public Paint getSectorBoundsPaint() {
    return sectorBoundsPaint;
  }

  public void setSectorBoundsPaint(@NonNull Paint sectorBoundsPaint) {
    if (sectorBoundsPaint == null)
      throw new NullPointerException(ERROR_MSG_NULL_ARGUMENT);
    this.sectorBoundsPaint = sectorBoundsPaint;
  }

  public float getInnerCircleRadius() {
    return innerCircleRadius;
  }

  public void setInnerCircleRadius(float innerCircleRadius) {
    if (!this.isLaidOut())
      throw new IllegalStateException(ERROR_MSG_DIMENSIONS_SET_BEFORE_LAYOUT);

    if (innerCircleRadius < 0.0 || innerCircleRadius > joystickRadius)
      throw new IllegalArgumentException(ERROR_MSG_INNER_CIRCLE_RADIUS +
              ", passed value = " + activeKnobRadius +
              ", joystickRadius = " + joystickRadius);

    this.innerCircleRadius = innerCircleRadius;
    this.innerCircleRadiusToJoystickRadiusRatio = innerCircleRadius / joystickRadius;
    this.initSectorBounds();
    this.invalidate();
  }

  public float getInnerCircleRadiusToJoystickRadiusRatio() {
    return innerCircleRadiusToJoystickRadiusRatio;
  }

  public void setInnerCircleRadiusToJoystickRadiusRatio(float innerCircleRadiusToJoystickRadiusRatio) {
    if (!this.isLaidOut())
      throw new IllegalStateException(ERROR_MSG_DIMENSIONS_SET_BEFORE_LAYOUT);

    if (innerCircleRadiusToJoystickRadiusRatio < 0.0 ||
            innerCircleRadiusToJoystickRadiusRatio > 1.0)
      throw new IllegalArgumentException(ERROR_MSG_INNER_CIRCLE_RADIUS_RATIO +
              ", passed value = " + innerCircleRadiusToJoystickRadiusRatio);

    this.innerCircleRadiusToJoystickRadiusRatio = innerCircleRadiusToJoystickRadiusRatio;
    this.innerCircleRadius = innerCircleRadiusToJoystickRadiusRatio * joystickRadius;
    this.initSectorBounds();
    this.invalidate();
  }

  public float getIdleKnobRadius() {
    return idleKnobRadius;
  }

  public void setIdleKnobRadius(float idleKnobRadius) {
    if (!this.isLaidOut())
      throw new IllegalStateException(ERROR_MSG_DIMENSIONS_SET_BEFORE_LAYOUT);

    if (idleKnobRadius < 0.0 || idleKnobRadius > joystickRadius)
      throw new IllegalArgumentException(ERROR_MSG_KNOB_RADIUS +
              ", passed value = " + idleKnobRadius +
              ", joystickRadius = " + joystickRadius);

    this.idleKnobRadius = idleKnobRadius;
    this.invalidate();
  }

  public float getActiveKnobRadius() {
    return activeKnobRadius;
  }

  public void setActiveKnobRadius(float activeKnobRadius) {
    if (!this.isLaidOut())
      throw new IllegalStateException(ERROR_MSG_DIMENSIONS_SET_BEFORE_LAYOUT);

    if (activeKnobRadius < 0.0 || activeKnobRadius > joystickRadius)
      throw new IllegalArgumentException(ERROR_MSG_KNOB_RADIUS +
              ", passed value = " + activeKnobRadius +
              ", joystickRadius = " + joystickRadius);

    this.activeKnobRadius = activeKnobRadius;
    this.invalidate();
  }

  public float getActiveViewOpacity() {
    return activeViewOpacity;
  }

  public void setActiveViewOpacity(float activeViewOpacity) {
    if (activeViewOpacity < 0.0 || activeViewOpacity > 1.0)
      throw new IllegalArgumentException(ERROR_MSG_OPACITY_VALUE +
              ", passed value = " + activeViewOpacity);

    this.activeViewOpacity = activeViewOpacity;
    this.invalidate();
  }

  public float getIdleViewOpacity() {
    return idleViewOpacity;
  }

  public void setIdleViewOpacity(float idleViewOpacity) {
    if (idleViewOpacity < 0.0 || idleViewOpacity > 1.0)
      throw new IllegalArgumentException(ERROR_MSG_OPACITY_VALUE +
              ", passed value = " + idleViewOpacity);

    this.idleViewOpacity = idleViewOpacity;
    this.invalidate();
  }

  public Drawable getKnob() {
    return knob;
  }

  public void setKnob(@NonNull Drawable knob) {
    if (knob == null)
      throw new NullPointerException(ERROR_MSG_NULL_ARGUMENT);

    this.knob = knob;
    this.invalidate();
  }

  public long getOnKnobMoveNotificationRate() {
    return onKnobMoveNotificationRate;
  }

  public void setOnKnobMoveNotificationRate(long onKnobMoveNotificationRate) {
    if (onKnobMoveNotificationRate < 0)
      throw new IllegalArgumentException(ERROR_MSG_ON_KNOB_MOVE_NOTIFICATION_RATE_VALUE +
              ", passed value = " + onKnobMoveNotificationRate);

    this.onKnobMoveNotificationRate = onKnobMoveNotificationRate;
  }

  public OnKnobMoveListener getOnKnobMoveListener() {
    return onKnobMoveListener;
  }

  public void setOnKnobMoveListener(OnKnobMoveListener onKnobMoveListener) {
    this.onKnobMoveListener = onKnobMoveListener;
  }

  public interface OnKnobMoveListener {
    void onKnobMove(float magnitude, float angle, int direction);
  }

  protected class OnKnobMoveNotifier extends Thread {
    @Override
    public void run() {
      if (onKnobMoveNotificationRate < 10) {
        while (!this.isInterrupted() && (onKnobMoveListener != null)) {
          onKnobMoveListener.onKnobMove(magnitude, angle, direction);
        }
      } else {
        while (!this.isInterrupted() && (onKnobMoveListener != null)) {
          onKnobMoveListener.onKnobMove(magnitude, angle, direction);
          try {
            Thread.sleep(onKnobMoveNotificationRate);
          } catch (InterruptedException e) {
            break;
          }
        }
      }
    }
  }
}
