package com.example.planningmeeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class SwipeAdapter extends PagerAdapter {
    private Context context;

    public SwipeAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        if (position == 0) {
            // Используем макет для activity_main
            view = inflater.inflate(R.layout.activity_main, container, false);
            container.addView(view);
        } else if (position == 1) {
            // Используем макет для activity_notes
            view = inflater.inflate(R.layout.activity_notes, container, false);
            container.addView(view);
            // Здесь вы можете установить обработчик нажатия на кнопку или выполнить другие операции, связанные с activity_notes
        } else {
            return null;
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
