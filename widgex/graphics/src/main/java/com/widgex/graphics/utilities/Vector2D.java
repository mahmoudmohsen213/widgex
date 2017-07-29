package com.widgex.graphics.utilities;

import android.graphics.Point;
import android.graphics.PointF;

public class Vector2D extends PointF {
  public Vector2D() {
    super();
  }

  public Vector2D(float x, float y) {
    super(x, y);
  }

  public Vector2D(Point p) {
    super(p);
  }

  public Vector2D(PointF p) {
    super(p.x, p.y);
  }

  public Vector2D(Vector2D v) {
    super(v.x, v.y);
  }

  public double angle() {
    double angle;
    if (this.x == 0 && this.y == 0)
      angle = 0;
    else if (this.x == 0 && this.y > 0)
      angle = 90;
    else if (this.x == 0 && this.y < 0)
      angle = 270;
    else {
      angle = Math.toDegrees(Math.atan(Math.abs((double) this.y) / Math.abs((double) this.x)));
      if (this.x < 0 && this.y >= 0)
        angle = 180 - angle;
      else if (this.x < 0 && this.y < 0)
        angle = 180 + angle;
      else if (this.x > 0 && this.y < 0)
        angle = 360 - angle;
    }
    return angle;
  }
}
