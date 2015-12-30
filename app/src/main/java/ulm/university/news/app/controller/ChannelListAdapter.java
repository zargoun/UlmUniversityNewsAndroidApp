package ulm.university.news.app.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ulm.university.news.app.R;
import ulm.university.news.app.data.Channel;
import ulm.university.news.app.data.Lecture;

/**
 * TODO
 *
 * @author Matthias Mak
 */
public class ChannelListAdapter extends ArrayAdapter<Channel> {
    public ChannelListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ChannelListAdapter(Context context, int resource, List<Channel> channels) {
        super(context, resource, channels);
    }

    /**
     * Updates the data of the ChannelListAdapter.
     *
     * @param data The updated channel list.
     */
    public void setData(List<Channel> data) {
        clear();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                add(data.get(i));
            }
        }
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.channel_list_item, parent, false);
            // convertView = vi.inflate(R.layout.channel_list_item, parent, false);
        }

        Channel channel = getItem(position);

        if (channel != null) {
            TextView tvName = (TextView) convertView.findViewById(R.id.channel_list_item_tv_name);
            TextView tvType = (TextView) convertView.findViewById(R.id.channel_list_item_tv_type);
            TextView tvTerm = (TextView) convertView.findViewById(R.id.channel_list_item_tv_term);
            TextView tvIcon = (TextView) convertView.findViewById(R.id.channel_list_item_tv_icon);

            tvName.setText(channel.getName());
            tvType.setText(channel.getType().toString());
            tvTerm.setText(channel.getTerm());

            // Set appropriate channel icon.
            switch (channel.getType()) {
                case LECTURE:
                    tvIcon.setText(getContext().getString(R.string.lecture_symbol));
                    Lecture lecture = (Lecture) channel;
                    // Set icon with appropriate faculty color.
                    switch (lecture.getFaculty()) {
                        case ENGINEERING_COMPUTER_SCIENCE_PSYCHOLOGY:
                            tvIcon.setBackgroundResource(R.drawable.circle_informatics);
                            break;
                        case MATHEMATICS_ECONOMICS:
                            tvIcon.setBackgroundResource(R.drawable.circle_mathematics);
                            break;
                        case MEDICINES:
                            tvIcon.setBackgroundResource(R.drawable.circle_medicines);
                            break;
                        case NATURAL_SCIENCES:
                            tvIcon.setBackgroundResource(R.drawable.circle_science);
                            break;
                    }
                    break;
                case EVENT:
                    tvIcon.setText(getContext().getString(R.string.event_symbol));
                    tvIcon.setBackgroundResource(R.drawable.circle_main);
                    break;
                case SPORTS:
                    tvIcon.setText(getContext().getString(R.string.sports_symbol));
                    tvIcon.setBackgroundResource(R.drawable.circle_main);
                    break;
                case STUDENT_GROUP:
                    tvIcon.setText(getContext().getString(R.string.student_group_symbol));
                    tvIcon.setBackgroundResource(R.drawable.circle_main);
                    break;
                case OTHER:
                    tvIcon.setText(getContext().getString(R.string.other_symbol));
                    tvIcon.setBackgroundResource(R.drawable.circle_main);
                    break;
            }
        }
        return convertView;
    }
}
