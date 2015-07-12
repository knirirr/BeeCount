package com.knirirr.beecount;


/*
  This code from:
  http://pastebin.com/raw.php?i=e6WyrwSN
  As mentioned in this thread:
  https://stackoverflow.com/questions/16017165/auto-fit-textview-for-android
 */

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

/**
 * This class builds a new android Widget named AutoFitText which can be used instead of a TextView
 * to have the text font size in it automatically fit to match the screen width. Credits go largely
 * to Dunni, gjpc, gregm and speedplane from Stackoverflow, method has been (style-) optimized and
 * rewritten to match android coding standards and our MBC. This version upgrades the original
 * "AutoFitTextView" to now also be adaptable to height and to accept the different TextView types
 * (Button, TextClock etc.)
 *
 * @author pheuschk
 * @createDate: 18.04.2013
 */
@SuppressWarnings("unused")
public class AutoFitText extends TextView {

  /** Global min and max for text size. Remember: values are in pixels! */
  private final int MIN_TEXT_SIZE = 8;
  private final int MAX_TEXT_SIZE = 100;

  /** Flag for singleLine */
  private boolean mSingleLine = false;

  /**
   * A dummy {@link TextView} to test the text size without actually showing anything to the user
   */
  private TextView mTestView;

  /**
   * A dummy {@link Paint} to test the text size without actually showing anything to the user
   */
  private Paint mTestPaint;

  /**
   * Scaling factor for fonts. It's a method of calculating independently (!) from the actual
   * density of the screen that is used so users have the same experience on different devices. We
   * will use DisplayMetrics in the Constructor to get the value of the factor and then calculate
   * SP from pixel values
   */
  private final float mScaledDensityFactor;

  /**
   * Defines how close we want to be to the factual size of the Text-field. Lower values mean
   * higher precision but also exponentially higher computing cost (more loop runs)
   */
  private final float mThreshold = 0.5f;

  /**
   * Constructor for call without attributes --> invoke constructor with AttributeSet null
   *
   * @param context
   */
  public AutoFitText(Context context) {
    this(context, null);
  }

  @SuppressLint("NewApi")
  public AutoFitText(Context context, AttributeSet attrs) {
    super(context, attrs);

    mScaledDensityFactor = context.getResources().getDisplayMetrics().scaledDensity;
    //Log.i("BeeCount","DENSITY: " + String.valueOf(mScaledDensityFactor));


    mTestView = new TextView(context);

    mTestPaint = new Paint();
    mTestPaint.set(this.getPaint());

    //Log.i("BeeCount","--------");
    this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

      @Override
      public void onGlobalLayout() {
        // make an initial call to onSizeChanged to make sure that refitText is triggered
        onSizeChanged(AutoFitText.this.getWidth(), AutoFitText.this.getHeight(), 0, 0);
        // Remove the LayoutListener immediately so we don't run into an infinite loop
        //Log.i("BeeCount","VERSION: " + String.valueOf(Build.VERSION.SDK_INT));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
        {
          //Log.i("BeeCount","ICS or worse");
          AutoFitText.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        else
        {
          //Log.i("BeeCount","JB or better");
          AutoFitText.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      }
    });
  }

