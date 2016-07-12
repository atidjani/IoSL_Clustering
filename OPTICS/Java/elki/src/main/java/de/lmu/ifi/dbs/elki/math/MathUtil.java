package de.lmu.ifi.dbs.elki.math;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2015
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.math.BigInteger;
import java.util.Random;

/**
 * A collection of math related utility functions.
 *
 * @author Arthur Zimek
 * @author Erich Schubert
 * @since 0.2
 *
 * @apiviz.landmark
 */
public final class MathUtil {
  /**
   * Two times Pi.
   */
  public static final double TWOPI = 2. * Math.PI;

  /**
   * Half the value of Pi.
   */
  public static final double HALFPI = .5 * Math.PI;

  /**
   * One quarter of Pi.
   */
  public static final double QUARTERPI = .25 * Math.PI;

  /**
   * 1.5 times Pi.
   */
  public static final double ONEHALFPI = 1.5 * Math.PI;

  /**
   * Pi squared
   */
  public static final double PISQUARE = Math.PI * Math.PI;

  /**
   * Square root of Pi.
   */
  public static final double SQRTPI = Math.sqrt(Math.PI);

  /**
   * Square root of two times Pi.
   */
  public static final double SQRTTWOPI = Math.sqrt(TWOPI);

  /**
   * Constant for sqrt(pi/2)
   */
  public static final double SQRTHALFPI = Math.sqrt(HALFPI);

  /**
   * Square root of 2.
   */
  public static final double SQRT2 = Math.sqrt(2.);

  /**
   * Square root of 5.
   */
  public static final double SQRT5 = Math.sqrt(5.);

  /**
   * Square root of 0.5 == 1 / sqrt(2).
   */
  public static final double SQRTHALF = Math.sqrt(.5);

  /**
   * Precomputed value of 1 / sqrt(pi).
   */
  public static final double ONE_BY_SQRTPI = 1. / SQRTPI;

  /**
   * Precomputed value of 1 / sqrt(2 * pi).
   */
  public static final double ONE_BY_SQRTTWOPI = 1. / SQRTTWOPI;

  /**
   * Precomputed value of log(1 / sqrt(2 * pi)) = -.5 * log(2*pi).
   */
  public static final double LOG_ONE_BY_SQRTTWOPI = -.5 * Math.log(TWOPI);

  /**
   * 1. / log(2)
   */
  public static final double ONE_BY_LOG2 = 1. / Math.log(2.);

  /**
   * One third.
   */
  public static final double ONE_THIRD = 1. / 3.;

  /**
   * Logarithm of 2 to the basis e, for logarithm conversion.
   */
  public static final double LOG2 = Math.log(2.);

  /**
   * Logarithm of 3 to the basis e, for logarithm conversion.
   */
  public static final double LOG3 = Math.log(3.);

  /**
   * Natural logarithm of 10.
   */
  public static final double LOG10 = Math.log(10.);

  /**
   * Math.log(Math.PI).
   */
  public static final double LOGPI = Math.log(Math.PI);

  /**
   * Math.log(Math.PI) / 2.
   */
  public static final double LOGPIHALF = LOGPI / 2.;

  /**
   * Math.log(2*Math.PI).
   */
  public static final double LOGTWOPI = Math.log(TWOPI);

  /**
   * Math.log(Math.sqrt(2*Math.PI)).
   */
  public static final double LOGSQRTTWOPI = Math.log(SQRTTWOPI);

  /**
   * Log(log(2))
   */
  public static final double LOGLOG2 = Math.log(LOG2);

  /**
   * Constant for degrees to radians.
   */
  public static final double DEG2RAD = Math.PI / 180.0;

  /**
   * Constant for radians to degrees.
   */
  public static final double RAD2DEG = 180 / Math.PI;

  /**
   * Fake constructor for static class.
   */
  private MathUtil() {
    // Static methods only - do not instantiate!
  }

  /**
   * Compute the base 2 logarithm.
   *
   * @param x X
   * @return Logarithm base 2.
   */
  public static double log2(double x) {
    return Math.log(x) * ONE_BY_LOG2;
  }

