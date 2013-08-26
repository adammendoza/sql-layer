/**
 * Copyright (C) 2009-2013 FoundationDB, LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.foundationdb.server.types.typestests;

import com.foundationdb.server.types.AkType;
import com.foundationdb.server.types3.TExecutionContext;
import com.foundationdb.server.types3.TInstance;
import com.foundationdb.server.types3.mcompat.mtypes.MDatetimes;
import com.foundationdb.server.types3.pvalue.PUnderlying;
import com.foundationdb.util.ByteSource;
import com.foundationdb.util.WrappingByteSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.foundationdb.server.types.typestests.TestCase.NO_STATE;

final class StandardTestCases {
    static Collection<TestCase<?>> get() {
        return onlyList;
    }

    private static Collection<TestCase<?>> make() {
        List<TestCase<?>> list = new ArrayList<>();
        
        // TODO need to figure out upper and lower limits, test those

        list.add(TestCase.forDate(0, NO_STATE));
        list.add(TestCase.forDate(5119897L, NO_STATE)); // 9999-12-15
        
        list.add(TestCase.forDateTime(0, NO_STATE));
        list.add(TestCase.forDateTime(99991231235959L, NO_STATE));
        
        list.add(TestCase.forDecimal(BigDecimal.ZERO, 1, 0, NO_STATE));
        list.add(TestCase.forDecimal(BigDecimal.ONE, 1, 0, NO_STATE));
        list.add(TestCase.forDecimal(new BigDecimal("10.0"), 3, 1, NO_STATE));
        list.add(TestCase.forDecimal(BigDecimal.valueOf(-10), 10, 0, NO_STATE));
        
        list.add(TestCase.forDouble(0, NO_STATE));
        list.add(TestCase.forDouble(-0, NO_STATE));
        list.add(TestCase.forDouble(1, NO_STATE));
        list.add(TestCase.forDouble(-1, NO_STATE));
        list.add(TestCase.forDouble(Double.MIN_VALUE, NO_STATE));
        list.add(TestCase.forDouble(Double.MAX_VALUE, NO_STATE));
        
        list.add(TestCase.forFloat(0, NO_STATE));
        list.add(TestCase.forFloat(-0, NO_STATE));
        list.add(TestCase.forFloat(1, NO_STATE));
        list.add(TestCase.forFloat(-1, NO_STATE));
        list.add(TestCase.forFloat(Float.MIN_VALUE, NO_STATE));
        list.add(TestCase.forFloat(Float.MAX_VALUE, NO_STATE));

        list.add(TestCase.forTinyInt(-1, NO_STATE));
        list.add(TestCase.forTinyInt(0, NO_STATE));
        list.add(TestCase.forTinyInt(1, NO_STATE));
        
        list.add(TestCase.forInt(-1, NO_STATE));
        list.add(TestCase.forInt(0, NO_STATE));
        list.add(TestCase.forInt(1, NO_STATE));
        list.add(TestCase.forInt(-1, Long.MAX_VALUE));
        list.add(TestCase.forInt(-1, Long.MIN_VALUE));

        list.add(TestCase.forLong(-1, NO_STATE));
        list.add(TestCase.forLong(0, NO_STATE));
        list.add(TestCase.forLong(1, NO_STATE));
        list.add(TestCase.forLong(-1, Long.MAX_VALUE));
        list.add(TestCase.forLong(-1, Long.MIN_VALUE));

        list.add(TestCase.forChar('A', NO_STATE));
        list.add(TestCase.forChar('Z', NO_STATE));
        
        list.add(TestCase.forString("", 32, "UTF-8", NO_STATE));
        list.add(TestCase.forString("word", 32, "UTF-8", NO_STATE));
        list.add(TestCase.forString("☃", 32, "UTF-8", NO_STATE));
        list.add(TestCase.forString("a ☃ is cold", 32, "UTF-8", NO_STATE));

        list.add(TestCase.forText("", 32, "UTF-8", NO_STATE));
        list.add(TestCase.forText("word", 32, "UTF-8", NO_STATE));
        list.add(TestCase.forText("☃", 32, "UTF-8", NO_STATE));
        list.add(TestCase.forText("a ☃ is cold", 32, "UTF-8", NO_STATE));

        list.add(TestCase.forTime(-1, NO_STATE));
        list.add(TestCase.forTime(0, NO_STATE));
        list.add(TestCase.forTime(1, NO_STATE));

        list.add(TestCase.forBool(true, NO_STATE));
        list.add(TestCase.forBool(false, NO_STATE));

        TExecutionContext context = new TExecutionContext (null, null, null);
        list.add(TestCase.forTimestamp(MDatetimes.parseTimestamp("0000-00-00 00:00:00", "UTC", context), NO_STATE));
        list.add(TestCase.forTimestamp(MDatetimes.parseTimestamp("1970-01-01 00:00:01", "UTC", context), NO_STATE));
        list.add(TestCase.forTimestamp(MDatetimes.parseTimestamp("2011-08-18 15:09:00", "UTC", context), NO_STATE));
        list.add(TestCase.forTimestamp(MDatetimes.parseTimestamp("2038-01-19 03:14:06", "UTC", context), NO_STATE));

        list.add(TestCase.forUBigInt(BigInteger.ZERO, NO_STATE));
        list.add(TestCase.forUBigInt(BigInteger.ONE, NO_STATE));
        list.add(TestCase.forUBigInt(BigInteger.TEN, NO_STATE));

        list.add(TestCase.forUDouble(0, NO_STATE));
        list.add(TestCase.forUDouble(-0, NO_STATE));
        list.add(TestCase.forUDouble(1, NO_STATE));
        list.add(TestCase.forUDouble(-1, NO_STATE));
        list.add(TestCase.forUDouble(Double.MIN_VALUE, NO_STATE));
        list.add(TestCase.forUDouble(Double.MAX_VALUE, NO_STATE));

        list.add(TestCase.forUFloat(0, NO_STATE));
        list.add(TestCase.forUFloat(-0, NO_STATE));
        list.add(TestCase.forUFloat(1, NO_STATE));
        list.add(TestCase.forUFloat(-1, NO_STATE));
        list.add(TestCase.forUFloat(Float.MIN_VALUE, NO_STATE));
        list.add(TestCase.forUFloat(Float.MAX_VALUE, NO_STATE));

        list.add(TestCase.forUInt(0, NO_STATE));
        list.add(TestCase.forUInt(1, NO_STATE));
        list.add(TestCase.forUInt(Integer.MAX_VALUE, NO_STATE));
        list.add(TestCase.forUInt(((long)Math.pow(2, 32))-1, NO_STATE));

        list.add(TestCase.forVarBinary(wrap(), 0, NO_STATE));
        list.add(TestCase.forVarBinary(wrap(Byte.MIN_VALUE, Byte.MAX_VALUE, 0), 2, NO_STATE));

        list.add(TestCase.forYear(MDatetimes.parseYear("0000", context), NO_STATE));
        list.add(TestCase.forYear(MDatetimes.parseYear("1901", context), NO_STATE));
        list.add(TestCase.forYear(MDatetimes.parseYear("1983", context), NO_STATE));
        list.add(TestCase.forYear(MDatetimes.parseYear("2155", context), NO_STATE));

        list.add(TestCase.forInterval_Millis(0, NO_STATE));
        list.add(TestCase.forInterval_Millis(Long.MAX_VALUE, NO_STATE));
        list.add(TestCase.forInterval_Millis(Long.MIN_VALUE, NO_STATE));

        list.add(TestCase.forInterval_Month(0, NO_STATE));
        list.add(TestCase.forInterval_Month(Long.MAX_VALUE, NO_STATE));
        list.add(TestCase.forInterval_Month(Long.MIN_VALUE, NO_STATE));

        verifyAllTypesTested(list);

        return Collections.unmodifiableCollection(list);
    }

    private static void verifyAllTypesTested(Collection<? extends TestCase<?>> testCases) {
        Set<PUnderlying> allTypes = EnumSet.allOf(PUnderlying.class);
        // No Types currently use this -> single char? 
        allTypes.remove(PUnderlying.UINT_16);
        for (TestCase<?> testCase : testCases) {
            allTypes.remove(TInstance.pUnderlying(testCase.type()));
        }
        if (!allTypes.isEmpty()) {
            throw new RuntimeException("Untested pUnderlying: " + allTypes);
        }
    }

    private static ByteSource wrap(int... values) {
        byte[] bytes = new byte[values.length];
        for (int i=0; i < bytes.length; ++i) {
            int asInt = values[i];
            if (asInt < Byte.MIN_VALUE || asInt > Byte.MAX_VALUE) {
                throw new AssertionError("invalid byte value " + asInt);
            }
            bytes[i] = (byte)asInt;
        }
        return new WrappingByteSource().wrap(bytes);
    }

    private static Collection<TestCase<?>> onlyList = make();
}
