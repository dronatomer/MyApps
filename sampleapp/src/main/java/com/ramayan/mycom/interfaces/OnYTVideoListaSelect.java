package com.ramayan.mycom.interfaces;




import com.ramayan.mycom.Model.Videos;

import java.util.ArrayList;

/**
 * Created by smedic on 5.3.17..
 */

public interface OnYTVideoListaSelect {

    void onVideoSelected(ArrayList<Videos> videoList, int position);

}
