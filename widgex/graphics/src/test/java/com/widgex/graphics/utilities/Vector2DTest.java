package com.widgex.graphics.utilities;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

public class Vector2DTest {
  @Test
  public void testAngle() throws Exception {
    final float EPS = 1e-6f;
    Vector2D vector2D = mock(Vector2D.class);
    doCallRealMethod().when(vector2D).angle();
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        Vector2D mockedVector2D = (Vector2D) invocation.getMock();
        mockedVector2D.x = invocation.getArgument(0);
        mockedVector2D.y = invocation.getArgument(1);
        return mockedVector2D;
      }
    }).when(vector2D).set(anyFloat(), anyFloat());

    vector2D.set(1, 0);
    double expected = 0.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(1, 1);
    expected = 45.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(0, 1);
    expected = 90.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(-1, 1);
    expected = 135.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(-1, 0);
    expected = 180.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(-1, -1);
    expected = 225.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(0, -1);
    expected = 270.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(1, -1);
    expected = 315.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(1, (float) Math.tan(Math.toRadians(30)));
    expected = 30.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(1, (float) Math.tan(Math.toRadians(60)));
    expected = 60.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(-1, (float) Math.tan(Math.toRadians(60)));
    expected = 120.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(-1, (float) Math.tan(Math.toRadians(30)));
    expected = 150.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(-1, (float) -Math.tan(Math.toRadians(30)));
    expected = 210.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(-1, (float) -Math.tan(Math.toRadians(60)));
    expected = 240.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(1, (float) -Math.tan(Math.toRadians(60)));
    expected = 300.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);

    vector2D.set(1, (float) -Math.tan(Math.toRadians(30)));
    expected = 330.0;
    assertEquals("expected " + expected + " found " + vector2D.angle(),
            expected, vector2D.angle(), EPS);
  }
}
