package com.widgex.graphics.utilities;

import android.graphics.Matrix;
import android.graphics.PointF;

public class TransformationMatrix extends Matrix {
  protected float transformationInput[];
  protected float transformationOutput[];

  public TransformationMatrix() {
    super();
    transformationInput = new float[2];
    transformationOutput = new float[2];
  }

  public TransformationMatrix(Matrix src) {
    super(src);
    transformationInput = new float[2];
    transformationOutput = new float[2];
  }

  public Vector2D mapPoint(float x, float y) {
    transformationInput[0] = x;
    transformationInput[1] = y;
    this.mapPoints(transformationOutput, transformationInput);
    return new Vector2D(transformationOutput[0], transformationOutput[1]);
  }

  public Vector2D mapPoint(final PointF pointF) {
    transformationInput[0] = pointF.x;
    transformationInput[1] = pointF.y;
    this.mapPoints(transformationOutput, transformationInput);
    return new Vector2D(transformationOutput[0], transformationOutput[1]);
  }

  public void mapPoint(final PointF pointF, PointF mappedPoint) {
    transformationInput[0] = pointF.x;
    transformationInput[1] = pointF.y;
    this.mapPoints(transformationOutput, transformationInput);
    mappedPoint.set(transformationOutput[0], transformationOutput[1]);
  }

  public Vector2D mapVector(float x, float y) {
    transformationInput[0] = x;
    transformationInput[1] = y;
    this.mapVectors(transformationOutput, transformationInput);
    return new Vector2D(transformationOutput[0], transformationOutput[1]);
  }

  public Vector2D mapVector(final PointF pointF) {
    transformationInput[0] = pointF.x;
    transformationInput[1] = pointF.y;
    this.mapVectors(transformationOutput, transformationInput);
    return new Vector2D(transformationOutput[0], transformationOutput[1]);
  }

  public void mapVector(final PointF pointF, PointF mappedPoint) {
    transformationInput[0] = pointF.x;
    transformationInput[1] = pointF.y;
    this.mapVectors(transformationOutput, transformationInput);
    mappedPoint.set(transformationOutput[0], transformationOutput[1]);
  }

  public boolean setPolyToPoly(PointF[] srcPoints, PointF[] dstPoints) {
    if (srcPoints.length != dstPoints.length)
      throw new IllegalArgumentException("number of src points not equal to number of dst points");

    float src[] = new float[srcPoints.length * 2];
    float dst[] = new float[dstPoints.length * 2];
    for (int i = 0, j = 0; i < srcPoints.length; i++, j += 2) {
      src[j] = srcPoints[i].x;
      src[j + 1] = srcPoints[i].y;
    }

    for (int i = 0, j = 0; i < dstPoints.length; i++, j += 2) {
      dst[j] = dstPoints[i].x;
      dst[j + 1] = dstPoints[i].y;
    }

    return this.setPolyToPoly(src, 0, dst, 0, srcPoints.length);
  }
}
