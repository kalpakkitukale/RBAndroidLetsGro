package com.ramanbyte.utilities;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ramanbyte.R;

/**
 * @author  Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 26/02/2020
 */
public class ImageSetter {

    @BindingAdapter(value = {"app:imageUrl", "app:placeHolder"}, requireAll = false)
    public static void loadImage(ImageView imageView, String url, Drawable drawable) {
        if (!TextUtils.isEmpty(url))
            GlideApp.with(imageView.getContext())
                    .load(url)
                    /*.override((int) BindingUtils.dimen(R.dimen.dp_40), (int) BindingUtils.dimen(R.dimen.dp_40))*/
                    .placeholder(drawable)
                    /*.diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)*/
                    .dontAnimate()
                    .into(imageView);

    }

    /*@BindingAdapter(value = {"app:imageUrl", "app:placeHolder", "app:progressBar"}, requireAll = false)
    public static void loadImage(final ImageView imageView, String url, final Drawable drawable, final ProgressBar progressBar ) {
        if (!TextUtils.isEmpty(url))
            GlideApp.with(imageView.getContext())
                    .load(url)
                    .override((int) BindingUtils.dimen(R.dimen.dp_40), (int) BindingUtils.dimen(R.dimen.dp_40))
                    .placeholder(drawable)
                    .listener(new RequestListener<Drawable>(){
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (progressBar!=null) progressBar.setVisibility(View.GONE);
                            assert e != null;
                            e.printStackTrace();
                            AppLog.INSTANCE.errorLog(e.getMessage(),e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (progressBar!=null) progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
    }*/
}
