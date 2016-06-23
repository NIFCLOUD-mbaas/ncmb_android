package com.nifty.cloud.mb.core;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.internal.bytecode.InstrumentationConfiguration;
import org.robolectric.internal.bytecode.ShadowMap;


public class CustomRobolectricTestRunner extends RobolectricTestRunner {

    public CustomRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected ShadowMap createShadowMap() {
        return super.createShadowMap().newBuilder()
                //モック時に動作するクラスを登録する
                .addShadowClass(ShadowNCMBUser.class)
                .build();
    }

    public InstrumentationConfiguration createClassLoaderConfig() {
        return InstrumentationConfiguration.newBuilder()
                //モック対象のクラスを登録する
                .addInstrumentedClass(NCMBUser.class.getName())
                .build();
    }
}