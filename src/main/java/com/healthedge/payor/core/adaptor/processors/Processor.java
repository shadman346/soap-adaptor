package com.healthedge.payor.core.adaptor.processors;

import com.healthedge.payor.core.adaptor.context.Context;
import java.io.Serializable;

public interface Processor {
    void process(Context context);
}
