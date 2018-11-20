package com.qingyun.download.template.application;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Ensures that Glide's generated API is created for the Gallery sample.
 */
@GlideModule(glideName = "ImageGlide")
public final class QYGlideAppModule extends AppGlideModule {
    // Intentionally empty.
}
