package com.sanron.library.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.sanron.library.R;
import com.sanron.library.model.Lyric;


/**
 * Created by sanron on 16-5-13.
 */
public class LyricView extends View {

    /**
     * 歌词上下间距
     */
    private int mSentenceMargin;
    private Paint mCurrentPaint;
    private Paint mNormalPaint;
    private int mCenterX;
    private int mCenterY;
    private Rect mClipBounds;
    private Lyric mLyric;
    private int mCurrentSentenceIndex = 1;

    public LyricView(Context context) {
        this(context, null);
    }

    public LyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.LyricView);
        Resources resources = getResources();
        final int DEFAULT_NORMAL_TEXT_SIZE = (int) resources.getDimension(R.dimen.default_sentence_text_size);
        final int DEFAULT_CURRENT_TEXT_SIZE = (int) resources.getDimension(R.dimen.default_current_sentence_text_size);
        final int DEFAULT_NORMAL_TEXT_COLOR = resources.getColor(R.color.default_sentence_text_color);
        final int DEFAULT_CURRENT_TEXT_COLOR = resources.getColor(R.color.default_current_sentence_text_color);
        final int DEFAULT_SENTENCE_MARGIN = (int) resources.getDimension(R.dimen.default_sentence_margin);

        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);
        mCurrentPaint.setTextSize(ta.getDimension(R.styleable.LyricView_currentTextSize, DEFAULT_CURRENT_TEXT_SIZE));
        mCurrentPaint.setColor(ta.getColor(R.styleable.LyricView_currentTextColor, DEFAULT_CURRENT_TEXT_COLOR));
        mCurrentPaint.setTextAlign(Paint.Align.CENTER);

        mNormalPaint = new Paint();
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setTextSize(ta.getDimension(R.styleable.LyricView_textSize, DEFAULT_NORMAL_TEXT_SIZE));
        mNormalPaint.setColor(ta.getColor(R.styleable.LyricView_textColor, DEFAULT_NORMAL_TEXT_COLOR));
        mNormalPaint.setTextAlign(Paint.Align.CENTER);

        mSentenceMargin = (int) ta.getDimension(R.styleable.LyricView_sentenceMargin, DEFAULT_SENTENCE_MARGIN);


    }

    public void setCurrentSentenceIndex(int index) {
        if (isEmpty()) {
            return;
        }

        mCurrentSentenceIndex = index;
        postInvalidate();
    }

    public boolean isEmpty() {
        return mLyric == null
                || mLyric.sentences == null
                || mLyric.sentences.size() == 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isEmpty()) {
            return;
        }

        canvas.save();
        canvas.clipRect(mClipBounds);
        int curY = 0;
        //先画当前的歌词
        int currentTotalHeight = 0;
        if (mCurrentSentenceIndex > -1) {
            currentTotalHeight = drawSentence(mLyric.sentences.get(mCurrentSentenceIndex).content,
                    canvas, mCurrentPaint, -1, true);
        } else {
            Paint.FontMetricsInt fontMetricsInt = mCurrentPaint.getFontMetricsInt();
            currentTotalHeight = fontMetricsInt.bottom - fontMetricsInt.top;
        }

        //当前歌词之前的歌词
        curY = mCenterY - currentTotalHeight / 2 - mSentenceMargin;
        for (int i = mCurrentSentenceIndex - 1; i >= 0; i--) {
            if (curY > mClipBounds.top) {
                String sentence = mLyric.sentences.get(i).content;
                curY -= drawSentence(sentence, canvas, mNormalPaint, curY, false) + mSentenceMargin;
            }
        }

        //当前歌词之后的歌词
        curY = mCenterY + currentTotalHeight / 2 + mSentenceMargin;
        for (int i = mCurrentSentenceIndex + 1; i < mLyric.sentences.size(); i++) {
            if (curY < mClipBounds.bottom) {
                String sentence = mLyric.sentences.get(i).content;
                curY += drawSentence(sentence, canvas, mNormalPaint, curY, true) + mSentenceMargin;
            }
        }
        canvas.restore();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = (w - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
        mCenterY = (h - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();
        mClipBounds = new Rect(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
    }

    private int drawSentence(String sentence, Canvas canvas, Paint paint, int startY, boolean after) {
        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        final int textHeight = fontMetricsInt.bottom - fontMetricsInt.top;
        if (TextUtils.isEmpty(sentence)) {
            return textHeight;
        }
        float textWidth = paint.measureText(sentence);
        int lineLength = sentence.length();
        int totalHeight = textHeight;
        int line = 1;
        if (textWidth > mClipBounds.width()) {
            //计算每行的字符串长度
            lineLength = (int) Math.min(sentence.length() * mClipBounds.width() / textWidth, sentence.length() - 1);
            line = (int) Math.ceil(sentence.length() / (float) lineLength);
            totalHeight = (line * textHeight + (line - 1) * mSentenceMargin);
        }
        int startIndex = 0;
        if (startY == -1) {
            startY = mCenterY - totalHeight / 2;
        }
        if (!after) {
            startY -= totalHeight;
        }
        int i = 0;
        int offsetY = textHeight + mSentenceMargin;
        while (i++ < line) {
            int end = Math.min(startIndex + lineLength, sentence.length());
            String s = sentence.substring(startIndex, end);
            canvas.drawText(s, mCenterX, startY - fontMetricsInt.top, paint);
            startY += offsetY;
            startIndex = end;
        }
        return totalHeight;
    }


    public Lyric getLyric() {
        return mLyric;
    }

    public void setLyric(Lyric lyric) {
        mLyric = lyric;
    }
}
