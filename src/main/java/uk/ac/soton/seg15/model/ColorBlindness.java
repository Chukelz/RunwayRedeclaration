package uk.ac.soton.seg15.model;


import javafx.scene.paint.Color;

public class ColorBlindness {

  public static ColorStyleType colorType = ColorStyleType.NORMAL;

  public static Matrix protanopiaSim = new Matrix(0.0f, 1.05118294f,
      -0.05116099f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f);

  public static Matrix deuteranopiaSim = new Matrix(1.0f, 0.0f, 0.0f,
      0.9513092f, 0.0f, 0.04866992f, 0.0f, 0.0f, 1.0f);

  public static Matrix tritanopiaSim = new Matrix(1.0f, 0.0f, 0.0f, 0.0f,
      1.0f, 0.0f, -0.86744736f, 1.86727089f, 0.0f);
  public static Matrix rgb2lms = new Matrix(0.31399022f, 0.63951294f,
      0.04649755f, 0.15537241f, 0.75789446f, 0.08670142f, 0.01775239f,
      0.10944209f, 0.87256922f);

  public static Matrix lms2rgb = new Matrix(5.47221206f, -4.6419601f,
      0.16963708f, -1.1252419f, 2.29317094f, -0.1678952f, 0.02980165f,
      -0.19318073f, 1.16364789f);

  public static float clip(float x) {
    return Math.min(Math.max(x, 0), 1);
  }
  public static Vector convertRGB2LMS(Vector rgbColor) {
    return rgb2lms.rightMult(rgbColor);
  }

  public static Vector convertLMS2RGB(Vector lmsColor) {
    return lms2rgb.rightMult(lmsColor);
  }

  public static Vector simulateColorblindness(Color color, ColorStyleType type) {
    Vector vector_color = new Vector((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue());
    if (type == ColorStyleType.Protanopia) {
      Vector lms_color = convertRGB2LMS(vector_color);
      lms_color = protanopiaSim.rightMult(lms_color);
      return convertLMS2RGB(lms_color);
    } else if (type == ColorStyleType.Deuteranopia) {
      Vector lms_color = convertRGB2LMS(vector_color);
      lms_color = deuteranopiaSim.rightMult(lms_color);
      return convertLMS2RGB(lms_color);
    } else if (type == ColorStyleType.Tritanopia) {
      Vector lms_color = convertRGB2LMS(vector_color);
      lms_color = tritanopiaSim.rightMult(lms_color);
      return convertLMS2RGB(lms_color);
    }
    return vector_color;
  }

  public static Color daltonizeCorrect(Color color) {
    if (colorType != ColorStyleType.NORMAL) {
      Vector simVector = simulateColorblindness(color, colorType);
      Color newColor = new Color(clip((float)(color.getRed() * simVector.v1)), clip((float)(color.getGreen() * simVector.v2)),
          clip((float)(color.getBlue() * simVector.v3)), 1.0f);
      return newColor;
    }
    return color;
  }

  static class Vector {

    public float v1;
    public float v2;
    public float v3;

    public Vector() {
      this(0, 0, 0);
    }

    public Vector(float v1, float v2, float v3) {
      this.v1 = v1;
      this.v2 = v2;
      this.v3 = v3;
    }

    public Vector(Vector v) {
      this.v1 = v.v1;
      this.v2 = v.v2;
      this.v3 = v.v3;
    }

    public float get(int index) {
      switch (index) {
        case 1:
          return v1;
        case 2:
          return v2;
        case 3:
          return v3;
        default:
          throw new RuntimeException("Index must be 1, 2, or 3.");
      }
    }

    public void set(int index, float value) {
      switch (index) {
        case 1:
          v1 = value;
          break;
        case 2:
          v2 = value;
          break;
        case 3:
          v3 = value;
          break;
        default:
          throw new RuntimeException("Index must be 1, 2, or 3.");
      }
    }

    public Vector add(Vector that) {
      Vector out = new Vector(v1 + that.v1, v2 + that.v2, v3 + that.v3);

      return out;
    }

    public Vector sub(Vector that) {
      Vector out = new Vector(v1 - that.v1, v2 - that.v2, v3 - that.v3);

      return out;
    }

    public float dot(Vector that) {
      return v1 * that.v1 + v2 * that.v2 + v3 * that.v3;
    }

    @Override
    public String toString() {
      return "Vector(" + v1 + ", " + v2 + ", " + v3 + ")";
    }
  }

  static class Matrix {

    public float r1c1;
    public float r1c2;
    public float r1c3;
    public float r2c1;
    public float r2c2;
    public float r2c3;
    public float r3c1;
    public float r3c2;
    public float r3c3;

    public Matrix(float r1c1, float r1c2, float r1c3, float r2c1, float r2c2,
        float r2c3, float r3c1, float r3c2, float r3c3) {
      this.r1c1 = r1c1;
      this.r1c2 = r1c2;
      this.r1c3 = r1c3;
      this.r2c1 = r2c1;
      this.r2c2 = r2c2;
      this.r2c3 = r2c3;
      this.r3c1 = r3c1;
      this.r3c2 = r3c2;
      this.r3c3 = r3c3;
    }

    public Matrix(Matrix m) {
      this.r1c1 = m.r1c1;
      this.r1c2 = m.r1c2;
      this.r1c3 = m.r1c3;
      this.r2c1 = m.r2c1;
      this.r2c2 = m.r2c2;
      this.r2c3 = m.r2c3;
      this.r3c1 = m.r3c1;
      this.r3c2 = m.r3c2;
      this.r3c3 = m.r3c3;
    }

    public Vector rightMult(Vector v) {
      Vector out = new Vector();

      out.v1 = r1c1 * v.v1 + r1c2 * v.v2 + r1c3 * v.v3;
      out.v2 = r2c1 * v.v1 + r2c2 * v.v2 + r2c3 * v.v3;
      out.v3 = r3c1 * v.v1 + r3c2 * v.v2 + r3c3 * v.v3;

      return out;
    }
  }
}
