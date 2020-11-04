package com.excalibur.popupdialogdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class BaseDialog extends DialogFragment {
    private Context context;
    private DialogInterface.OnDismissListener onDismissListener;
    private boolean cancelOnTouchOutside = true;
    private boolean dismissOnBackPress = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    protected abstract void setUp(View view);

    protected abstract int getLayoutId();

    protected abstract ViewGroup getRootViewGroup();

    protected void initAnimation() {
        if (getRootViewGroup() != null)
            animateDialog(getRootViewGroup());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(context) {
            @Override
            public void onBackPressed() {
                if (isDismissOnBackPressed()) {
                    dismissDialog(null);
                }
            }
        };
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (isBottomDialog()) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.BOTTOM;
            }
        }
        configDialog(dialog);
        return dialog;
    }

    public boolean isBottomDialog() {
        return false;
    }

    protected boolean isDismissOnBackPressed() {
        return dismissOnBackPress;
    }

    protected void configDialog(Dialog dialog) {
        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
        initAnimation();
    }

    public void show(View view) {
        show(view.getContext());
    }

    public void show(Context context) {
        show(((AppCompatActivity) context).getSupportFragmentManager(), this.getClass().getSimpleName());
    }

    public void show(FragmentManager fragmentManager, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment prevFragment = fragmentManager.findFragmentByTag(tag);
        if (prevFragment != null) {
            transaction.remove(prevFragment);
        }
        transaction.addToBackStack(null);

        show(transaction, tag);
    }

    public void showAllowingStateLoss(FragmentManager fragmentManager, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment prevFragment = fragmentManager.findFragmentByTag(tag);
        if (prevFragment != null) {
            transaction.remove(prevFragment);
        }
        transaction.addToBackStack(null);
        transaction.add(this, tag);
        transaction.commitAllowingStateLoss();
    }

    public void dismissDialog(String tag) {
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (onDismissListener != null)
            onDismissListener.onDismiss(getDialog());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public DialogInterface.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public boolean isCancelOnTouchOutside() {
        return cancelOnTouchOutside;
    }

    public void setCancelOnTouchOutside(boolean cancelable) {
        this.cancelOnTouchOutside = cancelable;
    }

    public void setDismissOnBackPress(boolean dismissOnBackPress) {
        this.dismissOnBackPress = dismissOnBackPress;
    }

    public void animateDialog(ViewGroup viewGroup) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(viewGroup, ViewGroup.SCALE_X, 0.7f, 1f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(viewGroup, ViewGroup.SCALE_Y, 0.7f, 1f);
        set.playTogether(animatorX, animatorY);
        set.setInterpolator(new BounceInterpolator());
        set.setDuration(500);
        set.start();
    }
}
