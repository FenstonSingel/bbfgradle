package com.stepanov.bbf.coverage.instrumentation;

import java.lang.instrument.Instrumentation;

public class PremainClass {

    public static void premain(String args, Instrumentation instr) throws Exception {
        instr.addTransformer(new Transformer());
    }

}
