/**
 * END USER LICENSE AGREEMENT (“EULA”)
 *
 * READ THIS AGREEMENT CAREFULLY (date: 9/13/2011):
 * http://www.akiban.com/licensing/20110913
 *
 * BY INSTALLING OR USING ALL OR ANY PORTION OF THE SOFTWARE, YOU ARE ACCEPTING
 * ALL OF THE TERMS AND CONDITIONS OF THIS AGREEMENT. YOU AGREE THAT THIS
 * AGREEMENT IS ENFORCEABLE LIKE ANY WRITTEN AGREEMENT SIGNED BY YOU.
 *
 * IF YOU HAVE PAID A LICENSE FEE FOR USE OF THE SOFTWARE AND DO NOT AGREE TO
 * THESE TERMS, YOU MAY RETURN THE SOFTWARE FOR A FULL REFUND PROVIDED YOU (A) DO
 * NOT USE THE SOFTWARE AND (B) RETURN THE SOFTWARE WITHIN THIRTY (30) DAYS OF
 * YOUR INITIAL PURCHASE.
 *
 * IF YOU WISH TO USE THE SOFTWARE AS AN EMPLOYEE, CONTRACTOR, OR AGENT OF A
 * CORPORATION, PARTNERSHIP OR SIMILAR ENTITY, THEN YOU MUST BE AUTHORIZED TO SIGN
 * FOR AND BIND THE ENTITY IN ORDER TO ACCEPT THE TERMS OF THIS AGREEMENT. THE
 * LICENSES GRANTED UNDER THIS AGREEMENT ARE EXPRESSLY CONDITIONED UPON ACCEPTANCE
 * BY SUCH AUTHORIZED PERSONNEL.
 *
 * IF YOU HAVE ENTERED INTO A SEPARATE WRITTEN LICENSE AGREEMENT WITH AKIBAN FOR
 * USE OF THE SOFTWARE, THE TERMS AND CONDITIONS OF SUCH OTHER AGREEMENT SHALL
 * PREVAIL OVER ANY CONFLICTING TERMS OR CONDITIONS IN THIS AGREEMENT.
 */

package com.akiban.server.types3.mcompat.mfuncs;

import com.akiban.server.types3.LazyList;
import com.akiban.server.types3.TExecutionContext;
import com.akiban.server.types3.TOverload;
import com.akiban.server.types3.TOverloadResult;
import com.akiban.server.types3.common.DateExtractor;
import com.akiban.server.types3.mcompat.mtypes.MDatetimes;
import com.akiban.server.types3.mcompat.mtypes.MNumeric;
import com.akiban.server.types3.mcompat.mtypes.MString;
import com.akiban.server.types3.pvalue.PValueSource;
import com.akiban.server.types3.pvalue.PValueTarget;
import com.akiban.server.types3.texpressions.TInputSetBuilder;
import com.akiban.server.types3.texpressions.TOverloadBase;
import org.joda.time.MutableDateTime;




public class MDay extends TOverloadBase{
    
    private static final int ZERO_INDEX = 0;
    
    static enum DateType {
        DAY {
            @Override
            long evaluate(MutableDateTime cal, long[] input)
            {
                return input[DateExtractor.DAY];
            }            
        },
        DAYOFMONTH {
            @Override
            long evaluate(MutableDateTime cal, long[] input)
            {
                return input[DateExtractor.DAY];
            }
        },
        DAYOFWEEK {
            @Override
            long evaluate(MutableDateTime cal, long[] input)
            {
                return (cal.getDayOfWeek() % 7) + 1;
            }
        },
        DAYOFYEAR {
            @Override
            long evaluate(MutableDateTime cal, long[] input)
            {
                return cal.getDayOfYear();
            }
        },
        WEEKDAY {
            @Override
            long evaluate(MutableDateTime cal, long[] input)
            {
                return cal.getDayOfWeek() - 1;
            }
        };
        
        abstract long evaluate(MutableDateTime cal, long[] input);
    }
    
    private final DateType dateType;
    
    MDay(DateType dateType) {
        this.dateType = dateType;
    }
    
    @Override
    protected void buildInputSets(TInputSetBuilder builder) {
        builder.covers(MDatetimes.DATE, 0);
    }

    @Override
    protected void doEvaluate(TExecutionContext context, LazyList<? extends PValueSource> inputs, PValueTarget output) {
        long[] datetime = DateExtractor.extract(inputs.get(0).getInt64());
        MutableDateTime cal = DateExtractor.getMutableDateTime(context, datetime, true);
        
        if (!DateExtractor.validHrMinSec(datetime) || !DateExtractor.validDayMonth(datetime)) output.putNull();
        else output.putInt32((int)(dateType.evaluate(cal, datetime)));
    }

    @Override
    public String overloadName() {
        return dateType.name();
    }

    @Override
    public TOverloadResult resultType() {
        return TOverloadResult.fixed(MNumeric.INT.instance());
    }
}
