package com.example.ungdungbantraicay.Helper;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ungdungbantraicay.R;

import java.io.File;

public class ImageHelper {
    public static void loadFruitImage(Context context, String imageName, ImageView imageView) {
        // Thử tìm trong drawable (ảnh mẫu)
        int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        if (resId != 0) {
            // Nếu tìm thấy trong drawable -> dùng Glide load resource
            Glide.with(context).load(resId).into(imageView);
        } else {
            // Nếu không thấy -> Thử tìm trong bộ nhớ máy (ảnh Admin chụp)
            File file = new File(context.getFilesDir(), imageName);
            Glide.with(context)
                    .load(file)
                    .placeholder(R.drawable.apple) // Ảnh hiện trong lúc đang load
                    .error(R.drawable.apple)       // Ảnh hiện nếu bị lỗi
                    .into(imageView);
        }
    }
}
