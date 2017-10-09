package com.swr.matchmatch.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.swr.matchmatch.R;
import com.swr.matchmatch.model.PhotoInfo;
import com.swr.matchmatch.util.UIUtils;

import java.util.List;

/**
 * Created by Steve on 10/7/2017.
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

                steps++;

                int state = holder.getpInfo().getState();

                holder.getpInfo().flipState();

                Picasso.with(holder.getImageView().getContext()).load(info.getUrl()).into(holder.getImageView());

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

    @Override
    public int getItemCount() {
        if(photos != null) return photos.size();
        else return 0;
    }
}
