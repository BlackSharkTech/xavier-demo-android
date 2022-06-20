package com.blacksharktech.xavier;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact, container, false);

        SpannableString ss = new SpannableString(getString(R.string.email));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                composeEmail("info@blacksharktech.com", "Question about Xavier MRZ");
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), android.R.color.holo_blue_dark));
            }
        };
        ss.setSpan(clickableSpan, 53, 102-26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView email = v.findViewById(R.id.email);
        email.setText(ss);
        email.setMovementMethod(LinkMovementMethod.getInstance());
        email.setHighlightColor(Color.TRANSPARENT);

        TextView rate = v.findViewById(R.id.rate);
        rate.setMovementMethod(LinkMovementMethod.getInstance());

        Button b = v.findViewById(R.id.rateButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "market://details?id=com.blacksharktech.xavier";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        return v;
    }

    /**
     * Composes an email to send to info@blackshark.com
     * @param emailAddress
     * @param subject
     */
    private void composeEmail(String emailAddress, String subject) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        StringBuilder body = new StringBuilder();

        body.append("I am interested in purchasing a Xavier license. \n\n");
        body.append("Android application ID: \n\n\n");
        body.append("iOS bundle ID: \n\n\n");
        body.append("Company Name: \n\n\n");
        body.append("Company Address (optional): \n\n\n");
        body.append("Point of Contact Name: \n\n\n");
        body.append("Point of Contact Phone Number: \n\n\n");
        body.append("Point of Contact Email: \n\n\n");

        // sendIntent.setType("text/plain");
        sendIntent.setType("message/rfc822");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
        sendIntent.setType("message/rfc822");
        startActivity(sendIntent);
    }

}
