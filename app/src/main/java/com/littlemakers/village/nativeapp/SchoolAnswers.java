package com.littlemakers.village.nativeapp;
import android.content.Context;import android.view.*;import android.widget.*;
/** Measured native answer text; long answers and large fonts grow instead of clipping in a canvas. */
final class SchoolAnswers extends LinearLayout {
 interface Listener{void chosen(int answer);}
 private final Button[] choices=new Button[3];private final ThemeManager theme;private int direction=-1;
 SchoolAnswers(Context c,ThemeManager t,Listener listener){super(c);theme=t;for(int i=0;i<3;i++){final int answer=i;Button b=new Button(c);b.setAllCaps(false);b.setSingleLine(false);b.setMaxLines(Integer.MAX_VALUE);b.setEllipsize(null);b.setHorizontallyScrolling(false);b.setText("Answer");GameUi.control(b,t);b.setPadding(t.dp(12),t.dp(8),t.dp(12),t.dp(8));b.setGravity(Gravity.CENTER);b.setOnClickListener(v->{v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);listener.chosen(answer);});choices[i]=b;addView(b);}setOrientation(VERTICAL);}
 void choices(String[] values,boolean enabled){for(int i=0;i<3;i++){choices[i].setText((i+1)+" · "+values[i]);choices[i].setContentDescription("Answer "+(i+1)+", "+values[i]);choices[i].setEnabled(enabled);}requestLayout();}
 void enableAnswers(boolean enabled){for(Button b:choices)b.setEnabled(enabled);}
 @Override protected void onMeasure(int w,int h){int width=MeasureSpec.getSize(w);boolean row=true;float column=width/3f-theme.dp(32);for(Button b:choices)if(b.getPaint().measureText(b.getText().toString())>column)row=false;int next=row?HORIZONTAL:VERTICAL;if(next!=direction){direction=next;setOrientation(next);for(Button b:choices){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(row?0:-1,-2,row?1:0);p.setMargins(theme.dp(3),theme.dp(3),theme.dp(3),theme.dp(3));b.setLayoutParams(p);}}super.onMeasure(w,h);}
}
