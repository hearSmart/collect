package org.odk.collect.android.mhealthintegration;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.hearxgroup.encryption.Logger;
import com.hearxgroup.hearx.Constants;
import com.hearxgroup.mhealthintegration.Models.HeartestTest;
import com.hearxgroup.mhealthintegration.Models.MHealthTestRequest;
import com.hearxgroup.mhealthintegration.Models.Patient;
import com.hearxgroup.mhealthintegration.TestRequestHelper;
import com.hearxgroup.mhealthintegration.Util;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.IFormElement;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.TreeReference;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.widgets.QuestionWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) 2017 hearX Group (Pty) Ltd. All rights reserved
 * Created by David Howe on 2018/08/25.
 * hearX Group (Pty) Ltd.
 * info@hearxgroup.com
 */
public class MHealthUtil {

    private static final String TAG = MHealthUtil.class.getSimpleName();

    public static @Nullable Patient buildPatient(String participantId) {
        Log.d(TAG, "buildPatient");
        Log.d(TAG, "participantId: "+participantId);
        //BUILD ANONYMOUS PATIENT
        return Patient.build(
                "Anonymous",//firstName
                "Anonymous",//lastName
                "1980-10-15",//YYYY-MM-dd
                "male", //male/female
                "eng",//iso3 languageCode
                null,//email
                null,//contactnumber
                null,//identificationNumber (Users national identification number)
                participantId);//referenceNumber (Any reference string you have to connect with your system)
    }

    public static Bundle getODKReturnBundle(HeartestTest htTest) {
        Log.d(TAG, "returnToODK");
        if(htTest.getTotalResponses()==0)
            htTest.setTotalResponses(1);
        Bundle bundle = new Bundle();
        bundle.putBoolean("test_heartest", true);
        bundle.putInt("average_l", (int)htTest.getPtaLeft());
        bundle.putInt("average_r", (int)htTest.getPtaRight());
        bundle.putInt("test_duration", htTest.getDuration());
        bundle.putInt("false_response", (htTest.getTotalFalseResponses()*100)/htTest.getTotalResponses());
        bundle.putString("note", htTest.getNotes());

        int condition1kL = 0;
        int norm1kL = 0;
        int condition1kR = 0;
        int norm1kR = 0;

        for(int k=0; k<htTest.getFrequencyResults().length; k++) {
            String ptaKey;
            String noiseKey;
            if(htTest.getFrequencyResults()[k].getEar()== com.hearxgroup.hearx.Constants.EAR_LEFT) {
                ptaKey = "pta_l_";
                noiseKey = "amb_l_";
            }
            else {
                ptaKey = "pta_r_";
                noiseKey = "amb_r_";
            }

            switch(htTest.getFrequencyResults()[k].getFrequency()) {
                case 500:
                    ptaKey+="05k";
                    noiseKey+="05k";
                    break;

                case 1000:
                    ptaKey+="1k";
                    noiseKey+="1k";
                    if(htTest.getFrequencyResults()[k].isConditionTone()) {
                        if(htTest.getFrequencyResults()[k].getEar()== com.hearxgroup.hearx.Constants.EAR_LEFT)
                            condition1kL = htTest.getFrequencyResults()[k].getThreshold();
                        else
                            condition1kR = htTest.getFrequencyResults()[k].getThreshold();
                    }
                    else {
                        if(htTest.getFrequencyResults()[k].getEar()== com.hearxgroup.hearx.Constants.EAR_LEFT)
                            norm1kL = htTest.getFrequencyResults()[k].getThreshold();
                        else
                            norm1kR = htTest.getFrequencyResults()[k].getThreshold();
                    }
                    break;

                case 2000:
                    ptaKey+="2k";
                    noiseKey+="2k";
                    break;

                case 4000:
                    ptaKey+="4k";
                    noiseKey+="4k";
                    break;
            }

            if(!htTest.getFrequencyResults()[k].isConditionTone()) {
                bundle.putInt(ptaKey, htTest.getFrequencyResults()[k].getThreshold());
                bundle.putInt(noiseKey, (int) htTest.getFrequencyResults()[k].getNoise());
            }
        }

        if(Math.abs(condition1kL-norm1kL)>=10) {
            bundle.putInt("test_retest_l", 1);
            bundle.putString("test_retest_l_spec", "1kHz Left Ear Test-Retest thresholds differ by "+Math.abs(condition1kL-norm1kL)+"dB");
        }
        else
            bundle.putInt("test_retest_l", 0);

        if(Math.abs(condition1kR-norm1kR)>=10) {
            bundle.putInt("test_retest_r", 1);
            bundle.putString("test_retest_r_spec", "1kHz Right Ear Test-Retest thresholds differ by "+Math.abs(condition1kR-norm1kR)+"dB");
        }
        else
            bundle.putInt("test_retest_r", 0);

       return bundle;
    }

    public static void requestMHTest(Context context, @Nullable Patient patient) {
        //GENERATE UNIQUE 24 CHAR TEST ID
        String testId = getRandomSequence();
        //BUILD TEST REQUEST
        MHealthTestRequest testRequest =
                MHealthTestRequest.build(
                        testId, //UNIQUE TEST ID
                        "org.odk.collect.mhealthtest", //ACTION NAME AS DEFINED IN YOUR MANIFEST
                        patient,
                        Constants.INDEX_HEARTEST); //PATIENT OBJECT OR NULL
        //UTILITY TO HELP YOU VALIDATE YOUR TEST REQUEST
        String requestValidationResponse = Util.validateTestRequest(context, testRequest);
        if(requestValidationResponse==null)
            //VALIDATION WAS PASSED, INITIATE TEST REQUEST
            TestRequestHelper.startTest(context, testRequest);
        else
            //VALIDATION ERROR OCCURRED
            Log.e("requestMHTest", "Validation error:"+requestValidationResponse);
    }

    public static String getRandomSequence() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
