package fr.smeal.ui.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PhotoEditorView extends View {

    private Bitmap baseBitmap;
    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private final List<Sticker> stickers = new ArrayList<>();
    private Sticker selectedSticker = null;
    private float lastX, lastY;

    // Intensités des filtres
    private float saturation = 1f;
    private float contrast = 1f;

    public PhotoEditorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBaseBitmap(Bitmap bitmap) {
        this.baseBitmap = bitmap;
        invalidate();
    }

    public void setSaturation(float value) {
        this.saturation = value;
        updateFilters();
    }

    public void setContrast(float value) {
        this.contrast = value;
        updateFilters();
    }

    private void updateFilters() {
        ColorMatrix cmSaturation = new ColorMatrix();
        cmSaturation.setSaturation(saturation);

        float scale = contrast;
        float translate = (-.5f * scale + .5f) * 255f;
        ColorMatrix cmContrast = new ColorMatrix(new float[] {
                scale, 0, 0, 0, translate,
                0, scale, 0, 0, translate,
                0, 0, scale, 0, translate,
                0, 0, 0, 1, 0
        });

        cmSaturation.postConcat(cmContrast);
        paint.setColorFilter(new ColorMatrixColorFilter(cmSaturation));
        invalidate();
    }

    public void addSticker(Bitmap stickerBitmap) {
        stickers.add(new Sticker(stickerBitmap, getWidth() / 2f, getHeight() / 2f));
        invalidate();
    }

    // Méthode pour générer le bitmap final avec les modifs (Canvas)
    public Bitmap getFinalBitmap() {
        if (baseBitmap == null) return null;
        Bitmap result = Bitmap.createBitmap(baseBitmap.getWidth(), baseBitmap.getHeight(), baseBitmap.getConfig());
        Canvas canvas = new Canvas(result);
        
        // Dessiner la photo avec filtres
        Rect src = new Rect(0, 0, baseBitmap.getWidth(), baseBitmap.getHeight());
        RectF dst = new RectF(0, 0, baseBitmap.getWidth(), baseBitmap.getHeight());
        canvas.drawBitmap(baseBitmap, src, dst, paint);

        // Dessiner les stickers (mise à l'échelle selon la taille réelle de l'image)
        float scaleX = (float) baseBitmap.getWidth() / getWidth();
        float scaleY = (float) baseBitmap.getHeight() / getHeight();
        
        for (Sticker sticker : stickers) {
            canvas.drawBitmap(sticker.bitmap, 
                (sticker.x * scaleX) - (sticker.bitmap.getWidth() / 2f), 
                (sticker.y * scaleY) - (sticker.bitmap.getHeight() / 2f), null);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (baseBitmap == null) return;

        Rect src = new Rect(0, 0, baseBitmap.getWidth(), baseBitmap.getHeight());
        RectF dst = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawBitmap(baseBitmap, src, dst, paint);

        for (Sticker sticker : stickers) {
            canvas.drawBitmap(sticker.bitmap, sticker.x - sticker.bitmap.getWidth() / 2f, 
                             sticker.y - sticker.bitmap.getHeight() / 2f, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = stickers.size() - 1; i >= 0; i--) {
                    Sticker s = stickers.get(i);
                    if (x >= s.x - s.bitmap.getWidth() / 2f && x <= s.x + s.bitmap.getWidth() / 2f &&
                        y >= s.y - s.bitmap.getHeight() / 2f && y <= s.y + s.bitmap.getHeight() / 2f) {
                        selectedSticker = s;
                        lastX = x;
                        lastY = y;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (selectedSticker != null) {
                    selectedSticker.x += (x - lastX);
                    selectedSticker.y += (y - lastY);
                    lastX = x;
                    lastY = y;
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                selectedSticker = null;
                break;
        }
        return super.onTouchEvent(event);
    }

    private static class Sticker {
        Bitmap bitmap;
        float x, y;

        Sticker(Bitmap bitmap, float x, float y) {
            this.bitmap = bitmap;
            this.x = x;
            this.y = y;
        }
    }
}