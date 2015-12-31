package com.metinkale.prayerapp.hadis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.metinkale.prayer.R;
import com.metinkale.prayerapp.BaseActivity;
import com.metinkale.prayerapp.hadis.SqliteHelper.Hadis;

import java.text.Normalizer;
import java.util.Locale;

public class Frag extends Fragment {

    private static final String NUMBER = "nr";
    private TextView mTv;
    private String mText;
    private String mQuery;

    public static Fragment create(int nr) {
        Frag frag = new Frag();
        Bundle bdl = new Bundle();
        bdl.putInt(NUMBER, nr);
        frag.setArguments(bdl);
        return frag;
    }

    private static String normalize(String str) {
        String string = Normalizer.normalize(str, Normalizer.Form.NFD);
        string = string.replaceAll("[^\\p{ASCII}]", "_");
        return string.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public void setArguments(Bundle bdl) {
        super.setArguments(bdl);
        int nr = bdl.getInt(NUMBER);

        Hadis h = SqliteHelper.get().get(nr);
        if (h.kaynak == null) {
            h.kaynak = "";
        }
        bdl.putString("kaynak", h.kaynak);
        bdl.putString("hadis", h.hadis);
        bdl.putString("detay", h.detay);
        bdl.putString("konu", h.konu);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bdl = getArguments();
        String hadis = bdl.getString("hadis");
        String kaynak = bdl.getString("kaynak");
        String konu = bdl.getString("konu");
        String detay = bdl.getString("detay");
        View v = inflater.inflate(R.layout.hadis_frag, container, false);
        mTv = (TextView) v.findViewById(R.id.hadis);
        mTv.setPadding(mTv.getPaddingLeft(), mTv.getPaddingTop(), mTv.getPaddingRight(), mTv.getPaddingBottom() + ((BaseActivity) getActivity()).getBottomMargin());

        if (hadis.startsWith("Narrated")) {
            hadis = "<b>" + hadis.substring(0, hadis.indexOf("\n")) + "</b><br/>" + hadis.substring(hadis.indexOf("\n"));

        }
        mText = hadis.replace("\n", "<br/>") + (kaynak.length() <= 3 ? "" : "<br/><br/>" + kaynak);
        mTv.setText(Html.fromHtml(mText));
        TextView category = (TextView) v.findViewById(R.id.category);
        category.setText(Html.fromHtml(konu));
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(Html.fromHtml(detay));

        setQuery(mQuery);
        return v;
    }

    public void setQuery(String query) {
        if (query == null || mText == null) {
            mQuery = query;
            return;
        }
        if (query == "") {
            mTv.setText(Html.fromHtml(mText));
        } else {
            query = normalize(query);

            StringBuilder st = new StringBuilder(mText);
            String normalized = mText;
            int i = normalized.indexOf(normalize(query));
            int p = 0;
            while (i >= 0) {
                st.insert(i + p, "<b>");
                p += 3;
                st.insert(i + query.length() + p, "</b>");
                p += 4;
                i = normalized.indexOf(normalize(query), i + 1);
            }
            mTv.setText(Html.fromHtml(st.toString()));
        }

    }
}
