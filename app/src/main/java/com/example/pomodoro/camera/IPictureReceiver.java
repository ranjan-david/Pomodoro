package com.example.pomodoro.camera;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


public interface IPictureReceiver {
	File getSinkPath();

	UUID [] picturesTaken(@NonNull Context context,
                          @Nullable Uri[] picturesUri, boolean sinkFiles, long sequenceCounter,
                          @Nullable UUID docUUID, @Nullable UUID pageUUID, @Nullable AtomicReference<UUID> refInsertAfterUUID,
                          boolean openResult)
			throws IOException;
}
