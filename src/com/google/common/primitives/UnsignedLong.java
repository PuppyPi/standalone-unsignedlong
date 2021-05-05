/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.primitives;

import static java.util.Objects.*;
import java.io.Serializable;
import java.math.BigInteger;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

/**
 * A wrapper class for unsigned {@code long} values, supporting arithmetic operations.
 *
 * <p>In some cases, when speed is more important than code readability, it may be faster simply to
 * treat primitive {@code long} values as unsigned, using static methods to deal with them.
 * 
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/PrimitivesExplained#unsigned-support">
 * unsigned primitive utilities</a>.
 *
 * @author Louis Wasserman
 * @author Colin Evans
 * @since 11.0
 */
//@GwtCompatible(serializable = true)
public final class UnsignedLong extends Number implements Comparable<UnsignedLong>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final long UNSIGNED_MASK = 0x7fffffffffffffffL;
	
	public static final UnsignedLong ZERO = new UnsignedLong(0);
	public static final UnsignedLong ONE = new UnsignedLong(1);
	public static final UnsignedLong MAX_VALUE = new UnsignedLong(-1L);
	
	private final long value;
	
	private UnsignedLong(long value) {
		this.value = value;
	}
	
	/**
	 * Returns an {@code UnsignedLong} corresponding to a given bit representation.
	 * The argument is interpreted as an unsigned 64-bit value. Specifically, the sign bit
	 * of {@code bits} is interpreted as a normal bit, and all other bits are treated as usual.
	 *
	 * <p>If the argument is nonnegative, the returned result will be equal to {@code bits},
	 * otherwise, the result will be equal to {@code 2^64 + bits}.
	 *
	 * <p>To represent decimal constants less than {@code 2^63}, consider {@link #valueOf(long)}
	 * instead.
	 *
	 * @since 14.0
	 */
	public static UnsignedLong fromLongBits(long bits) {
		// TODO(user): consider caching small values, like Long.valueOf
		return new UnsignedLong(bits);
	}
	
	/**
	 * Returns an {@code UnsignedLong} representing the same value as the specified {@code long}.
	 *
	 * @throws IllegalArgumentException if {@code value} is negative
	 * @since 14.0
	 */
	public static UnsignedLong valueOf(long value) {
		if (value < 0)
			throw new IllegalArgumentException("value ("+value+") is outside the range for an unsigned long value");
		return fromLongBits(value);
	}
	
	/**
	 * Returns a {@code UnsignedLong} representing the same value as the specified
	 * {@code BigInteger}. This is the inverse operation of {@link #bigIntegerValue()}.
	 *
	 * @throws IllegalArgumentException if {@code value} is negative or {@code value >= 2^64}
	 */
	public static UnsignedLong valueOf(BigInteger value) {
		requireNonNull(value);
		
		if (!(value.signum() >= 0 && value.bitLength() <= Long.SIZE))
			throw new IllegalArgumentException("value ("+value+") is outside the range for an unsigned long value");
		
		return fromLongBits(value.longValue());
	}
	
	/**
	 * Returns an {@code UnsignedLong} holding the value of the specified {@code String}, parsed as
	 * an unsigned {@code long} value.
	 *
	 * @throws NumberFormatException if the string does not contain a parsable unsigned {@code long}
	 *     value
	 */
	public static UnsignedLong valueOf(String string) {
		return valueOf(string, 10);
	}
	
	/**
	 * Returns an {@code UnsignedLong} holding the value of the specified {@code String}, parsed as
	 * an unsigned {@code long} value in the specified radix.
	 *
	 * @throws NumberFormatException if the string does not contain a parsable unsigned {@code long}
	 *     value, or {@code radix} is not between {@link Character#MIN_RADIX} and
	 *     {@link Character#MAX_RADIX}
	 */
	public static UnsignedLong valueOf(String string, int radix) {
		return fromLongBits(parseUnsignedLong(string, radix));
	}
	
	/**
	 * Returns the result of adding this and {@code val}. If the result would have more than 64 bits,
	 * returns the low 64 bits of the result.
	 *
	 * @since 14.0
	 */
	@CheckReturnValue
	public UnsignedLong plus(UnsignedLong val) {
		return fromLongBits(this.value + requireNonNull(val).value);
	}
	
	/**
	 * Returns the result of subtracting this and {@code val}. If the result would have more than 64
	 * bits, returns the low 64 bits of the result.
	 *
	 * @since 14.0
	 */
	@CheckReturnValue
	public UnsignedLong minus(UnsignedLong val) {
		return fromLongBits(this.value - requireNonNull(val).value);
	}
	
	/**
	 * Returns the result of multiplying this and {@code val}. If the result would have more than 64
	 * bits, returns the low 64 bits of the result.
	 *
	 * @since 14.0
	 */
	@CheckReturnValue
	public UnsignedLong times(UnsignedLong val) {
		return fromLongBits(value * requireNonNull(val).value);
	}
	
	/**
	 * Returns the result of dividing this by {@code val}.
	 *
	 * @since 14.0
	 */
	@CheckReturnValue
	public UnsignedLong dividedBy(UnsignedLong val) {
		return fromLongBits(divide(value, requireNonNull(val).value));
	}
	
	/**
	 * Returns this modulo {@code val}.
	 *
	 * @since 14.0
	 */
	@CheckReturnValue
	public UnsignedLong mod(UnsignedLong val) {
		return fromLongBits(remainder(value, requireNonNull(val).value));
	}
	
	/**
	 * Returns the value of this {@code UnsignedLong} as an {@code int}.
	 */
	@Override
	public int intValue() {
		return (int) value;
	}
	
	/**
	 * Returns the value of this {@code UnsignedLong} as a {@code long}. This is an inverse operation
	 * to {@link #fromLongBits}.
	 *
	 * <p>Note that if this {@code UnsignedLong} holds a value {@code >= 2^63}, the returned value
	 * will be equal to {@code this - 2^64}.
	 */
	@Override
	public long longValue() {
		return value;
	}
	
	/**
	 * Returns the value of this {@code UnsignedLong} as a {@code float}, analogous to a widening
	 * primitive conversion from {@code long} to {@code float}, and correctly rounded.
	 */
	@Override
	public float floatValue() {
		@SuppressWarnings("cast")
		float fValue = (float) (value & UNSIGNED_MASK);
		if (value < 0) {
			fValue += 0x1.0p63f;
		}
		return fValue;
	}
	
	/**
	 * Returns the value of this {@code UnsignedLong} as a {@code double}, analogous to a widening
	 * primitive conversion from {@code long} to {@code double}, and correctly rounded.
	 */
	@Override
	public double doubleValue() {
		@SuppressWarnings("cast")
		double dValue = (double) (value & UNSIGNED_MASK);
		if (value < 0) {
			dValue += 0x1.0p63;
		}
		return dValue;
	}
	
	/**
	 * Returns the value of this {@code UnsignedLong} as a {@link BigInteger}.
	 */
	public BigInteger bigIntegerValue() {
		BigInteger bigInt = BigInteger.valueOf(value & UNSIGNED_MASK);
		if (value < 0) {
			bigInt = bigInt.setBit(Long.SIZE - 1);
		}
		return bigInt;
	}
	
	@Override
	public int compareTo(UnsignedLong o) {
		requireNonNull(o);
		return compare(value, o.value);
	}
	
	@Override
	public int hashCode() {
		return (int) (value ^ (value >>> 32));
	}
	
	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj instanceof UnsignedLong) {
			UnsignedLong other = (UnsignedLong) obj;
			return value == other.value;
		}
		return false;
	}
	
	/**
	 * Returns a string representation of the {@code UnsignedLong} value, in base 10.
	 */
	@Override
	public String toString() {
		return toString(value);
	}
	
	/**
	 * Returns a string representation of the {@code UnsignedLong} value, in base {@code radix}. If
	 * {@code radix < Character.MIN_RADIX} or {@code radix > Character.MAX_RADIX}, the radix
	 * {@code 10} is used.
	 */
	public String toString(int radix) {
		return toString(value, radix);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//// Copied from UnsignedLongs.java to remove dependencies and make this file be self-contained ////
	
	/**
	 * Returns a string representation of x, where x is treated as unsigned.
	 */
	@CheckReturnValue
	private static String toString(long x) {
		return toString(x, 10);
	}
	
	/**
	 * Returns a string representation of {@code x} for the given radix, where {@code x} is treated
	 * as unsigned.
	 *
	 * @param x the value to convert to a string.
	 * @param radix the radix to use while working with {@code x}
	 * @throws IllegalArgumentException if {@code radix} is not between {@link Character#MIN_RADIX}
	 *         and {@link Character#MAX_RADIX}.
	 */
	@CheckReturnValue
	private static String toString(long x, int radix) {
		if (!(radix >= Character.MIN_RADIX && radix <= Character.MAX_RADIX))
			throw new IllegalArgumentException("radix ("+radix+") must be between Character.MIN_RADIX and Character.MAX_RADIX");
		
		if (x == 0) {
			// Simply return "0"
			return "0";
		} else {
			char[] buf = new char[64];
			int i = buf.length;
			if (x < 0) {
				// Separate off the last digit using unsigned division. That will leave
				// a number that is nonnegative as a signed integer.
				long quotient = divide(x, radix);
				long rem = x - quotient * radix;
				buf[--i] = Character.forDigit((int) rem, radix);
				x = quotient;
			}
			// Simple modulo/division approach
			while (x > 0) {
				buf[--i] = Character.forDigit((int) (x % radix), radix);
				x /= radix;
			}
			// Generate string
			return new String(buf, i, buf.length - i);
		}
	}
	
	
	private static long parseUnsignedLong(String s, int radix) {
		requireNonNull(s);
		if (s.length() == 0) {
			throw new NumberFormatException("empty string");
		}
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
			throw new NumberFormatException("illegal radix: " + radix);
		}
		
		int max_safe_pos = maxSafeDigits[radix] - 1;
		long value = 0;
		for (int pos = 0; pos < s.length(); pos++) {
			int digit = Character.digit(s.charAt(pos), radix);
			if (digit == -1) {
				throw new NumberFormatException(s);
			}
			if (pos > max_safe_pos && overflowInParse(value, digit, radix)) {
				throw new NumberFormatException("Too large for unsigned long: " + s);
			}
			value = (value * radix) + digit;
		}
		
		return value;
	}
	
	private static boolean overflowInParse(long current, int digit, int radix) {
		if (current >= 0) {
			if (current < maxValueDivs[radix]) {
				return false;
			}
			if (current > maxValueDivs[radix]) {
				return true;
			}
			// current == maxValueDivs[radix]
			return (digit > maxValueMods[radix]);
		}
		
		// current < 0: high bit is set
		return true;
	}
	
	private static final int[] maxSafeDigits = new int[Character.MAX_RADIX + 1];
	private static final long[] maxValueDivs = new long[Character.MAX_RADIX + 1];
	private static final int[] maxValueMods = new int[Character.MAX_RADIX + 1];
	
	
	
	
	@CheckReturnValue
	private static long divide(long dividend, long divisor) {
		if (divisor < 0) { // i.e., divisor >= 2^63:
			if (compare(dividend, divisor) < 0) {
				return 0; // dividend < divisor
			} else {
				return 1; // dividend >= divisor
			}
		}
		
		// Optimization - use signed division if dividend < 2^63
		if (dividend >= 0) {
			return dividend / divisor;
		}
		
		/*
		 * Otherwise, approximate the quotient, check, and correct if necessary. Our approximation is
		 * guaranteed to be either exact or one less than the correct value. This follows from fact
		 * that floor(floor(x)/i) == floor(x/i) for any real x and integer i != 0. The proof is not
		 * quite trivial.
		 */
		long quotient = ((dividend >>> 1) / divisor) << 1;
		long rem = dividend - quotient * divisor;
		return quotient + (compare(rem, divisor) >= 0 ? 1 : 0);
	}
	
	
	
	@CheckReturnValue
	private static int compare(long a, long b) {
		a = flip(a);
		b = flip(b);
		return (a < b) ? -1 : ((a > b) ? 1 : 0);
	}
	
	
	private static long flip(long a) {
		return a ^ Long.MIN_VALUE;
	}
	
	
	
	@CheckReturnValue
	private static long remainder(long dividend, long divisor) {
		if (divisor < 0) { // i.e., divisor >= 2^63:
			if (compare(dividend, divisor) < 0) {
				return dividend; // dividend < divisor
			} else {
				return dividend - divisor; // dividend >= divisor
			}
		}
		
		// Optimization - use signed modulus if dividend < 2^63
		if (dividend >= 0) {
			return dividend % divisor;
		}
		
		/*
		 * Otherwise, approximate the quotient, check, and correct if necessary. Our approximation is
		 * guaranteed to be either exact or one less than the correct value. This follows from fact
		 * that floor(floor(x)/i) == floor(x/i) for any real x and integer i != 0. The proof is not
		 * quite trivial.
		 */
		long quotient = ((dividend >>> 1) / divisor) << 1;
		long rem = dividend - quotient * divisor;
		return rem - (compare(rem, divisor) >= 0 ? divisor : 0);
	}
}
