package com.UniqueBulleteSolutions.whatsapp.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.UniqueBulleteSolutions.whatsapp.fragment.CallFragment;
import com.UniqueBulleteSolutions.whatsapp.fragment.ChatFragment;
import com.UniqueBulleteSolutions.whatsapp.fragment.StatusFragment;

public class FragmentAdapter extends FragmentPagerAdapter {
    String CUID;
    int tabCount;

    public FragmentAdapter(@NonNull FragmentManager fm, int behavior, String CUID) {
        super(fm, behavior);
        this.CUID = CUID;
        tabCount = behavior;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatFragment();
            case 1:
                return new StatusFragment();
            case 2:
                return new CallFragment();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        String title = null;
//        if (position == 0) {
//            title = "CHATS";
//        } else if (position == 1) {
//            title = "STATUS";
//        } else if (position == 2) {
//            title = "CALL";
//        }
//        return title;
//    }
}
