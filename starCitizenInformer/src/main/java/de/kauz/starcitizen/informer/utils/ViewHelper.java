package de.kauz.starcitizen.informer.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * Class for convenient methods of manipulating views.
 * 
 * @author MadKauz
 * 
 */
public class ViewHelper {

	/**
	 * Shows the specified view if invisible or gone.
	 * 
	 * @param v
	 *            the view to show
	 */
	public static void showView(View v) {
		if (v.getVisibility() == View.INVISIBLE
				|| v.getVisibility() == View.GONE) {
			v.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Hides the specified view if visible.
	 * 
	 * @param v
	 *            the view to hide
	 */
	public static void hideView(View v) {
		if (v.getVisibility() == View.VISIBLE) {
			v.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Lets a specified view be gone
	 * 
	 * @param v
	 *            the view to let go
	 */
	public static void goneView(View v) {
		if (v.getVisibility() == View.VISIBLE) {
			v.setVisibility(View.GONE);
		}
	}

	/**
	 * Stars a loading animation for the specified view.
	 * 
	 * @param v
	 *            the view to animate
	 */
	public static void animateLoading(View v) {
		showView(v);
		AlphaAnimation loadingAnim = new AlphaAnimation(1F, 0.0F);
		loadingAnim.setDuration(800);
		loadingAnim.setFillAfter(false);
		loadingAnim.setRepeatMode(Animation.REVERSE);
		loadingAnim.setRepeatCount(Animation.INFINITE);
		v.startAnimation(loadingAnim);
	}

	/**
	 * Stops animation of the specified view.
	 * 
	 * @param v
	 *            the view
	 */
	public static void stopAnimatingLoading(View v) {
		v.clearAnimation();
		v.setVisibility(View.GONE);
	}

	/**
	 * Fades the specified view in.
	 * 
	 * @param v
	 *            the view to fade in
	 */
	public static void fadeIn(View v) {
		showView(v);
		AlphaAnimation fadeIn = new AlphaAnimation(0F, 1F);
		fadeIn.setDuration(1000);
		fadeIn.setFillAfter(false);
		v.startAnimation(fadeIn);

	}
	
	/**
	 * Fades the specified view in slowly.
	 * 
	 * @param v
	 *            the view to fade in
	 */
	public static void progressfadeIn(View v) {
		showView(v);
		
		AnimationSet set = new AnimationSet(false);
		
		ScaleAnimation scaleIn = new ScaleAnimation(0.1f, 1, 0.1f, 1,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleIn.setFillAfter(true);
		scaleIn.setStartOffset(500);
		scaleIn.setDuration(500);
		scaleIn.setInterpolator(new OvershootInterpolator());
		
	
		AlphaAnimation fadeIn = new AlphaAnimation(0F, 1F);
		fadeIn.setDuration(2000);
		fadeIn.setFillAfter(false);
		
		set.addAnimation(scaleIn);
		set.addAnimation(fadeIn);
		v.startAnimation(set);

	}
	
	/**
	 * Fades the specified view out.
	 * 
	 * @param v
	 *            the view to fade out
	 */
	public static void fadeOut(final View v) {
		if (v.getVisibility() == View.VISIBLE) {
			AlphaAnimation fadeOut = new AlphaAnimation(1F, 0F);
		
			fadeOut.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					v.setVisibility(View.INVISIBLE);
					
				}
			});
			
			fadeOut.setDuration(500);
			fadeOut.setFillAfter(true);
			v.startAnimation(fadeOut);
		}
	}

	/**
	 * Animates a coming from left to right of a specivied view
	 * 
	 * @param v
	 *            the view to animate.
	 */
	public static void animateTopToDown(View v) {

		v.setVisibility(View.VISIBLE);

		ScaleAnimation animation = new ScaleAnimation(1, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF,
				Animation.RELATIVE_TO_SELF, 0);
		animation.setFillAfter(true);
		animation.setDuration(500);
		animation.setInterpolator(new AccelerateInterpolator());

		v.startAnimation(animation);
	}

	/**
	 * Converts pixels to dp.
	 * 
	 * @param context
	 * @param px
	 *            the pixels to apply
	 * @return the pixels in dp conversion
	 */
	public static int convertToDP(Context context, int px) {
		return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				px, context.getResources().getDisplayMetrics()));
	}

	/**
	 * Retrieves the display width in pixels.
	 * 
	 * @param context
	 * @return the widht in pixels
	 */
	public static int getDisplayWidth(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.widthPixels;
	}

	/**
	 * Retrieves the display height in pixels.
	 * 
	 * @param context
	 * @return the height in pixels
	 */
	public static int getDisplayHeight(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.heightPixels;
	}

	/**
	 * Animates the specified view
	 * 
	 * @param v
	 *            the view to animate
	 */
	public static void animateFromBottomToTop(View v) {
		if (v.getVisibility() != View.VISIBLE) {
			v.setVisibility(View.VISIBLE);

			ScaleAnimation animation = new ScaleAnimation(1, 1, 0, 1,
					Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF,
					Animation.RELATIVE_TO_SELF, 1);
			animation.setFillAfter(false);
			animation.setDuration(500);
			animation.setInterpolator(new AccelerateInterpolator());

			v.startAnimation(animation);
		}
	}

	/**
	 * Animates the specified view
	 * 
	 * @param v
	 *            the view to animate
	 */
	public static void animateFromTopToBottom(final View v) {
		if (v.getVisibility() == View.VISIBLE) {

			ScaleAnimation animation = new ScaleAnimation(1, 1, 1, 0,
					Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF,
					Animation.RELATIVE_TO_SELF, 1);
			animation.setFillAfter(false);
			animation.setDuration(500);
			animation.setInterpolator(new AccelerateInterpolator());
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					v.setVisibility(View.INVISIBLE);
				}
			});

			v.startAnimation(animation);
		}
	}

}
