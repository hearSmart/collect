package org.odk.collect.android.widgets.interfaces;

/**
 * Interface implemented by widgets that need binary data.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public interface BinaryWidget extends ButtonClickListener {
    void setBinaryData(Object answer);
}