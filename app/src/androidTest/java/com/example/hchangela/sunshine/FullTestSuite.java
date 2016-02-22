package com.example.hchangela.sunshine;

import android.test.suitebuilder.TestSuiteBuilder;

import com.example.hchangela.sunshine.data.WeatherDbHelper;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by Prashant Mehta on 1/27/15.
 */
public class FullTestSuite extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }

    public FullTestSuite(){
        super();
    }

}
