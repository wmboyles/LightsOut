/*
 * Gauss-Jordan elimination over any field (Java)
 *
 * Copyright (c) 2019 Project Nayuki
 * All rights reserved. Contact Nayuki for licensing.
 * https://www.nayuki.io/page/gauss-jordan-elimination-over-any-field
 */

package com.game.lightsout;

import java.util.Objects;

/**
 * A finite field of the form Z<sub><var>p</var></sub>, where <var>p</var> is a prime number.
 * Each element of this kind of field is an integer in the range [0, <var>p</var>).
 * Both the field and the elements are immutable and thread-safe.
 *
 * @author Project Nayuki
 */
public final class PrimeField extends Field<Integer> {
    /**
     * The modulus of this field, which is also the number of elements in this finite field. Must be prime.
     */
    private final int modulus;


    /**
     * Constructs a prime field with the specified modulus. The modulus must be a
     * prime number, but this critical property is not checked by the constructor.
     *
     * @param mod the modulus, which must be prime
     * @throws IllegalArgumentException if {@code mod} &lt; 2
     */
    public PrimeField(int mod) {
        if (mod < 2) {
            throw new IllegalArgumentException("Modulus must be prime");
        }
        this.modulus = mod;
    }


    public Integer zero() {
        return 0;
    }


    public Integer one() {
        return 1;
    }


    public boolean equals(Integer x, Integer y) {
        return check(x) == check(y);
    }


    public Integer negate(Integer x) {
        return (modulus - check(x)) % modulus;
    }


    public Integer add(Integer x, Integer y) {
        return (int) (((long) check(x) + check(y)) % modulus);
    }


    public Integer subtract(Integer x, Integer y) {
        return (int) (((long) check(x) + modulus - check(y)) % modulus);
    }


    public Integer multiply(Integer x, Integer y) {
        return (int) ((long) check(x) * check(y) % modulus);
    }


    public Integer reciprocal(Integer w) {
        // Extended Euclidean GCD algorithm
        int x = modulus;
        int y = check(w);
        if (y == 0) {
            throw new ArithmeticException("Division by zero");
        }
        int a = 0, b = 1;
        while (y != 0) {
            int z = x % y;
            int c = a - x / y * b;
            x = y;
            y = z;
            a = b;
            b = c;
        }
        if (x != 1) { // All non-zero values must have a reciprocal
            throw new IllegalStateException("Field modulus is not prime");
        }

        return (int) (((long) a + modulus) % modulus);
    }


    // Checks if the given object is non-null and within the range
    // of valid values, and returns the unboxed primitive value.
    private int check(Integer i) {
        Objects.requireNonNull(i);
        if (i < 0 || i >= modulus) {
            throw new IllegalArgumentException("Not an element of this field: " + i);
        }

        return i;
    }
}