  /**
   * Computes the square root of the sum of the squared arguments without under
   * or overflow.
   *
   * Note: this code is <em>not</em> redundant to {@link Math#hypot}, since the
   * latter is significantly slower (but maybe has a higher precision).
   *
   * @param a first cathetus
   * @param b second cathetus
   * @return {@code sqrt(a<sup>2</sup> + b<sup>2</sup>)}
   */
  public static double fastHypot(double a, double b) {
    if(a < 0) {
      a = -a;
    }
    if(b < 0) {
      b = -b;
    }
    if(a > b) {
      final double r = b / a;
      return a * Math.sqrt(1 + r * r);
    }
    else if(b != 0) {
      final double r = a / b;
      return b * Math.sqrt(1 + r * r);
    }
    else {
      return 0.0;
    }
  }

  /**
   * Computes the square root of the sum of the squared arguments without under
   * or overflow.
   *
   * Note: this code is <em>not</em> redundant to {@link Math#hypot}, since the
   * latter is significantly slower (but has a higher precision).
   *
   * @param a first cathetus
   * @param b second cathetus
   * @param c second cathetus
   * @return {@code sqrt(a<sup>2</sup> + b<sup>2</sup> + c<sup>2</sup>)}
   */
  public static double fastHypot3(double a, double b, double c) {
    if(a < 0) {
      a = -a;
    }
    if(b < 0) {
      b = -b;
    }
    if(c < 0) {
      c = -c;
    }
    double m = (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
    if(m <= 0) {
      return 0.0;
    }
    a = a / m;
    b = b / m;
    c = c / m;
    return m * Math.sqrt(a * a + b * b + c * c);
  }

  /**
   * Compute the Factorial of n, often written as <code>c!</code> in
   * mathematics.
   * <p>
   * Use this method if for large values of <code>n</code>.
   * </p>
   *
   * @param n Note: n &gt;= 0. This {@link BigInteger} <code>n</code> will be 0
   *        after this method finishes.
   * @return n * (n-1) * (n-2) * ... * 1
   */
  public static BigInteger factorial(BigInteger n) {
    BigInteger nFac = BigInteger.ONE;
    while(n.compareTo(BigInteger.ONE) > 0) {
      nFac = nFac.multiply(n);
      n = n.subtract(BigInteger.ONE);
    }
    return nFac;
  }

  /**
   * Compute the Factorial of n, often written as <code>c!</code> in
   * mathematics.
   *
   * @param n Note: n &gt;= 0
   * @return n * (n-1) * (n-2) * ... * 1
   */
  public static long factorial(int n) {
    long nFac = 1;
    for(long i = n; i > 0; i--) {
      nFac *= i;
    }
    return nFac;
  }

  /**
   * <p>
   * Binomial coefficient, also known as "n choose k".
   * </p>
   *
   * @param n Total number of samples. n &gt; 0
   * @param k Number of elements to choose. <code>n &gt;= k</code>,
   *        <code>k &gt;= 0</code>
   * @return n! / (k! * (n-k)!)
   */
  public static long binomialCoefficient(long n, long k) {
    final long m = Math.max(k, n - k);
    double temp = 1;
    for(long i = n, j = 1; i > m; i--, j++) {
      temp = temp * i / j;
    }
    return (long) temp;
  }

  /**
   * Compute the Factorial of n, often written as <code>c!</code> in
   * mathematics.
   *
   * @param n Note: n &gt;= 0
   * @return n * (n-1) * (n-2) * ... * 1
   */
  public static double approximateFactorial(int n) {
    double nFac = 1.0;
    for(int i = n; i > 0; i--) {
      nFac *= i;
    }
    return nFac;
  }

  /**
   * Binomial coefficent, also known as "n choose k").
   *
   * @param n Total number of samples. n &gt; 0
   * @param k Number of elements to choose. <code>n &gt;= k</code>,
   *        <code>k &gt;= 0</code>
   * @return n! / (k! * (n-k)!)
   */
  public static double approximateBinomialCoefficient(int n, int k) {
    final int m = max(k, n - k);
    long temp = 1;
    for(int i = n, j = 1; i > m; i--, j++) {
      temp = temp * i / j;
    }
    return temp;
  }

  /**
   * Compute the sum of the i first integers.
   *
   * @param i maximum summand
   * @return Sum
   */
  public static long sumFirstIntegers(final long i) {
    return ((i - 1L) * i) >> 1;
  }

  /**
   * Produce an array of random numbers in [0:1].
   *
   * @param len Length
   * @return Array
   */
  public static double[] randomDoubleArray(int len) {
    return randomDoubleArray(len, new Random());
  }

  /**
   * Produce an array of random numbers in [0:1].
   *
   * @param len Length
   * @param r Random generator
   * @return Array
   */
  public static double[] randomDoubleArray(int len, Random r) {
    final double[] ret = new double[len];
    for(int i = 0; i < len; i++) {
      ret[i] = r.nextDouble();
    }
    return ret;
  }

  /**
   * Convert Degree to Radians.
   *
   * This is essentially the same as {@link Math#toRadians}, but we keep it for
   * now, it might be marginally faster, but certainly not slower.
   *
   * @param deg Degree value
   * @return Radian value
   */
  public static double deg2rad(double deg) {
    return deg * DEG2RAD;
  }

  /**
   * Radians to Degree.
   *
   * This is essentially the same as {@link Math#toRadians}, but we keep it for
   * now, it might be marginally faster, but certainly not slower.
   *
   * @param rad Radians value
   * @return Degree value
   */
  public static double rad2deg(double rad) {
    return rad * RAD2DEG;
  }

  /**
   * Normalize an angle to [0:2pi[
   *
   * @param x Input angle
   * @return Normalized angle
   */
  public static double normAngle(double x) {
    x %= TWOPI;
    return (x > 0) ? x : x + TWOPI;
  }

  /**
   * <b>Fast</b> way of computing cos(x) from x and sin(x).
   *
   * @param angle Input angle x
   * @param sin Sine of x.
   * @return Cosine of x
   */
  public static double sinToCos(double angle, double sin) {
    // Numerics of the formula below aren't too good.
    if((-1e-5 < sin && sin < 1e-5) || sin > 0.99999 || sin < -0.99999) {
      return Math.cos(angle);
    }
    angle = normAngle(angle);
    final double s = Math.sqrt(1 - sin * sin);
    return (angle < HALFPI || angle > ONEHALFPI) ? s : -s;
  }

  /**
   * <b>Fast</b> way of computing sin(x) from x and cos(x).
   *
   * @param angle Input angle x
   * @param cos Cosine of x.
   * @return Sine of x
   */
  public static double cosToSin(double angle, double cos) {
    // Numerics of the formula below aren't too good.
    if((-1e-5 < cos && cos < 1e-5) || cos > 0.99999 || cos < -0.99999) {
      return Math.sin(angle);
    }
    angle = normAngle(angle);
    final double s = Math.sqrt(1 - cos * cos);
    return (angle < Math.PI) ? s : -s;
  }

  /**
   * Find the next power of 2.
   *
   * Classic bit operation, for signed 32-bit. Valid for positive integers only
   * (0 otherwise).
   *
   * @param x original integer
   * @return Next power of 2
   */
  public static int nextPow2Int(int x) {
    --x;
    x |= x >>> 1;
    x |= x >>> 2;
    x |= x >>> 4;
    x |= x >>> 8;
    x |= x >>> 16;
    return ++x;
  }

  /**
   * Find the next power of 2.
   *
   * Classic bit operation, for signed 64-bit. Valid for positive integers only
   * (0 otherwise).
   *
   * @param x original long integer
   * @return Next power of 2
   */
  public static long nextPow2Long(long x) {
    --x;
    x |= x >>> 1;
    x |= x >>> 2;
    x |= x >>> 4;
    x |= x >>> 16;
    x |= x >>> 32;
    return ++x;
  }

  /**
   * Find the next larger number with all ones.
   *
   * Classic bit operation, for signed 32-bit. Valid for positive integers only
   * (-1 otherwise).
   *
   * @param x original integer
   * @return Next number with all bits set
   */
  public static int nextAllOnesInt(int x) {
    x |= x >>> 1;
    x |= x >>> 2;
    x |= x >>> 4;
    x |= x >>> 8;
    x |= x >>> 16;
    return x;
  }

  /**
   * Find the next larger number with all ones.
   *
   * Classic bit operation, for signed 64-bit. Valid for positive integers only
   * (-1 otherwise).
   *
   * @param x original long integer
   * @return Next number with all bits set
   */
  public static long nextAllOnesLong(long x) {
    x |= x >>> 1;
    x |= x >>> 2;
    x |= x >>> 4;
    x |= x >>> 16;
    x |= x >>> 32;
    return x;
  }

  /**
   * Return the largest double that rounds down to this float.
   *
   * Note: Probably not always correct - subnormal values are quite tricky. So
   * some of the bounds might not be tight.
   *
   * @param f Float value
   * @return Double value
   */
  public static double floatToDoubleUpper(float f) {
    if(Float.isNaN(f)) {
      return Double.NaN;
    }
    if(Float.isInfinite(f)) {
      if(f > 0) {
        return Double.POSITIVE_INFINITY;
      }
      else {
        return Double.longBitsToDouble(0xc7efffffffffffffL);
      }
    }
    long bits = Double.doubleToRawLongBits((double) f);
    if((bits & 0x8000000000000000L) == 0) { // Positive
      if(bits == 0L) {
        return Double.longBitsToDouble(0x3690000000000000L);
      }
      if(f == Float.MIN_VALUE) {
        // bits += 0x7_ffff_ffff_ffffl;
        return Double.longBitsToDouble(0x36a7ffffffffffffL);
      }
      if(Float.MIN_NORMAL > f && f >= Double.MIN_NORMAL) {
        // The most tricky case:
        // a denormalized float, but a normalized double
        final long bits2 = Double.doubleToRawLongBits((double) Math.nextUp(f));
        bits = (bits >>> 1) + (bits2 >>> 1) - 1L;
      }
      else {
        bits += 0xfffffffL; // 28 extra bits
      }
      return Double.longBitsToDouble(bits);
    }
    else {
      if(bits == 0x8000000000000000L) {
        return -0.0d;
      }
      if(f == -Float.MIN_VALUE) {
        // bits -= 0xf_ffff_ffff_ffffl;
        return Double.longBitsToDouble(0xb690000000000001L);
      }
      if(-Float.MIN_NORMAL < f && f <= -Double.MIN_NORMAL) {
        // The most tricky case:
        // a denormalized float, but a normalized double
        final long bits2 = Double.doubleToRawLongBits((double) Math.nextUp(f));
        bits = (bits >>> 1) + (bits2 >>> 1) + 1L;
      }
      else {
        bits -= 0xfffffffL; // 28 extra bits
      }
      return Double.longBitsToDouble(bits);
    }
  }

  /**
   * Return the largest double that rounds up to this float.
   *
   * Note: Probably not always correct - subnormal values are quite tricky. So
   * some of the bounds might not be tight.
   *
   * @param f Float value
   * @return Double value
   */
  public static double floatToDoubleLower(float f) {
    if(Float.isNaN(f)) {
      return Double.NaN;
    }
    if(Float.isInfinite(f)) {
      if(f < 0) {
        return Double.NEGATIVE_INFINITY;
      }
      else {
        return Double.longBitsToDouble(0x47efffffffffffffL);
      }
    }
    long bits = Double.doubleToRawLongBits((double) f);
    if((bits & 0x8000000000000000L) == 0) { // Positive
      if(bits == 0L) {
        return +0.0d;
      }
      if(f == Float.MIN_VALUE) {
        // bits -= 0xf_ffff_ffff_ffffl;
        return Double.longBitsToDouble(0x3690000000000001L);
      }
      if(Float.MIN_NORMAL > f /* && f >= Double.MIN_NORMAL */) {
        // The most tricky case:
        // a denormalized float, but a normalized double
        final long bits2 = Double.doubleToRawLongBits((double) -Math.nextUp(-f));
        bits = (bits >>> 1) + (bits2 >>> 1) + 1L; // + (0xfff_ffffL << 18);
      }
      else {
        bits -= 0xfffffffL; // 28 extra bits
      }
      return Double.longBitsToDouble(bits);
    }
    else {
      if(bits == 0x8000000000000000L) {
        return Double.longBitsToDouble(0xb690000000000000L);
      }
      if(f == -Float.MIN_VALUE) {
        // bits += 0x7_ffff_ffff_ffffl;
        return Double.longBitsToDouble(0xb6a7ffffffffffffL);
      }
      if(-Float.MIN_NORMAL < f /* && f <= -Double.MIN_NORMAL */) {
        // The most tricky case:
        // a denormalized float, but a normalized double
        final long bits2 = Double.doubleToRawLongBits((double) -Math.nextUp(-f));
        bits = (bits >>> 1) + (bits2 >>> 1) - 1L;
      }
      else {
        bits += 0xfffffffL; // 28 extra bits
      }
      return Double.longBitsToDouble(bits);
    }
  }

  /**
   * More stable than {@code Math.log(1 - Math.exp(x))}
   *
   * @param x Value
   * @return log(1-exp(x))
   */
  public static double log1mexp(double x) {
    return (x > -LOG2) ? Math.log(-Math.expm1(x)) : Math.log1p(-Math.exp(x));
  }

  /**
   * Fast loop for computing {@code Math.pow(x, p)} for p >= 0 integer.
   *
   * @param x Base
   * @param p Exponent
   * @return {@code Math.pow(x, p)}
   */
  public static double powi(double x, int p) {
    if(p <= 2) {
      return Math.pow(x, p);
    }
    double tmp = x, ret = (p & 1) == 1 ? x : 1.;
    while(true) {
      tmp *= tmp;
      p >>= 1;
      if(p == 1) {
        return ret * tmp;
      }
      if((p & 1) != 0) {
        ret *= tmp;
      }
    }
  }

  /**
   * Fast loop for computing {@code Math.pow(x, p)} for p >= 0 integer and x
   * integer.
   *
   * @param x Base
   * @param p Exponent
   * @return {@code Math.pow(x, p)}
   */
  public static int ipowi(int x, int p) {
    if(p <= 2) {
      return (int) Math.pow(x, p);
    }
    int tmp = x, ret = (p & 1) == 1 ? x : 1;
    while(true) {
      tmp *= tmp;
      p >>= 1;
      if(p == 1) {
        return ret * tmp;
      }
      if((p & 1) != 0) {
        ret *= tmp;
      }
    }
  }

  /**
   * Empty integer array.
   */
  public static final int[] EMPTY_INTS = new int[0];

  /**
   * Generate an array of integers.
   *
   * @param start First integer
   * @param end Last integer (exclusive!)
   * @return Array of integers of length end-start
   */
  public static int[] sequence(int start, int end) {
    if(start >= end) {
      return EMPTY_INTS;
    }
    int[] ret = new int[end - start];
    for(int j = 0; start < end; start++, j++) {
      ret[j] = start;
    }
    return ret;
  }

  /**
   * Binary max, <i>without</i> handling of special values.
   *
   * Because of the lack of special case handling, this is faster than
   * {@link Math#max}. But usually, it should be written inline as
   * {@code (a >= b) ? a : b}
   *
   * The result is asymmetric in case of {@code Double.NaN}:<br />
   * {@code MathUtil.max(Double.NaN, 1.)} is 1, but <br />
   * {@code MathUtil.max(1., Double.NaN)} is {@code Double.NaN}.
   *
   * @param a First value
   * @param b Second value
   * @return Maximum
   */
  public static double max(double a, double b) {
    return a >= b ? a : b;
  }

  /**
   * Ternary max, <i>without</i> handling of special values.
   *
   * Because of the lack of special case handling, this is faster than
   * {@link Math#max}. But usually, it should be written inline.
   *
   * @param a First value
   * @param b Second value
   * @param c Third value
   * @return Maximum
   */
  public static double max(double a, double b, double c) {
    return a >= b ? (a >= c ? a : c) : (b >= c ? b : c);
  }

  /**
   * Quadrary max, <i>without</i> handling of special values.
   *
   * Because of the lack of special case handling, this is faster than
   * {@link Math#max}. But usually, it should be written inline.
   *
   * @param a First value
   * @param b Second value
   * @param c Third value
   * @param d Fourth value
   * @return Maximum
   */
  public static double max(double a, double b, double c, double d) {
    return a >= b ? //
        a >= c ? (a >= d ? a : d) : (c >= d ? c : d) //
        : //
        b >= c ? (b >= d ? b : d) : (c >= d ? c : d);
  }

  /**
   * Binary max. If possible, inline.
   *
   * @param a First value
   * @param b Second value
   * @return Maximum
   */
  public static int max(int a, int b) {
    return a >= b ? a : b;
  }

  /**
   * Ternary max. If possible, inline.
   *
   * @param a First value
   * @param b Second value
   * @param c Third value
   * @return Maximum
   */
  public static int max(int a, int b, int c) {
    return a >= b ? (a >= c ? a : c) : (b >= c ? b : c);
  }

  /**
   * Quadrary max, <i>without</i> handling of special values.
   *
   * Because of the lack of special case handling, this is faster than
   * {@link Math#max}. But usually, it should be written inline.
   *
   * @param a First value
   * @param b Second value
   * @param c Third value
   * @param d Fourth value
   * @return Maximum
   */
  public static int max(int a, int b, int c, int d) {
    return a >= b ? //
        a >= c ? (a >= d ? a : d) : (c >= d ? c : d) //
        : //
        b >= c ? (b >= d ? b : d) : (c >= d ? c : d);
  }

  /**
   * Binary min, <i>without</i> handling of special values.
   *
   * Because of the lack of special case handling, this is faster than
   * {@link Math#min}. But usually, it should be written inline as
   * {@code (a <= b) ? a : b}
   *
   * The result is asymmetric in case of {@code Double.NaN}:<br />
   * {@code MathUtil.min(Double.NaN, 1.)} is 1, but <br />
   * {@code MathUtil.min(1., Double.NaN)} is {@code Double.NaN}.
   *
   * @param a First value
   * @param b Second value
   * @return minimum
   */
  public static double min(double a, double b) {
    return a <= b ? a : b;
  }

  /**
   * Ternary min, <i>without</i> handling of special values.
   *
   * Because of the lack of special case handling, this is faster than
   * {@link Math#min}. But usually, it should be written inline.
   *
   * @param a First value
   * @param b Second value
   * @param c Third value
   * @return minimum
   */
  public static double min(double a, double b, double c) {
    return a <= b ? (a <= c ? a : c) : (b <= c ? b : c);
  }

  /**
   * Quadrary min, <i>without</i> handling of special values.
   *
   * Because of the lack of special case handling, this is faster than
   * {@link Math#min}. But usually, it should be written inline.
   *
   * @param a First value
   * @param b Second value
   * @param c Third value
   * @param d Fourth value
   * @return minimum
   */
  public static double min(double a, double b, double c, double d) {
    return a <= b ? //
        a <= c ? (a <= d ? a : d) : (c <= d ? c : d) //
        : //
        b <= c ? (b <= d ? b : d) : (c <= d ? c : d);
  }

  /**
   * Binary min, <i>without</i> handling of special values.
   *
   * Because of the lack of special case handling, this is faster than
   * {@link Math#min}. But usually, it should be written inline.
   *
   * @param a First value
   * @param b Second value
   * @return minimum
   */
  public static int min(int a, int b) {
    return a <= b ? a : b;
  }

  /**
   * Ternary min, <i>without</i> handling of special values.
   *
   * Because of the lack of special case handling, this is faster than
   * {@link Math#min}. But usually, it should be written inline.
   *
   * @param a First value
   * @param b Second value
   * @param c Third value
   * @return minimum
   */
  public static int min(int a, int b, int c) {
    return a <= b ? (a <= c ? a : c) : (b <= c ? b : c);
  }

  /**
   * Quadrary min, <i>without</i> handling of special values.
   *
   * Because of the lack of special case handling, this is faster than
   * {@link Math#min}. But usually, it should be written inline.
   *
   * @param a First value
   * @param b Second value
   * @param c Third value
   * @param d Fourth value
   * @return minimum
   */
  public static int min(int a, int b, int c, int d) {
    return a <= b ? //
        a <= c ? (a <= d ? a : d) : (c <= d ? c : d) //
        : //
        b <= c ? (b <= d ? b : d) : (c <= d ? c : d);
  }
}
