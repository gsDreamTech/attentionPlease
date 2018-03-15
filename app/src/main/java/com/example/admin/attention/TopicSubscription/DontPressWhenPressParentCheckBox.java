package com.example.admin.attention.TopicSubscription;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


public class DontPressWhenPressParentCheckBox extends android.support.v7.widget.AppCompatCheckBox
{

	public DontPressWhenPressParentCheckBox(Context context)
	{
		super(context);
	}

	public DontPressWhenPressParentCheckBox(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public DontPressWhenPressParentCheckBox(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public void setPressed(boolean pressed)
	{
		if (pressed && getParent() instanceof View && ((View) getParent()).isPressed())
		{
			return;
		}
		super.setPressed(pressed);
	}
}