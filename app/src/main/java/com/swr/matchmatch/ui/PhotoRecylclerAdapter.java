package com.swr.matchmatch.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.swr.matchmatch.R;
import com.swr.matchmatch.Rotate3DAnimation;
import com.swr.matchmatch.model.PhotoInfo;
import com.swr.matchmatch.util.UIUtils;

import java.util.List;

/**
 * Created by Steve Rock, SWR Technologies, LLC  on 10/6/2017.
 *
 * MIT License

 Copyright (c) [2017] Steven William Rock, SWR Technologies, LLC

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */
public class PhotoRecylclerAdapter extends RecyclerView.Adapter<PhotoInfoHolder> {

    public final static String TAG = PhotoRecylclerAdapter.class.getSimpleName();;
    private List<PhotoInfo> photos;

    private PhotoInfoHolder lastHolder;
    private PhotoInfoHolder currentHolder;
    private int steps = 0;
    private int matchedCount = 0;
    private int currentlySelected = 0;
    private int nextSelected = 0;

    private StartNextRotate startNext;
    private Rotate3DAnimation rotation;

    private ImageView image;
    private String imageUrl;


    public PhotoRecylclerAdapter(List<PhotoInfo> photos){
        this.photos = photos;
    }

    @Override
    public PhotoInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.grid_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        PhotoInfoHolder vh = new PhotoInfoHolder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(final PhotoInfoHolder holder, int position) {

        final PhotoInfo info = photos.get(position);
        holder.setData(info, position, holder.getImageView().getContext());

        Picasso.with(holder.getImageView().getContext()).load( holder.getImageView().getContext().getResources().getString(R.string.CARD_BACK_SIDE)).into(holder.getImageView());

        holder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log here
                Log.d(TAG, "Showing photo for " + info.getTitle());
                Log.d(TAG, "Photo info =  " + holder.getpInfo());


                steps++;

                int state = holder.getpInfo().getState();

                holder.getpInfo().flipState();

                image = holder.getImageView();

                //Picasso.with(image.getContext()).load(info.getUrl()).into(image);
                imageUrl =  info.getUrl();

                startRotation(0,90, image, true);

                // First card selection
                if(currentlySelected == 0) {
                    currentlySelected = info.getPhotoNum();
                    lastHolder = holder;

                    nextSelected = 0;
                    currentHolder = null;

                }
                else{  // Second card selection
                    currentHolder = holder;
                    nextSelected = info.getPhotoNum();
                }

                if(nextSelected == currentlySelected){  // 2 cards selected - Found Match
                    // found match - do nothing keep open
                    currentlySelected = 0;
                    nextSelected = 0;

                    currentHolder = null;
                    lastHolder = null;

                    matchedCount++;
                    Log.d(TAG, "mathch count = " + matchedCount);

                }
                else if (nextSelected > 0 && currentlySelected > 0){  // 2 cards selected - No match
                    // flip back over on a timer
                    currentlySelected = 0;
                    nextSelected = 0;

                    CloseImagesAsynchTask aTask = new CloseImagesAsynchTask();
                    aTask.setCurrentHolder(currentHolder);
                    aTask.setLastHolder(lastHolder);
                    aTask.execute();

                    currentHolder = null;
                    lastHolder = null;
                }
                if(matchedCount == 8){
                    // game over
                    UIUtils.showSnackBar("Congratulations, you finished the game in " + steps + " steps!", holder.getImageView());
                }

            }

        });

    }

    public void add(int position, PhotoInfo item) {
        photos.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        photos.remove(position);
        notifyItemRemoved(position);
    }

    private void startRotation(float start, float end, ImageView image, boolean first) {
// Calculating center point
        final float centerX = image.getWidth() / 2.0f;
        final float centerY = image.getHeight() / 2.0f;
        Log.d(TAG, "centerX="+centerX+", centerY="+centerY);

        rotation =new Rotate3DAnimation(start, end, centerX, centerY, 0f, true);
        rotation.setDuration(300);
        rotation.setFillAfter(true);

        rotation.setInterpolator(new LinearInterpolator());

        startNext = new StartNextRotate();
        startNext.setFirst(first);
        rotation.setAnimationListener(startNext);

        image.startAnimation(rotation);
    }


    @Override
    public int getItemCount() {
        if(photos != null) return photos.size();
        else return 0;
    }

    private class StartNextRotate implements Animation.AnimationListener {

        private boolean first = false;

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        public void onAnimationEnd(Animation animation) {

            Log.d(TAG, "onAnimationEnd......");
            //image.startAnimation(rotation);

            Picasso.with(image.getContext()).load(imageUrl).into(image);

            if(first) {
                first = false;
                startRotation(90, 180, image, first);
            }

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