  /**
   * Main method of this widget. Resizes the font so the specified text fits in the text box
   * assuming the text box has the specified width. This is done via a dummy text view that is
   * refit until it matches the real target width and height up to a certain threshold factor
   *
   * @param targetFieldWidth
   *            The width that the TextView currently has and wants filled
   * @param targetFieldHeight
   *            The width that the TextView currently has and wants filled
   */
  private void refitText(String text, int targetFieldWidth, int targetFieldHeight)
  {

    // Variables need to be visible outside the loops for later use. Remember size is in pixels
    float lowerTextSize = MIN_TEXT_SIZE;
    //Log.i("BeeCount","LTS(1): " + String.valueOf(lowerTextSize));
    float upperTextSize = MAX_TEXT_SIZE;

    // Force the text to wrap. In principle this is not necessary since the dummy TextView
    // already does this for us but in rare cases adding this line can prevent flickering
    this.setMaxWidth(targetFieldWidth);

    // Padding should not be an issue since we never define it programmatically in this app
    // but just to to be sure we cut it off here
    targetFieldWidth = targetFieldWidth - this.getPaddingLeft() - this.getPaddingRight();
    targetFieldHeight = targetFieldHeight - this.getPaddingTop() - this.getPaddingBottom();

    // Initialize the dummy with some params (that are largely ignored anyway, but this is
    // mandatory to not get a NullPointerException)
    mTestView.setLayoutParams(new LayoutParams(targetFieldWidth, targetFieldHeight));

    // maxWidth is crucial! Otherwise the text would never line wrap but blow up the width
    mTestView.setMaxWidth(targetFieldWidth);

    // the user requested a single line. This is very easy to do since we primarily need to
    // respect the width, don't have to break, don't have to measure...


    /*************************** Converging algorithm 1 ***********************************/
    for (float testSize; (upperTextSize - lowerTextSize) > mThreshold; )
    {

      // Go to the mean value...
      testSize = (upperTextSize + lowerTextSize) / 2;

      mTestView.setTextSize(TypedValue.COMPLEX_UNIT_SP, testSize / mScaledDensityFactor);
      mTestView.setText(text);
      mTestView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

      if (mTestView.getMeasuredWidth() >= targetFieldWidth)
      {
        upperTextSize = testSize; // Font is too big, decrease upperSize
      }
      else
      {
        lowerTextSize = testSize; // Font is too small, increase lowerSize
        //Log.i("BeeCount","LTS(2): " + String.valueOf(lowerTextSize));
      }
    }
    /**************************************************************************************/

    // In rare cases with very little letters and width > height we have vertical overlap!
    mTestView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

    if (mTestView.getMeasuredHeight() > targetFieldHeight)
    {
      upperTextSize = lowerTextSize;
      //Log.i("BeeCount","LTS(3): " + String.valueOf(lowerTextSize));
      //lowerTextSize = MIN_TEXT_SIZE;
      lowerTextSize =  lowerTextSize * 0.8f;
      //Log.i("BeeCount","LTS(3.1): " + String.valueOf(lowerTextSize));

      /*************************** Converging algorithm 1.5 *****************************/
      for (float testSize; (upperTextSize - lowerTextSize) > mThreshold; )
      {

        // Go to the mean value...
        testSize = (upperTextSize + lowerTextSize) / 2;

        mTestView.setTextSize(TypedValue.COMPLEX_UNIT_SP, testSize / mScaledDensityFactor);
        mTestView.setText(text);
        mTestView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

        if (mTestView.getMeasuredHeight() >= targetFieldHeight)
        {
          upperTextSize = testSize; // Font is too big, decrease upperSize
        }
        else
        {
          lowerTextSize = testSize; // Font is too small, increase lowerSize
          //Log.i("BeeCount","LTS(4): " + String.valueOf(lowerTextSize));
        }
      }
      /**********************************************************************************/
    }
    //Log.i("BeeCount","FONT SIZE: " + String.valueOf(lowerTextSize / mScaledDensityFactor));
    //Log.i("BeeCount","LTS(5): " + String.valueOf(lowerTextSize));
    //Log.i("BeeCount","--------");
    this.setTextSize(TypedValue.COMPLEX_UNIT_SP, lowerTextSize / mScaledDensityFactor);
    return;
  }

  /**
   * This method receives a call upon a change in text content of the TextView. Unfortunately it
   * is also called - among others - upon text size change which means that we MUST NEVER CALL
   * {@link #refitText(String)} from this method! Doing so would result in an endless loop that
   * would ultimately result in a stack overflow and termination of the application
   *
   * So for the time being this method does absolutely nothing. If you want to notify the view of
   * a changed text call {@link #setText(CharSequence)}
   */
  @Override
  protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
    // Super implementation is also intentionally empty so for now we do absolutely nothing here
    super.onTextChanged(text, start, lengthBefore, lengthAfter);
  }

  @Override
  protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    if (width != oldWidth && height != oldHeight) {
      refitText(this.getText().toString(), width, height);
    }
  }

  /**
   * This method is guaranteed to be called by {@link TextView#setText(CharSequence)} immediately.
   * Therefore we can safely add our modifications here and then have the parent class resume its
   * work. So if text has changed you should always call {@link TextView#setText(CharSequence)} or
   * {@link TextView#setText(CharSequence, BufferType)} if you know whether the {@link BufferType}
   * is normal, editable or spannable. Note: the method will default to {@link BufferType#NORMAL}
   * if you don't pass an argument.
   */
  @Override
  public void setText(CharSequence text, BufferType type) {

    int targetFieldWidth = this.getWidth();
    int targetFieldHeight = this.getHeight();

    if (targetFieldWidth <= 0 || targetFieldHeight <= 0 || text.equals("")) {
      // Log.v("tag", "Some values are empty, AutoFitText was not able to construct properly");
    }
    else {
      refitText(text.toString(), targetFieldWidth, targetFieldHeight);
    }
    super.setText(text, type);
  }

  /**
   * TODO add sensibility for {@link #setMaxLines(int)} invocations
   */
  @Override
  public void setMaxLines(int maxLines) {
    // TODO Implement support for this. This could be relatively easy. The idea would probably
    // be to manipulate the targetHeight in the refitText-method and then have the algorithm do
    // its job business as usual. Nonetheless, remember the height will have to be lowered
    // dynamically as the font size shrinks so it won't be a walk in the park still
    if (maxLines == 1) {
      this.setSingleLine(true);
    }
    else {
      throw new UnsupportedOperationException(
          "MaxLines != 1 are not implemented in AutoFitText yet, use TextView instead");
    }
  }

  @Override
  public void setSingleLine(boolean singleLine) {
    // save the requested value in an instance variable to be able to decide later
    mSingleLine = singleLine;
    super.setSingleLine(singleLine);
  }
}

