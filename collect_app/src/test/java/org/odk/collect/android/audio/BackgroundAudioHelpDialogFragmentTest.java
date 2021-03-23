package org.odk.collect.android.audio;

import android.content.DialogInterface;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.odk.collect.android.support.RobolectricHelpers.launchDialogFragment;

@RunWith(AndroidJUnit4.class)
public class BackgroundAudioHelpDialogFragmentTest {

    @Test
    public void hasOkButton() {
        FragmentScenario<BackgroundAudioHelpDialogFragment> scenario = launchDialogFragment(BackgroundAudioHelpDialogFragment.class);
        scenario.onFragment(f -> {
            AlertDialog dialog = (AlertDialog) f.getDialog();
            Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            assertThat(okButton.isShown(), is(true));
        });
    }
}
