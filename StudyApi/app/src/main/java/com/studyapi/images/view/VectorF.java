package com.studyapi.images.view;

import android.graphics.PointF;
import android.view.MotionEvent;

public class VectorF {
	
	public float angle;
	public float length;
	
	public final PointF start = new PointF();
	public final PointF end = new PointF();
	
	public void calculateEndPoint() {
		end.x = (float)Math.cos(angle) * length + start.x;
		end.y = (float)Math.sin(angle) * length + start.y;
	}
	
	public void setStart(PointF p) {
		this.start.x = p.x;
		this.start.y = p.y;
	}
	
	public void setEnd(PointF p) {
		this.end.x = p.x;
		this.end.y = p.y;
	}
	
	public void set(MotionEvent event) {
		this.start.x = event.getX(0);
		this.start.y = event.getY(0);
		this.end.x = event.getX(1);
		this.end.y = event.getY(1);
	}
	
	public float calculateLength() {
        PointF p1 = start;
        PointF p2 = end;
        float x = p1.x - p2.x;
        float y = p1.y - p2.y;
        length = (float)Math.sqrt(x * x + y * y);
        return length;
	}

    private float angle(float x1, float y1, float x2, float y2) {
        return (float) Math.atan2(y2 - y1, x2 - x1);
    }

	public float calculateAngle() {
        PointF p1 = start, p2 = end;
        angle = angle(p1.x, p1.y, p2.x, p2.y);
		return angle;
	}

}
