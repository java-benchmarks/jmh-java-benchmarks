/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.yulikexuan.benchmarks.jdk;


import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Fork(value = 2)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class StringInternBenchmark {

    /*
     * The Blackhole class used here is a jmh feature that addresses one of the
     * points about microbenchmarking:
     *     if the value of an operation isn’t used, the compiler is free to
     *     optimize out the operation. So we make sure that the values are used
     *     by passing them to the consume() method of the Blackhole
     */
    @Benchmark
    public void testIntern(Blackhole bh, InternState internState) {
        String[] strArr = internState.getStrArray();
        for (int i = 0; i < internState.getStrAmt(); i++) {
            String t = strArr[i].intern();
            bh.consume(t);
        }
    }

    @Benchmark
    public void testMap(Blackhole bh, InternState internState) {
        ConcurrentHashMap<String,String> map = internState.getMap();
        String[] strArr = internState.getStrArray();
        for (int i = 0; i < internState.getStrAmt(); i++) {
            String s = strArr[i];
            String t = map.putIfAbsent(s, s);
            bh.consume(t);
        }
    }

    @State(Scope.Thread)
    public static class InternState {

        @Param({"100"})
        private int strAmt;

        private String[] strArray;
        private ConcurrentHashMap<String,String> map;

        @Setup(Level.Iteration)
        public void doSetup() {
            strArray = new String[strAmt];
            for (int i = 0; i < strAmt; i++) {
                strArray[i] = RandomStringUtils.randomAlphanumeric(17);
            }
            map = new ConcurrentHashMap<>();
        }

        @TearDown(Level.Iteration)
        public void doTearDown() {

            for (int i = 0; i < strAmt; i++) {
                strArray[i] = null;
            }
            strArray = null;

            map.clear();
            map = null;
        }

        public int getStrAmt() {
            return this.strAmt;
        }

        public String[] getStrArray() {
            return this.strArray;
        }

        public ConcurrentHashMap<String, String> getMap() {
            return this.map;
        }

    }//: End of class InternState
}