package com.example.pomodoro.camera;

import android.graphics.PointF;

public interface ICameraOverlay {
	void showCorners(boolean shown);
	void showAlert(boolean alert, int delay);

	// 4 points: top-left, top-right, bottom-left, bottom-right
	void setDocumentCorners(PointF[] points);
}
