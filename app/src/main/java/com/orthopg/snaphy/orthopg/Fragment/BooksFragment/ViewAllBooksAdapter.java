package com.orthopg.snaphy.orthopg.Fragment.BooksFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orthopg.snaphy.orthopg.MainActivity;
import com.orthopg.snaphy.orthopg.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nikita on 8/3/17.
 */

public class ViewAllBooksAdapter extends RecyclerView.Adapter<ViewAllBooksAdapter.ViewHolder> {

    MainActivity mainActivity;
    List<BookListModel> bookListModelList;

    public ViewAllBooksAdapter(MainActivity mainActivity, List<BookListModel> bookListModelList){

        this.mainActivity = mainActivity;
        this.bookListModelList = bookListModelList;
    }


    @Override
    public ViewAllBooksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewBookView = inflater.inflate(R.layout.layout_view_all_books,parent,false);
        ViewAllBooksAdapter.ViewHolder viewHolder = new ViewAllBooksAdapter.ViewHolder(viewBookView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BookListModel bookListModel = bookListModelList.get(position);

        TextView bookName = holder.bookName;
        ImageView bookCover = holder.bookCover;

        if(bookListModel!=null){

            if(bookListModel.getName()!=null){
                if(!bookListModel.getName().isEmpty()){
                    bookName.setText(bookListModel.getName().toString());
                }
            }

            if(bookListModel.getDrawable()!=null){
                bookCover.setImageDrawable(bookListModel.getDrawable());
            }
        }
    }

    @Override
    public int getItemCount() {
        return bookListModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.layout_books_list_textview1) TextView bookName;
        @Bind(R.id.layout_books_list_imageview1) ImageView bookCover;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}